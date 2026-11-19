package mazequest.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import mazequest.algorithms.SearchResult;
import mazequest.controller.GameController;
import mazequest.engine.*;
import mazequest.model.*;

/**
 * Vista Java2D escalable: menús, tablero y panel técnico sobre estado real. Coordenadas lógicas
 * 1200x800; todas las mutaciones ocurren en el EDT.
 */
public final class GamePanel extends JPanel {
  /** Pantallas que comparten el mismo renderer. */
  private enum Screen {
    MENU,
    LEVELS,
    HELP,
    SETTINGS,
    PLAY,
    TECH
  }

  /**
   * Botón dibujado y accesible por ratón o Tab/Enter.
   *
   * @param bounds área
   * @param label texto
   * @param action acción
   * @param enabled disponibilidad
   */
  private record Button(Rectangle bounds, String label, Runnable action, boolean enabled) {}

  /** Puente entre eventos y lógica, no serializable. */
  private final GameController controller;

  /** Reloj de repintado del EDT. */
  private final Timer timer;

  /** Pantalla visible. */
  private Screen screen = Screen.MENU;

  /** Áreas interactivas reconstruidas en cada frame. */
  private final Button[] buttons = new Button[32];

  /** Cantidad de botones activos. */
  private int buttonCount;

  /** Índice de navegación con Tab. */
  private int focus = -1;

  /** Ratón en espacio lógico horizontal. */
  private double mouseX = -1;

  /** Ratón en espacio lógico vertical. */
  private double mouseY = -1;

  /** Estado visual de pulsación. */
  private boolean pressed;

  /** Nivel seleccionado, origen cero. */
  private int level;

  /** Diálogo activo; vacío permite acciones. */
  private String modal = "";

  /** Preferencia de interpolación. */
  private boolean animations = true;

  /** Preferencia de celdas BFS descubiertas. */
  private boolean showExplored = true;

  /** Inicio del fundido actual. */
  private long transition = System.nanoTime();

  /** Inicio del movimiento visual. */
  private long moveStarted;

  /** Inicio del reloj de sesión no reversible. */
  private long sessionStarted;

  /** Origen de interpolación del jugador. */
  private Position oldPlayer;

  /** Orígenes de interpolación enemigos. */
  private Position[] oldEnemies = new Position[0];

  /** Activa feedback rojo tras colisión. */
  private boolean damaged;

  /** Entrada de historial consultada. */
  private int historyIndex = -1;

  /** Estructura seleccionada en laboratorio. */
  private int technicalTab;

  /** Recorrido del BST: inorden, preorden, posorden. */
  private int order;

  /** Desplazamiento vertical del contenido técnico. */
  private int scroll;

  /** Dirección de recorrido de la lista doble. */
  private boolean reverseHistory;

  /** Mensaje transitorio de movimiento inválido. */
  private String notice = "";

  /** Fin de visibilidad del aviso. */
  private long noticeUntil;

  /** Cierre controlado por la ventana propietaria. */
  private final Runnable close;

  /**
   * Construye controles, temporizador y enlaces de teclado.
   *
   * @param close callback para cerrar la ventana
   */
  public GamePanel(Runnable close) {
    this.close = close;
    controller = new GameController(this::repaint);
    setBackground(Palette.BG);
    setFocusable(true);
    setPreferredSize(new Dimension(1200, 800));
    timer = new Timer(16, e -> repaint());
    addMouseListener(
        new MouseAdapter() {
          /**
           * Registra feedback del botón presionado.
           *
           * @param e evento
           */
          @Override
          public void mousePressed(MouseEvent e) {
            pressed = true;
            locate(e);
            repaint();
          }

          /**
           * Ejecuta una acción disponible y recupera el foco del teclado.
           *
           * @param e evento
           */
          @Override
          public void mouseReleased(MouseEvent e) {
            pressed = false;
            locate(e);
            requestFocusInWindow();
            for (int i = 0; i < buttonCount; i++) {
              Button b = buttons[i];
              if (b.enabled && b.bounds.contains(mouseX, mouseY)) {
                // Enter continúa desde el botón pulsado, salvo que la acción cambie de pantalla.
                focus = i;
                b.action.run();
                break;
              }
            }
            repaint();
          }

          /**
           * Limpia hover al salir.
           *
           * @param e evento
           */
          @Override
          public void mouseExited(MouseEvent e) {
            mouseX = -1;
            mouseY = -1;
            pressed = false;
          }
        });
    addMouseMotionListener(
        new MouseMotionAdapter() {
          /**
           * Actualiza hover en coordenadas lógicas.
           *
           * @param e evento
           */
          @Override
          public void mouseMoved(MouseEvent e) {
            locate(e);
            repaint();
          }

          /**
           * Mantiene hover mientras se arrastra.
           *
           * @param e evento
           */
          @Override
          public void mouseDragged(MouseEvent e) {
            locate(e);
            repaint();
          }
        });
    addMouseWheelListener(
        e -> {
          if (screen == Screen.TECH) {
            scroll = Math.max(0, scroll + e.getWheelRotation() * 42);
            repaint();
          }
        });
    setFocusTraversalKeysEnabled(false);
    bind(
        "TAB",
        () -> {
          if (buttonCount > 0) {
            focus = (focus + 1) % buttonCount;
            repaint();
          }
        });
    bind(
        "ENTER",
        () -> {
          if (focus >= 0 && focus < buttonCount && buttons[focus].enabled)
            buttons[focus].action.run();
        });
    bind("ESCAPE", this::escape);
    bind("UP", () -> handleMove(-1, 0));
    bind("W", () -> handleMove(-1, 0));
    bind("DOWN", () -> handleMove(1, 0));
    bind("S", () -> handleMove(1, 0));
    bind("LEFT", () -> handleMove(0, -1));
    bind("A", () -> handleMove(0, -1));
    bind("RIGHT", () -> handleMove(0, 1));
    bind("D", () -> handleMove(0, 1));
    bind("Z", this::undo);
    bind("H", this::hint);
    bind(
        "T",
        () -> {
          if (screen == Screen.PLAY && modal.isEmpty()) change(Screen.TECH);
          else if (screen == Screen.TECH) change(Screen.PLAY);
        });
    bind(
        "R",
        () -> {
          if (screen == Screen.PLAY && modal.isEmpty()) modal = "RESTART";
        });
  }

