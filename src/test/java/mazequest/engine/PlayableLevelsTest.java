package mazequest.engine;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import mazequest.model.*;
import mazequest.structures.CustomQueue;
import org.junit.jupiter.api.Test;

/** Verificación adicional: existe una secuencia ganadora con enemigos móviles, sin perder vidas. */
class PlayableLevelsTest {
  /** Constructor de JUnit. */
  PlayableLevelsTest() {}

  /** Busca en espacio (celda, fase de patrullas) y reproduce la solución en el motor real. */
  @Test
  void allLevelsCanBeWonWithEnemies() {
    for (int level = 0; level < Levels.count(); level++) {
      Maze m = Levels.get(level);
      int period = 1;
      for (int i = 0; i < m.enemyCount(); i++) {
        int n = m.route(i).length;
        period = period / gcd(period, n) * n;
      }
      int cells = m.rows() * m.cols(), total = cells * period;
      int[] previous = new int[total];
      Arrays.fill(previous, -1);
      int start = m.start().row() * m.cols() + m.start().col(), end = -1;
      CustomQueue<Integer> q = new CustomQueue<>();
      q.enqueue(start);
      previous[start] = start;
      int[] dr = {-1, 0, 1, 0}, dc = {0, 1, 0, -1};
      while (!q.isEmpty()) {
        int state = q.dequeue(), time = state / cells, cell = state % cells;
        Position p = new Position(cell / m.cols(), cell % m.cols());
        if (p.equals(m.exit())) {
          end = state;
          break;
        }
        for (int d = 0; d < 4; d++) {
          Position next = p.step(dr[d], dc[d]);
          if (!m.walkable(next)) continue;
          boolean hit = false;
          for (int i = 0; i < m.enemyCount(); i++) {
            Position[] route = m.route(i);
            if (next.equals(route[time % route.length])
                || next.equals(route[(time + 1) % route.length])) hit = true;
          }
          int id = ((time + 1) % period) * cells + next.row() * m.cols() + next.col();
          if (!hit && previous[id] == -1) {
            previous[id] = state;
            q.enqueue(id);
          }
        }
      }
      assertTrue(end >= 0, "Sin secuencia segura: " + m.name());
      int length = 0;
      for (int n = end; n != start; n = previous[n]) length++;
      int[] sequence = new int[length];
      int n = end;
      for (int i = length - 1; i >= 0; i--) {
        sequence[i] = n % cells;
        n = previous[n];
      }
      GameEngine game = new GameEngine(m);
      for (int cell : sequence)
        assertTrue(
            game.move(
                cell / m.cols() - game.player().row(), cell % m.cols() - game.player().col()));
      assertEquals(GameStatus.WON, game.status());
      assertEquals(3, game.lives());
      System.out.println(m.name() + ": victoria real en " + game.moves() + " turnos, 3 vidas");
    }
  }

  /**
   * Máximo común divisor para calcular periodo conjunto.
   *
   * @param a primer número
   * @param b segundo número
   * @return divisor
   */
  private int gcd(int a, int b) {
    while (b != 0) {
      int r = a % b;
      a = b;
      b = r;
    }
    return a;
  }

  /** Intercambio auténtico: ambos actores intercambian origen y destino en un turno. */
  @Test
  void actualSwapIsCollision() {
    GameEngine e =
        new GameEngine(
            new Maze(
                "Intercambio",
                new String[] {"S...E", "....."},
                new Position[][] {
                  {new Position(1, 2), new Position(0, 2), new Position(0, 1), new Position(1, 1)}
                }));
    e.move(0, 1);
    assertEquals(new Position(0, 1), e.player());
    assertEquals(new Position(0, 2), e.enemyPosition(0));
    e.move(0, 1);
    assertEquals(2, e.lives());
    assertEquals(e.maze().start(), e.player());
    e.undo();
    assertEquals(new Position(0, 1), e.player());
    assertEquals(new Position(0, 2), e.enemyPosition(0));
    assertEquals(3, e.lives());
  }
}
