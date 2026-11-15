package mazequest.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

/** Comprueba publicación de BFS e invalidación de ayudas usando el EDT sin abrir ventanas. */
class GameControllerTest {
  /** Constructor utilizado por JUnit. */
  GameControllerTest() {}

  /**
   * Espera fuera del EDT una pista real y comprueba el hilo de todas las notificaciones.
   *
   * @return controlador con una pista completada en El umbral
   * @throws Exception si falla el envío al EDT o se interrumpe la espera
   */
  private GameController withCompletedHint() throws Exception {
    GameController[] holder = new GameController[1];
    CountDownLatch completed = new CountDownLatch(1);
    AtomicBoolean allOnEdt = new AtomicBoolean(true);
    SwingUtilities.invokeAndWait(
        () -> {
          holder[0] =
              new GameController(
                  () -> {
                    if (!SwingUtilities.isEventDispatchThread()) allOnEdt.set(false);
                    if (holder[0].search() != null) completed.countDown();
                  });
          holder[0].start(0);
          holder[0].hint();
        });
    assertTrue(completed.await(5, TimeUnit.SECONDS), "BFS no publicó resultado a tiempo");
    assertTrue(allOnEdt.get(), "Una notificación ocurrió fuera del EDT");
    return holder[0];
  }

  /**
   * BFS publica una ruta desde el jugador hasta la salida y contabiliza una consulta.
   *
   * @throws Exception si falla el envío al EDT o se interrumpe la espera
   */
  @Test
  void hintPublishesOnEdt() throws Exception {
    GameController controller = withCompletedHint();
    SwingUtilities.invokeAndWait(
        () -> {
          assertFalse(controller.searching());
          assertEquals(1, controller.hints());
          assertTrue(controller.search().found());
          var path = controller.search().path();
          assertEquals(controller.game().player(), path[0]);
          assertEquals(controller.game().maze().exit(), path[path.length - 1]);
        });
  }

  /**
   * Un muro conserva la pista; moverse la invalida y reiniciar limpia las ayudas de sesión.
   *
   * @throws Exception si falla el envío al EDT o se interrumpe la espera
   */
  @Test
  void movementAndRestartInvalidateHints() throws Exception {
    GameController controller = withCompletedHint();
    SwingUtilities.invokeAndWait(
        () -> {
          var previous = controller.search();
          assertFalse(controller.move(-1, 0));
          assertSame(previous, controller.search());
          assertTrue(controller.move(0, 1));
          assertNull(controller.search());
          assertEquals(1, controller.hints());
          assertTrue(controller.undo());
          assertEquals(0, controller.game().moves());
          controller.restart();
          assertNull(controller.search());
          assertFalse(controller.searching());
          assertEquals(0, controller.hints());
          assertEquals(0, controller.game().undoSize());
          assertEquals(0, controller.game().historySize());
        });
  }
}
