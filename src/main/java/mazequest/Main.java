package mazequest;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import mazequest.view.GamePanel;

/** Arranque de escritorio; crea y modifica componentes únicamente en el EDT. */
public final class Main {
  /** Evita instanciar el lanzador. */
  private Main() {}

  /**
   * Abre MazeQuest.
   *
   * @param args argumentos reservados
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          JFrame frame = new JFrame("MazeQuest: The Lost Labyrinth");
          GamePanel panel = new GamePanel(frame::dispose);
          frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
          frame.addWindowListener(
              new WindowAdapter() {
                /**
                 * Convierte el cierre del sistema en confirmación del juego.
                 *
                 * @param e evento
                 */
                @Override
                public void windowClosing(WindowEvent e) {
                  panel.requestClose();
                }
              });
          frame.setContentPane(panel);
          frame.setMinimumSize(new Dimension(960, 680));
          frame.pack();
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
          panel.requestFocusInWindow();
        });
  }
}