  /** Activa el reloj únicamente cuando el componente pertenece a una ventana. */
  @Override
  public void addNotify() {
    super.addNotify();
    timer.start();
  }

  /** Evita dejar un temporizador activo después de cerrar. */
  @Override
  public void removeNotify() {
    timer.stop();
    super.removeNotify();
  }

  /** Solicita confirmación de salida, también para el botón de cierre del sistema. */
  public void requestClose() {
    modal = "EXIT";
    repaint();
  }

  /**
   * Asocia teclas a acciones sin depender del componente que tenga foco.
   *
   * @param key tecla
   * @param action acción
   */
  private void bind(String key, Runnable action) {
    getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
    getActionMap()
        .put(
            key,
            new AbstractAction() {
              /**
               * Reenvía la acción en el EDT.
               *
               * @param e evento
               */
              @Override
              public void actionPerformed(ActionEvent e) {
                action.run();
                repaint();
              }
            });
  }

  /**
   * Convierte coordenadas físicas usando el mismo escalado que paintComponent.
   *
   * @param e evento
   */
  private void locate(MouseEvent e) {
    double scale = Math.min(getWidth() / 1200.0, getHeight() / 800.0);
    mouseX = (e.getX() - (getWidth() - 1200 * scale) / 2) / scale;
    mouseY = (e.getY() - (getHeight() - 800 * scale) / 2) / scale;
  }

  /**
   * Cambia pantalla y reinicia foco/desplazamiento.
   *
   * @param next destino
   */
  private void change(Screen next) {
    screen = next;
    modal = "";
    focus = -1;
    scroll = 0;
    transition = System.nanoTime();
    repaint();
  }

  /**
   * Inicia sesión real del nivel elegido.
   *
   * @param index nivel
   */
  private void start(int index) {
    level = index;
    controller.start(index);
    historyIndex = -1;
    sessionStarted = System.nanoTime();
    moveStarted = 0;
    change(Screen.PLAY);
  }

  /** Reinicia sin conservar historial ni reloj de sesión. */
  private void restart() {
    controller.restart();
    historyIndex = -1;
    sessionStarted = System.nanoTime();
    moveStarted = 0;
    modal = "";
  }

  /**
   * Determina si sigue una interpolación.
   *
   * @return animación activa
   */
  private boolean moving() {
    return animations && System.nanoTime() - moveStarted < 150_000_000L;
  }

  /**
   * Aplica entradas de movimiento solo durante juego libre.
   *
   * @param dr fila
   * @param dc columna
   */
  private void handleMove(int dr, int dc) {
    if (screen != Screen.PLAY || !modal.isEmpty() || moving()) return;
    GameEngine e = controller.game();
    oldPlayer = e.player();
    int lives = e.lives();
    oldEnemies = new Position[e.enemyCount()];
    for (int i = 0; i < oldEnemies.length; i++) oldEnemies[i] = e.enemyPosition(i);
    if (controller.move(dr, dc)) {
      damaged = e.lives() < lives;
      moveStarted = System.nanoTime();
      historyIndex = e.historySize() - 1;
    } else if (e.status() == GameStatus.PLAYING) {
      notice = "Un muro bloquea el paso";
      noticeUntil = System.nanoTime() + 900_000_000L;
    }
  }

