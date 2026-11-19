package mazequest.engine;

import java.util.Objects;
import mazequest.model.*;
import mazequest.structures.*;

/** Motor determinista por turnos, independiente de Swing y probado sin ventanas. */
public final class GameEngine {
  private final Maze maze;
  private final Enemy[] enemies;
  private CustomStack<GameSnapshot> undo;
  private DoublyLinkedList<MoveRecord> history;
  private Position player;
  private int lives;
  private int moves;
  private GameStatus status;

  /**
   * Construye la partida y copia patrullas a listas circulares.
   *
   * @param maze mapa validado
   * @throws NullPointerException si maze es nulo
   */
  public GameEngine(Maze maze) {
    this.maze = Objects.requireNonNull(maze);
    enemies = new Enemy[maze.enemyCount()];
    for (int i = 0; i < enemies.length; i++) enemies[i] = new Enemy(maze.route(i));
    restart();
  }

  /** Reinicia toda la sesión lógica; descarta pila e historial. */
  public void restart() {
    player = maze.start();
    lives = 3;
    moves = 0;
    status = GameStatus.PLAYING;
    undo = new CustomStack<>();
    history = new DoublyLinkedList<>();
    for (Enemy e : enemies) e.restore(0);
  }

  /**
   * Crea una copia reversible O(enemigos).
   *
   * @return instantánea independiente
   */
  private GameSnapshot snapshot() {
    int[] indices = new int[enemies.length];
    for (int i = 0; i < enemies.length; i++) indices[i] = enemies[i].index();
    return new GameSnapshot(player, lives, moves, status, indices);
  }

  /**
   * Ejecuta un paso ortogonal, avanza enemigos y resuelve una única pérdida de vida.
   *
   * @param dr delta fila
   * @param dc delta columna
   * @return true si consumió un turno
   */
  public boolean move(int dr, int dc) {
    // También rechaza diagonales y magnitudes enormes sin sumar coordenadas.
    if (status != GameStatus.PLAYING
        || !((dr == 0 && (dc == 1 || dc == -1)) || (dc == 0 && (dr == 1 || dr == -1))))
      return false;
    Position target = player.step(dr, dc);
    if (!maze.walkable(target)) return false;
    undo.apilar(snapshot());
    Position from = player;
    boolean hit = false;
    for (Enemy e : enemies) {
      if (e.position().equals(target)) hit = true;
      e.advance();
      if (e.position().equals(target)) hit = true;
    }
    player = target;
    moves++;
    if (hit) {
      lives--;
      player = maze.start();
      for (Enemy e : enemies) e.restore(0);
      if (lives == 0) status = GameStatus.LOST;
    } else if (player.equals(maze.exit())) status = GameStatus.WON;
    history.addLast(new MoveRecord(moves, from, target, player, hit));
    return true;
  }

  /**
   * Restaura todo el turno y retira su historial.
   *
   * @return false si no hay estados
   */
  public boolean undo() {
    if (undo.esVacia()) return false;
    GameSnapshot s = undo.desapilar();
    player = s.player();
    lives = s.lives();
    moves = s.moves();
    status = s.status();
    int[] indices = s.enemies();
    for (int i = 0; i < enemies.length; i++) enemies[i].restore(indices[i]);
    history.remove(history.size() - 1);
    return true;
  }

  /**
   * Mapa inmutable.
   *
   * @return mapa
   */
  public Maze maze() {
    return maze;
  }

  /**
   * Posición actual.
   *
   * @return jugador
   */
  public Position player() {
    return player;
  }

  /**
   * Vidas actuales.
   *
   * @return 0..3
   */
  public int lives() {
    return lives;
  }

  /**
   * Turnos actuales.
   *
   * @return cantidad
   */
  public int moves() {
    return moves;
  }

  /**
   * Estado.
   *
   * @return estado lógico
   */
  public GameStatus status() {
    return status;
  }

  /**
   * Tamaño de pila.
   *
   * @return estados disponibles
   */
  public int undoSize() {
    return undo.tamano();
  }

  /**
   * Tamaño de historial.
   *
   * @return entradas
   */
  public int historySize() {
    return history.size();
  }

  /**
   * Consulta bidireccional por índice.
   *
   * @param index entrada entre 0 (incluido) y historySize() (excluido)
   * @return registro
   * @throws IndexOutOfBoundsException si index no identifica una entrada existente
   */
  public MoveRecord historyAt(int index) {
    return history.get(index);
  }

  /**
   * Datos del historial recorriendo sus enlaces.
   *
   * @param reverse dirección
   * @return texto
   */
  public String historyText(boolean reverse) {
    StringBuilder b = new StringBuilder();
    if (reverse) history.forEachReverse(r -> b.append(r).append('\n'));
    else history.forEach(r -> b.append(r).append('\n'));
    return b.toString();
  }

  /**
   * Inspección de la pila real.
   *
   * @return estados desde el tope
   */
  public String stackText() {
    StringBuilder b = new StringBuilder();
    undo.forEach(
        s ->
            b.append("T")
                .append(s.moves())
                .append(" ")
                .append(s.player())
                .append(" vidas=")
                .append(s.lives())
                .append('\n'));
    return b.toString();
  }

  /**
   * Número de enemigos.
   *
   * @return cantidad
   */
  public int enemyCount() {
    return enemies.length;
  }

  /**
   * Consulta posición sin exponer enemigo mutable.
   *
   * @param index enemigo entre 0 (incluido) y enemyCount() (excluido)
   * @return posición
   * @throws IndexOutOfBoundsException si index no identifica un enemigo existente
   */
  public Position enemyPosition(int index) {
    return enemies[index].position();
  }

  /**
   * Consulta cursor.
   *
   * @param index enemigo entre 0 (incluido) y enemyCount() (excluido)
   * @return índice
   * @throws IndexOutOfBoundsException si index no identifica un enemigo existente
   */
  public int enemyIndex(int index) {
    return enemies[index].index();
  }

  /**
   * Inspección de lista circular real.
   *
   * @param index enemigo entre 0 (incluido) y enemyCount() (excluido)
   * @return ruta
   * @throws IndexOutOfBoundsException si index no identifica un enemigo existente
   */
  public String patrolText(int index) {
    return enemies[index].routeText();
  }
}
