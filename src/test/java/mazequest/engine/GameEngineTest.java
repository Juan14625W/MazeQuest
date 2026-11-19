package mazequest.engine;

import static org.junit.jupiter.api.Assertions.*;

import mazequest.algorithms.BreadthFirstSearch;
import mazequest.model.*;
import org.junit.jupiter.api.Test;

/** Casos verificables de turnos, daños y restauración integral. */
class GameEngineTest {
  /** Constructor para JUnit. */
  GameEngineTest() {}

  /**
   * Fixture de pasillo; el enemigo impide entrar en su posición previa.
   *
   * @return motor
   */
  private GameEngine collisionGame() {
    return new GameEngine(
        new Maze(
            "Prueba",
            new String[] {"#######", "#S...E#", "#######"},
            new Position[][] {{new Position(1, 2), new Position(1, 3)}}));
  }

  /** Valida movimientos rechazados, colisión previa, reaparición y undo íntegro. */
  @Test
  void collisionAndUndo() {
    GameEngine e = collisionGame();
    assertFalse(e.move(-1, 0));
    assertFalse(e.move(1, 1));
    assertFalse(e.move(Integer.MAX_VALUE, 1));
    assertEquals(0, e.moves());
    assertEquals(0, e.undoSize());
    assertTrue(e.move(0, 1));
    assertEquals(2, e.lives());
    assertEquals(new Position(1, 1), e.player());
    assertEquals(0, e.enemyIndex(0));
    assertEquals(1, e.moves());
    assertTrue(e.historyAt(0).damaged());
    assertTrue(e.undo());
    assertEquals(3, e.lives());
    assertEquals(0, e.moves());
    assertEquals(0, e.historySize());
    assertEquals(0, e.enemyIndex(0));
    assertFalse(e.undo());
  }

  /** Agotar vidas bloquea turnos; undo revierte derrota y reinicio limpia. */
  @Test
  void defeatRestart() {
    GameEngine e = collisionGame();
    for (int i = 0; i < 3; i++) e.move(0, 1);
    assertEquals(GameStatus.LOST, e.status());
    assertEquals(0, e.lives());
    assertFalse(e.move(0, 1));
    assertTrue(e.undo());
    assertEquals(GameStatus.PLAYING, e.status());
    assertEquals(1, e.lives());
    e.restart();
    assertEquals(3, e.lives());
    assertEquals(0, e.moves());
    assertEquals(0, e.historySize());
    assertEquals(0, e.undoSize());
  }

  /** La salida finaliza y deshacer restaura el estado anterior. */
  @Test
  void victoryAndHistory() {
    GameEngine e = new GameEngine(new Maze("Pasillo", new String[] {"S..E"}, new Position[0][]));
    for (int i = 0; i < 3; i++) assertTrue(e.move(0, 1));
    assertEquals(GameStatus.WON, e.status());
    assertFalse(e.move(0, -1));
    assertEquals(3, e.historySize());
    assertEquals(3, e.undoSize());
    assertTrue(e.historyText(false).startsWith("#1"));
    assertTrue(e.historyText(true).startsWith("#3"));
    assertTrue(e.undo());
    assertEquals(GameStatus.PLAYING, e.status());
    assertEquals(new Position(0, 2), e.player());
    e.move(0, -1);
    assertEquals(3, e.moves());
    assertEquals(new Position(0, 1), e.historyAt(2).result());
  }

  /** Detecta enemigo entrante, intercambio y daño múltiple limitado a una vida. */
  @Test
  void incomingSwapAndMultiple() {
    Position[][] route = {
      {new Position(1, 3), new Position(1, 2)}, {new Position(1, 2), new Position(1, 3)}
    };
    GameEngine e =
        new GameEngine(new Maze("Doble", new String[] {"#######", "#S...E#", "#######"}, route));
    e.move(0, 1);
    assertEquals(2, e.lives());
    GameEngine swap =
        new GameEngine(
            new Maze(
                "Cruce",
                new String[] {"S...E", "....."},
                new Position[][] {{new Position(1, 1), new Position(0, 1)}}));
    swap.move(0, 1);
    assertEquals(2, swap.lives());
  }

  /** En turnos seguros los enemigos cierran el ciclo y undo restaura su fase. */
  @Test
  void circularTurnsAndRestoration() {
    GameEngine e =
        new GameEngine(
            new Maze(
                "Patrulla",
                new String[] {"S...E", "....."},
                new Position[][] {{new Position(1, 2), new Position(1, 3)}}));
    e.move(0, 1);
    assertEquals(1, e.enemyIndex(0));
    e.move(0, -1);
    assertEquals(0, e.enemyIndex(0));
    e.undo();
    assertEquals(1, e.enemyIndex(0));
    assertEquals(new Position(0, 1), e.player());
    e.undo();
    assertEquals(0, e.enemyIndex(0));
    assertEquals(0, e.moves());
  }

  /** Los tres mapas existen, progresan en área y tienen solución estática. */
  @Test
  void levelsReachable() {
    int area = 0;
    for (int i = 0; i < Levels.count(); i++) {
      Maze m = Levels.get(i);
      assertTrue(m.rows() * m.cols() > area);
      area = m.rows() * m.cols();
      assertTrue(BreadthFirstSearch.find(m, m.start(), m.exit()).found(), m.name());
    }
  }

  /** Valida mapas malformados y copias defensivas. */
  @Test
  void mapValidation() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Maze("x", new String[] {"S.", "E"}, new Position[0][]));
    assertThrows(
        IllegalArgumentException.class,
        () -> new Maze("x", new String[] {"S?E"}, new Position[0][]));
    assertThrows(
        IllegalArgumentException.class,
        () -> new Maze("x", new String[] {"SSE"}, new Position[0][]));
    assertThrows(
        IllegalArgumentException.class,
        () -> new Maze("x", new String[] {"S.."}, new Position[0][]));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Maze(
                "x",
                new String[] {"S..E"},
                new Position[][] {{new Position(0, 0), new Position(0, 1)}}));
    String[] rows = {"S.E"};
    Maze m = new Maze("x", rows, new Position[0][]);
    rows[0] = "###";
    assertTrue(m.walkable(new Position(0, 1)));
    int[] indices = {1};
    GameSnapshot s = new GameSnapshot(m.start(), 3, 0, GameStatus.PLAYING, indices);
    indices[0] = 99;
    s.enemies()[0] = 88;
    assertEquals(1, s.enemies()[0]);
  }

  /** Reproduce el ensayo: el quinto paso causa daño y un undo recupera el turno cuatro. */
  @Test
  void presentationCollisionOccursOnFifthStep() {
    GameEngine e = new GameEngine(Levels.get(0));
    for (int i = 0; i < 4; i++) {
      assertTrue(e.move(0, 1));
      assertEquals(3, e.lives());
    }
    Position playerBefore = e.player(), enemyBefore = e.enemyPosition(0);
    assertTrue(e.move(0, 1));
    assertEquals(2, e.lives());
    assertEquals(e.maze().start(), e.player());
    assertEquals(5, e.moves());
    assertTrue(e.undo());
    assertEquals(3, e.lives());
    assertEquals(4, e.moves());
    assertEquals(playerBefore, e.player());
    assertEquals(enemyBefore, e.enemyPosition(0));
  }
}