  /** Deshace incluso estados terminales, sin atravesar un diálogo de confirmación. */
  private void undo() {
    if (screen != Screen.PLAY || !modal.isEmpty() || moving()) return;
    if (controller.undo()) {
      moveStarted = 0;
      historyIndex = controller.game().historySize() - 1;
    }
  }

  /** Lanza pista asíncrona en juego o panel técnico. */
  private void hint() {
    if ((screen == Screen.PLAY || screen == Screen.TECH) && modal.isEmpty()) controller.hint();
  }

  /** Escape cierra paneles o alterna pausa. */
  private void escape() {
    if (!modal.isEmpty()) {
      modal = "";
      return;
    }
    if (screen == Screen.TECH) change(Screen.PLAY);
    else if (screen == Screen.PLAY) modal = "PAUSE";
    else if (screen == Screen.MENU) modal = "EXIT";
    else change(Screen.MENU);
  }

  /**
   * Dibuja escena completa con letterboxing y antialiasing.
   *
   * @param graphics contexto Swing
   */
  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    Graphics2D g = (Graphics2D) graphics.create();
    double scale = Math.min(getWidth() / 1200.0, getHeight() / 800.0);
    g.translate((getWidth() - 1200 * scale) / 2, (getHeight() - 800 * scale) / 2);
    g.scale(scale, scale);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setPaint(new GradientPaint(0, 0, new Color(16, 31, 44), 1100, 800, Palette.BG));
    g.fillRect(0, 0, 1200, 800);
    buttonCount = 0;
    switch (screen) {
      case MENU -> menu(g);
      case LEVELS -> levels(g);
      case HELP -> help(g);
      case SETTINGS -> settings(g);
      case PLAY -> play(g);
      case TECH -> technical(g);
    }
    if (!modal.isEmpty()) dialog(g);
    // Fundido breve sobre la pantalla nueva; nunca bloquea el EDT.
    double fade =
        animations ? Math.max(0, 1 - (System.nanoTime() - transition) / 220_000_000.0) : 0;
    if (fade > 0) {
      g.setColor(new Color(10, 17, 27, (int) (fade * 220)));
      g.fillRect(0, 0, 1200, 800);
    }
    g.dispose();
  }

  /**
   * Cabecera común.
   *
   * @param g contexto
   * @param subtitle sección
   */
  private void header(Graphics2D g, String subtitle) {
    text(g, "◈", 44, 58, 30, Palette.CYAN, true);
    text(g, "MAZEQUEST", 88, 55, 22, Palette.TEXT, true);
    text(g, subtitle.toUpperCase(), 900, 53, 12, Palette.MUTED, true);
    g.setColor(Palette.LINE);
    g.drawLine(44, 80, 1156, 80);
    text(g, "THE LOST LABYRINTH", 45, 775, 11, Palette.MUTED, false);
    text(
        g,
        "WASD / FLECHAS     ·     ESC PAUSA     ·     TAB + ENTER",
        690,
        775,
        11,
        Palette.MUTED,
        false);
  }

  /**
   * Inicio personalizado con mapa real como motivo de fondo.
   *
   * @param g contexto
   */
  private void menu(Graphics2D g) {
    header(g, "Explora · recuerda · encuentra");
    text(g, "UNA SALIDA. CADA DECISIÓN CUENTA.", 64, 153, 12, Palette.CYAN, true);
    text(g, "EL LABERINTO", 60, 222, 48, Palette.TEXT, true);
    text(g, "TE ESPERA.", 60, 278, 48, Palette.GOLD, true);
    text(g, "Encuentra el camino. Aprende de cada paso.", 64, 325, 18, Palette.MUTED, false);
    text(g, "Tres expediciones hacia lo desconocido.", 64, 353, 18, Palette.MUTED, false);
    button(g, "Comenzar expedición   →", 64, 405, 400, 56, () -> start(0), true, true);
    button(g, "Seleccionar nivel", 64, 476, 400, 48, () -> change(Screen.LEVELS), false, true);
    button(g, "Cómo jugar", 64, 537, 192, 46, () -> change(Screen.HELP), false, true);
    button(g, "Configuración", 272, 537, 192, 46, () -> change(Screen.SETTINGS), false, true);
    button(g, "Salir", 64, 601, 400, 44, () -> modal = "EXIT", false, true);
    Maze m = Levels.get(0);
    board(g, m, null, 568, 163, 570, 460, true);
    card(g, 600, 644, 500, 58);
    text(g, "01—03", 620, 680, 19, Palette.GOLD, true);
    text(g, "TRES NIVELES · UNA MISIÓN", 710, 678, 13, Palette.MUTED, true);
  }

  /**
   * Selector con dificultad y enemigos reales.
   *
   * @param g contexto
   */
  private void levels(Graphics2D g) {
    header(g, "Expediciones");
    text(g, "Elige tu siguiente desafío", 54, 146, 34, Palette.TEXT, true);
    String[] descriptions = {
      "Primeros pasos entre piedra y luz.",
      "Cruces, desvíos y dos guardianes.",
      "Una red profunda de rutas y peligros."
    };
    for (int i = 0; i < 3; i++) {
      int x = 54 + i * 372, chosen = i;
      Maze m = Levels.get(i);
      card(g, x, 190, 348, 450);
      text(g, "0" + (i + 1), x + 24, 238, 27, Palette.GOLD, true);
      board(g, m, null, x + 20, 270, 308, 210, true);
      text(g, m.name(), x + 24, 521, 24, Palette.TEXT, true);
      text(g, descriptions[i], x + 24, 551, 13, Palette.MUTED, false);
      text(
          g,
          m.enemyCount() + " guardián(es) · " + m.cols() + " × " + m.rows(),
          x + 24,
          579,
          13,
          Palette.CYAN,
          false);
      button(g, "Explorar   →", x + 24, 594, 300, 34, () -> start(chosen), true, true);
    }
    button(g, "← Volver", 54, 688, 180, 45, () -> change(Screen.MENU), false, true);
  }

  /**
   * Instrucciones con reglas exactas del motor.
   *
   * @param g contexto
   */
  private void help(Graphics2D g) {
    header(g, "Guía de exploración");
    text(g, "Tu brújula en la oscuridad", 54, 145, 34, Palette.TEXT, true);
    String[][] rows = {
      {"MUÉVETE", "WASD o flechas. Solo los pasos válidos consumen turno."},
      {"OBSERVA", "Cada turno mueve a los guardianes por una ruta circular."},
      {"SOBREVIVE", "Evita su celda anterior y su celda nueva. Máximo un daño por turno."},
      {"REAPARECE", "Tras daño vuelves a la entrada; las patrullas vuelven al inicio."},
      {"RECUERDA", "Z deshace un turno completo, incluso después de ganar o perder."},
      {"ENCUENTRA", "H muestra el camino mínimo estático; no evita enemigos móviles."},
      {"INVESTIGA", "T abre estructuras reales. R reinicia con confirmación. Esc pausa."}
    };
    for (int i = 0; i < rows.length; i++) {
      int y = 190 + i * 65;
      card(g, 54, y, 1092, 54);
      text(g, rows[i][0], 76, y + 33, 13, Palette.CYAN, true);
      text(g, rows[i][1], 230, y + 33, 16, Palette.TEXT, false);
    }
    text(
        g,
        "Tres vidas. Salida dorada = victoria. Cero vidas = derrota. No se guarda progreso.",
        55,
        684,
        16,
        Palette.GOLD,
        false);
    button(g, "← Volver", 54, 705, 180, 38, () -> change(Screen.MENU), false, true);
  }

  /**
   * Ajustes de sesión con efecto inmediato.
   *
   * @param g contexto
   */
  private void settings(Graphics2D g) {
    header(g, "Configuración");
    text(g, "A tu ritmo", 54, 151, 38, Palette.TEXT, true);
    card(g, 54, 205, 1092, 340);
    text(g, "Animaciones", 85, 260, 23, Palette.TEXT, true);
    text(
        g,
        "Movimiento suave y transiciones. Desactiva para respuesta instantánea.",
        85,
        292,
        17,
        Palette.MUTED,
        false);
    button(
        g,
        animations ? "Activadas" : "Desactivadas",
        890,
        240,
        220,
        48,
        () -> animations = !animations,
        true,
        true);
    text(g, "Exploración BFS", 85, 393, 23, Palette.TEXT, true);
    text(
        g,
        "Muestra las celdas descubiertas, además de la ruta mínima.",
        85,
        425,
        17,
        Palette.MUTED,
        false);
    button(
        g,
        showExplored ? "Visible" : "Oculta",
        890,
        370,
        220,
        48,
        () -> showExplored = !showExplored,
        true,
        true);
    text(
        g,
        "Los ajustes se conservan durante esta ejecución. No se requiere conexión.",
        55,
        605,
        17,
        Palette.MUTED,
        false);
    button(g, "← Volver", 54, 690, 180, 45, () -> change(Screen.MENU), false, true);
  }

  /**
   * Partida y HUD obtenidos exclusivamente del motor.
   *
   * @param g contexto
   */
  private void play(Graphics2D g) {
    GameEngine e = controller.game();
    header(g, "Nivel 0" + (level + 1) + " / 03");
    text(g, e.maze().name(), 54, 127, 27, Palette.TEXT, true);
    text(
        g,
        "VIDAS  " + "● ".repeat(e.lives()) + "○ ".repeat(3 - e.lives()),
        560,
        125,
        20,
        Palette.RED,
        true);
    text(g, String.format("%03d", e.moves()) + "  TURNOS", 880, 125, 18, Palette.TEXT, true);
    card(g, 44, 152, 790, 533);
    board(g, e.maze(), e, 62, 169, 754, 499, false);
    card(g, 855, 152, 301, 533);
    text(g, "TU EXPEDICIÓN", 878, 187, 12, Palette.CYAN, true);
    text(g, "Encuentra la salida", 878, 222, 22, Palette.TEXT, true);
    text(g, "◈ Tú      ◆ Salida", 878, 259, 16, Palette.GOLD, false);
    text(g, "● Guardián", 878, 288, 16, Palette.RED, false);
    text(g, "HISTORIAL", 878, 337, 12, Palette.MUTED, true);
    if (e.historySize() > 0) {
      historyIndex = Math.max(0, Math.min(historyIndex, e.historySize() - 1));
      MoveRecord r = e.historyAt(historyIndex);
      text(g, "Turno " + r.turn() + " de " + e.historySize(), 878, 369, 18, Palette.TEXT, true);
      text(g, r.from() + " → " + r.target(), 878, 398, 16, Palette.MUTED, false);
      text(
          g,
          r.damaged() ? "Daño · regreso a entrada" : "Paso seguro",
          878,
          424,
          14,
          r.damaged() ? Palette.RED : Palette.CYAN,
          false);
    } else text(g, "Tu viaje comienza aquí.", 878, 379, 16, Palette.MUTED, false);
    button(g, "←", 878, 446, 116, 37, () -> historyIndex--, false, historyIndex > 0);
    button(
        g,
        "→",
        1008,
        446,
        124,
        37,
        () -> historyIndex++,
        false,
        historyIndex < e.historySize() - 1);
    text(g, "PISTA ESTÁTICA", 878, 523, 12, Palette.MUTED, true);
    text(
        g,
        controller.searching()
            ? "Explorando…"
            : controller.message().isEmpty() ? "Pulsa H para orientarte" : controller.message(),
        878,
        551,
        15,
        Palette.CYAN,
        false);
    text(g, "No predice a los guardianes.", 878, 578, 14, Palette.MUTED, false);
    long seconds = (System.nanoTime() - sessionStarted) / 1_000_000_000;
    text(
        g,
        String.format("%02d:%02d", seconds / 60, seconds % 60)
            + " sesión  ·  "
            + controller.hints()
            + " pistas",
        878,
        638,
        15,
        Palette.TEXT,
        false);
    button(g, "Z  Deshacer", 44, 705, 173, 43, this::undo, false, e.undoSize() > 0 && !moving());
    button(g, "H  Pista BFS", 230, 705, 173, 43, this::hint, true, !controller.searching());
    button(g, "T  Estructuras", 416, 705, 195, 43, () -> change(Screen.TECH), false, true);
    button(g, "R  Reiniciar", 624, 705, 191, 43, () -> modal = "RESTART", false, true);
    button(g, "Esc  Pausa", 855, 705, 301, 43, () -> modal = "PAUSE", false, true);
    if (System.nanoTime() < noticeUntil) text(g, notice, 66, 668, 14, Palette.GOLD, true);
    if (damaged && moving()) {
      g.setColor(new Color(246, 104, 118, 45));
      g.fillRect(44, 152, 790, 533);
    }
    if (e.status() != GameStatus.PLAYING && !moving() && modal.isEmpty())
      ending(g, e.status() == GameStatus.WON);
  }

  /**
   * Dibuja geometría y entidades con interpolación.
   *
   * @param g contexto
   * @param m mapa
   * @param e motor o null para vista previa
   * @param x izquierda
   * @param y arriba
   * @param width ancho
   * @param height alto
   * @param preview si vista previa
   */
  private void board(
      Graphics2D g, Maze m, GameEngine e, int x, int y, int width, int height, boolean preview) {
    double tile = Math.min(width / (double) m.cols(), height / (double) m.rows());
    double bx = x + (width - tile * m.cols()) / 2, by = y + (height - tile * m.rows()) / 2;
    for (int r = 0; r < m.rows(); r++)
      for (int c = 0; c < m.cols(); c++) {
        double px = bx + c * tile, py = by + r * tile;
        boolean floor = m.walkable(new Position(r, c));
        g.setColor(floor ? new Color(12, 23, 34) : new Color(32, 49, 64));
        g.fill(new RoundRectangle2D.Double(px + 1, py + 1, tile - 2, tile - 2, 4, 4));
        if (!floor) {
          g.setColor(new Color(48, 68, 83));
          g.draw(new Line2D.Double(px + 4, py + 3, px + tile - 4, py + 3));
          g.setColor(new Color(23, 37, 51));
          g.draw(new Line2D.Double(px + 3, py + tile - 3, px + tile - 3, py + tile - 3));
        }
      }
    SearchResult result = preview ? null : controller.search();
    if (result != null) {
      if (showExplored)
        for (Position p : result.explored()) {
          g.setColor(new Color(86, 225, 202, 24));
          g.fill(
              new Rectangle2D.Double(
                  bx + p.col() * tile + 2, by + p.row() * tile + 2, tile - 4, tile - 4));
        }
      g.setColor(new Color(86, 225, 202, 130));
      Position[] path = result.path();
      g.setStroke(
          new BasicStroke(
              (float) Math.max(2, tile * .07), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      for (int i = 1; i < path.length; i++)
        g.draw(
            new Line2D.Double(
                bx + (path[i - 1].col() + .5) * tile,
                by + (path[i - 1].row() + .5) * tile,
                bx + (path[i].col() + .5) * tile,
                by + (path[i].row() + .5) * tile));
      g.setStroke(new BasicStroke(1));
    }
    marker(
        g,
        bx + (m.start().col() + .5) * tile,
        by + (m.start().row() + .5) * tile,
        tile * .28,
        new Color(64, 111, 113),
        false);
    marker(
        g,
        bx + (m.exit().col() + .5) * tile,
        by + (m.exit().row() + .5) * tile,
        tile * .34,
        Palette.GOLD,
        true);
    double t =
        !moving() || damaged ? 1 : Math.min(1, (System.nanoTime() - moveStarted) / 150_000_000.0);
    t = t * t * (3 - 2 * t);
    if (e != null) {
      for (int i = 0; i < e.enemyCount(); i++) {
        Position p = e.enemyPosition(i),
            old = (oldEnemies.length == e.enemyCount() && moving() && !damaged) ? oldEnemies[i] : p;
        marker(
            g,
            bx + (old.col() + (p.col() - old.col()) * t + .5) * tile,
            by + (old.row() + (p.row() - old.row()) * t + .5) * tile,
            tile * .25,
            Palette.RED,
            false);
      }
      Position p = e.player(), old = oldPlayer != null && moving() && !damaged ? oldPlayer : p;
      marker(
          g,
          bx + (old.col() + (p.col() - old.col()) * t + .5) * tile,
          by + (old.row() + (p.row() - old.row()) * t + .5) * tile,
          tile * .29,
          Palette.CYAN,
          true);
    } else
      marker(
          g,
          bx + (m.start().col() + .5) * tile,
          by + (m.start().row() + .5) * tile,
          tile * .29,
          Palette.CYAN,
          true);
  }

  /**
   * Símbolo luminoso con forma distinta para jugador/salida y enemigo.
   *
   * @param g contexto
   * @param x centro
   * @param y centro
   * @param radius radio
   * @param color tono
   * @param diamond rombo
   */
  private void marker(
      Graphics2D g, double x, double y, double radius, Color color, boolean diamond) {
    g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
    g.fill(new Ellipse2D.Double(x - radius * 1.9, y - radius * 1.9, radius * 3.8, radius * 3.8));
    g.setColor(color);
    if (diamond) {
      Path2D p = new Path2D.Double();
      p.moveTo(x, y - radius);
      p.lineTo(x + radius, y);
      p.lineTo(x, y + radius);
      p.lineTo(x - radius, y);
      p.closePath();
      g.fill(p);
    } else g.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
    g.setColor(new Color(255, 255, 255, 180));
    g.fill(new Ellipse2D.Double(x - radius * .22, y - radius * .4, radius * .4, radius * .4));
  }

  /**
   * Panel técnico con desplazamiento y datos reales, nunca ejemplos simulados.
   *
   * @param g contexto
   */
  private void technical(Graphics2D g) {
    header(g, "Laboratorio de estructuras");
    text(g, "Lo que ocurre detrás de cada paso", 54, 136, 30, Palette.TEXT, true);
    String[] tabs = {"01  Pila", "02  Cola BFS", "03  Circular", "04  Historial", "05  BST"};
    for (int i = 0; i < 5; i++) {
      final int tab = i;
      button(
          g,
          tabs[i],
          54 + i * 220,
          172,
          208,
          44,
          () -> {
            technicalTab = tab;
            scroll = 0;
          },
          technicalTab == i,
          true);
    }
    GameEngine e = controller.game();
    SearchResult r = controller.search();
    String title = "", body = "";
    switch (technicalTab) {
      case 0 -> {
        title = "LIFO · " + e.undoSize() + " estados reversibles";
        body = "TOPE → estados anteriores (turno, posición, vidas)\n" + e.stackText();
      }
      case 1 -> {
        title = "FIFO · instantánea real de la frontera máxima";
        body =
            r == null
                ? "Ejecuta BFS para inspeccionar su cola."
                : "Cola en máximo: "
                    + r.frontier().length
                    + " elementos. No es la cola actual.\n"
                    + positions(r.frontier())
                    + "\n\nDescubrimiento: "
                    + r.visitedCount()
                    + " celdas\n"
                    + positions(r.explored());
      }
      case 2 -> {
        title = "Ciclos · " + e.enemyCount() + " patrullas enlazadas";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < e.enemyCount(); i++)
          b.append("GUARDIÁN ")
              .append(i + 1)
              .append(" · cursor ")
              .append(e.enemyIndex(i))
              .append(" · actual ")
              .append(e.enemyPosition(i))
              .append("\n")
              .append(e.patrolText(i))
              .append("\n\n");
        body = b.toString();
      }
      case 3 -> {
        title = "Doble enlace · " + e.historySize() + " movimientos";
        body =
            (reverseHistory ? "ÚLTIMO → PRIMERO (prev)" : "PRIMERO → ÚLTIMO (next)")
                + "\n"
                + e.historyText(reverseHistory);
      }
      case 4 -> {
        title = "BST no balanceado · " + (r == null ? 0 : r.visitedCount()) + " claves";
        body =
            r == null
                ? "Ejecuta BFS para construir el árbol de visitados."
                : "Clave = fila × "
                    + e.maze().cols()
                    + " + columna\n"
                    + new String[] {"INORDEN", "PREORDEN", "POSORDEN"}[order]
                    + "\n"
                    + r.treeText(order)
                    + "\n\n"
                    + "Buscar/insertar/eliminar: O(h), peor O(n).\n"
                    + "BFS con este BST: peor O(V²), memoria O(V).";
      }
    }
    text(g, title, 58, 257, 22, Palette.CYAN, true);
    card(g, 54, 280, 1092, 355);
    Shape clip = g.getClip();
    g.clipRect(72, 293, 1055, 327);
    g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
    g.setColor(Palette.TEXT);
    String[] lines = wrap(body, 103);
    int maxScroll = Math.max(0, lines.length * 27 - 310);
    scroll = Math.min(scroll, maxScroll);
    for (int i = 0; i < lines.length; i++) g.drawString(lines[i], 78, 317 + i * 27 - scroll);
    g.setClip(clip);
    text(
        g,
        "Rueda: desplazar contenido · El historial se consulta sin cambiar la partida.",
        56,
        663,
        14,
        Palette.MUTED,
        false);
    button(g, "← Volver al juego", 54, 698, 250, 45, () -> change(Screen.PLAY), false, true);
    if (technicalTab == 1 || technicalTab == 4)
      button(
          g,
          controller.searching() ? "Calculando…" : "Ejecutar BFS",
          330,
          698,
          220,
          45,
          this::hint,
          true,
          !controller.searching());
    if (technicalTab == 3)
      button(
          g,
          reverseHistory ? "Ver hacia adelante" : "Ver hacia atrás",
          330,
          698,
          250,
          45,
          () -> {
            reverseHistory = !reverseHistory;
            scroll = 0;
          },
          true,
          true);
    if (technicalTab == 4)
      button(
          g,
          "Recorrido: " + new String[] {"inorden", "preorden", "posorden"}[order],
          580,
          698,
          300,
          45,
          () -> {
            order = (order + 1) % 3;
            scroll = 0;
          },
          false,
          true);
  }

  /**
   * Serializa coordenadas auxiliares capturadas de estructuras reales.
   *
   * @param values posiciones
   * @return texto
   */
  private String positions(Position[] values) {
    StringBuilder b = new StringBuilder();
    for (Position p : values) b.append(p).append("  ");
    return b.toString();
  }

  /**
   * Divide líneas largas sin perder contenido; devuelve arreglo auxiliar de presentación.
   *
   * @param text contenido
   * @param width caracteres máximos
   * @return líneas
   */
  private String[] wrap(String text, int width) {
    StringBuilder b = new StringBuilder();
    // Cortar por espacios conserva claves numéricas y coordenadas completas.
    for (String paragraph : text.split("\n", -1)) {
      String remaining = paragraph;
      while (remaining.length() > width) {
        int cut = remaining.lastIndexOf(' ', width);
        if (cut <= 0) cut = width;
        b.append(remaining, 0, cut).append('\n');
        remaining = remaining.substring(cut).stripLeading();
      }
      b.append(remaining).append('\n');
    }
    return b.toString().split("\n", -1);
  }

  /**
   * Pantalla terminal que mantiene disponibles reinicio y undo.
   *
   * @param g contexto
   * @param won victoria
   */
  private void ending(Graphics2D g, boolean won) {
    shade(g);
    buttonCount = 0;
    card(g, 322, 220, 556, 365);
    text(
        g,
        won ? "EXPEDICIÓN COMPLETADA" : "EL LABERINTO TE ATRAPÓ",
        354,
        272,
        15,
        won ? Palette.GOLD : Palette.RED,
        true);
    text(
        g,
        won ? "Encontraste la salida." : "Aún hay otro camino.",
        354,
        320,
        31,
        Palette.TEXT,
        true);
    text(
        g,
        controller.game().moves() + " turnos · " + controller.game().lives() + " vidas restantes",
        354,
        362,
        18,
        Palette.MUTED,
        false);
    button(
        g,
        won && level < 2 ? "Siguiente nivel →" : "Volver a intentar",
        354,
        401,
        492,
        47,
        () -> {
          if (won && level < 2) start(level + 1);
          else restart();
        },
        true,
        true);
    button(
        g,
        "Deshacer turno",
        354,
        463,
        236,
        43,
        this::undo,
        false,
        controller.game().undoSize() > 0);
    button(g, "Menú principal", 608, 463, 238, 43, () -> change(Screen.MENU), false, true);
    text(g, "Z también restaura el turno anterior.", 354, 550, 14, Palette.MUTED, false);
  }

  /**
   * Diálogo propio de pausa y confirmaciones.
   *
   * @param g contexto
   */
  private void dialog(Graphics2D g) {
    shade(g);
    buttonCount = 0;
    card(g, 340, 245, 520, 300);
    boolean pause = modal.equals("PAUSE");
    String title =
        switch (modal) {
          case "EXIT" -> "¿Cerrar MazeQuest?";
          case "RESTART" -> "¿Reiniciar el nivel?";
          case "MENU" -> "¿Volver al menú?";
          default -> "Un respiro en el camino";
        };
    text(g, title, 372, 303, 27, Palette.TEXT, true);
    text(
        g,
        pause ? "La partida está en pausa." : "Se perderá el progreso de esta partida.",
        372,
        346,
        17,
        Palette.MUTED,
        false);
    button(g, pause ? "Continuar" : "Cancelar", 372, 389, 220, 48, () -> modal = "", true, true);
    button(
        g,
        pause ? "Menú principal" : "Confirmar",
        610,
        389,
        218,
        48,
        () -> {
          String action = modal;
          modal = "";
          if (action.equals("EXIT")) close.run();
          else if (action.equals("RESTART")) restart();
          else if (action.equals("PAUSE")) modal = "MENU";
          else change(Screen.MENU);
        },
        false,
        true);
    text(g, "Esc para volver", 372, 493, 14, Palette.MUTED, false);
  }

  /**
   * Oscurece escena sin alterar el estado.
   *
   * @param g contexto
   */
  private void shade(Graphics2D g) {
    g.setColor(new Color(3, 8, 15, 210));
    g.fillRect(0, 0, 1200, 800);
  }

  /**
   * Superficie con borde tenue.
   *
   * @param g contexto
   * @param x izquierda
   * @param y arriba
   * @param w ancho
   * @param h alto
   */
  private void card(Graphics2D g, int x, int y, int w, int h) {
    g.setColor(Palette.PANEL);
    g.fillRoundRect(x, y, w, h, 18, 18);
    g.setColor(Palette.LINE);
    g.drawRoundRect(x, y, w, h, 18, 18);
  }

  /**
   * Texto consistente.
   *
   * @param g contexto
   * @param s texto
   * @param x izquierda
   * @param y línea base
   * @param size tamaño
   * @param color color
   * @param bold peso
   */
  private void text(Graphics2D g, String s, int x, int y, int size, Color color, boolean bold) {
    g.setFont(Palette.font(size, bold));
    g.setColor(color);
    g.drawString(s, x, y);
  }

  /**
   * Dibuja y registra botón funcional.
   *
   * @param g contexto
   * @param label texto
   * @param x izquierda
   * @param y arriba
   * @param w ancho
   * @param h alto
   * @param action acción
   * @param primary acento
   * @param enabled disponible
   */
  private void button(
      Graphics2D g,
      String label,
      int x,
      int y,
      int w,
      int h,
      Runnable action,
      boolean primary,
      boolean enabled) {
    Rectangle rect = new Rectangle(x, y, w, h);
    int index = buttonCount;
    buttons[buttonCount++] = new Button(rect, label, action, enabled);
    boolean hover = enabled && (rect.contains(mouseX, mouseY) || focus == index);
    Color fill = primary ? Palette.CYAN : Palette.PANEL;
    if (hover) fill = primary ? new Color(127, 241, 219) : new Color(33, 54, 69);
    if (!enabled) fill = new Color(26, 35, 46);
    g.setColor(fill);
    g.fillRoundRect(x, y + (pressed && hover ? 2 : 0), w, h, 10, 10);
    g.setColor(hover ? Palette.CYAN : Palette.LINE);
    g.drawRoundRect(x, y, w, h, 10, 10);
    g.setFont(Palette.font(15, true));
    g.setColor(!enabled ? new Color(88, 106, 121) : primary ? Palette.BG : Palette.TEXT);
    FontMetrics fm = g.getFontMetrics();
    g.drawString(
        label, x + (w - fm.stringWidth(label)) / 2, y + (h - fm.getHeight()) / 2 + fm.getAscent());
  }
}
