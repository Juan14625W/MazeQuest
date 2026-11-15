package mazequest.view;

import java.awt.Color;
import java.awt.Font;

/** Colores y tipografía del juego, sin recursos externos ni licencias adicionales. */
final class Palette {
  /** Fondo profundo. */
  static final Color BG = new Color(10, 17, 27);

  /** Superficie elevada. */
  static final Color PANEL = new Color(19, 31, 45);

  /** Contorno tenue. */
  static final Color LINE = new Color(40, 61, 77);

  /** Acento interactivo. */
  static final Color CYAN = new Color(86, 225, 202);

  /** Objetivos. */
  static final Color GOLD = new Color(242, 196, 111);

  /** Peligro. */
  static final Color RED = new Color(246, 104, 118);

  /** Texto principal. */
  static final Color TEXT = new Color(230, 239, 243);

  /** Texto secundario. */
  static final Color MUTED = new Color(143, 163, 179);

  /** No instanciable. */
  private Palette() {}

  /**
   * Tipografía lógica portable.
   *
   * @param size tamaño
   * @param bold peso
   * @return fuente
   */
  static Font font(int size, boolean bold) {
    return new Font(Font.SANS_SERIF, bold ? Font.BOLD : Font.PLAIN, size);
  }
}
