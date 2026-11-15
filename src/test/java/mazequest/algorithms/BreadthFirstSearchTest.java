package mazequest.algorithms;

import static org.junit.jupiter.api.Assertions.*;

import mazequest.model.*;
import org.junit.jupiter.api.Test;

/** Verifica distancias conocidas, adyacencia, límites y unicidad de exploración. */
class BreadthFirstSearchTest {
  /** Constructor para JUnit. */
  BreadthFirstSearchTest() {}

  /** Un obstáculo obliga a rodear: seis pasos en lugar de cuatro. */
  @Test
  void knownShortestPath() {
    Maze m = new Maze("x", new String[] {"S.#.E", "....."}, new Position[0][]);
    SearchResult r = BreadthFirstSearch.find(m, m.start(), m.exit());
    assertTrue(r.found());
    assertEquals(6, r.distance());
    Position[] p = r.path();
    assertEquals(m.start(), p[0]);
    assertEquals(m.exit(), p[p.length - 1]);
    for (int i = 1; i < p.length; i++) {
      assertTrue(m.walkable(p[i]));
      assertEquals(
          1, Math.abs(p[i].row() - p[i - 1].row()) + Math.abs(p[i].col() - p[i - 1].col()));
    }
    boolean[][] seen = new boolean[m.rows()][m.cols()];
    for (Position q : r.explored()) {
      assertFalse(seen[q.row()][q.col()]);
      seen[q.row()][q.col()] = true;
    }
    assertEquals(r.explored().length, r.visitedCount());
    assertTrue(r.frontier().length > 0);
    assertFalse(r.treeText(0).isEmpty());
    assertFalse(r.treeText(1).isEmpty());
    assertFalse(r.treeText(2).isEmpty());
    p[0] = null;
    assertNotNull(r.path()[0]);
  }

  /** Destino aislado devuelve resultado vacío, no excepción. */
  @Test
  void unreachable() {
    Maze m = new Maze("x", new String[] {"S#E"}, new Position[0][]);
    SearchResult r = BreadthFirstSearch.find(m, m.start(), m.exit());
    assertFalse(r.found());
    assertEquals(-1, r.distance());
    assertEquals(1, r.visitedCount());
  }

  /** Origen igual a destino requiere cero pasos. */
  @Test
  void sameCell() {
    Maze m = new Maze("x", new String[] {"S.E"}, new Position[0][]);
    SearchResult r = BreadthFirstSearch.find(m, m.start(), m.start());
    assertEquals(0, r.distance());
    assertArrayEquals(new Position[] {m.start()}, r.path());
  }

  /** Rechaza nulos, muros y posiciones fuera de límites. */
  @Test
  void invalidPositions() {
    Maze m = new Maze("x", new String[] {"S#E"}, new Position[0][]);
    assertThrows(IllegalArgumentException.class, () -> BreadthFirstSearch.find(m, null, m.exit()));
    assertThrows(
        IllegalArgumentException.class,
        () -> BreadthFirstSearch.find(m, new Position(-1, 0), m.exit()));
    assertThrows(
        IllegalArgumentException.class,
        () -> BreadthFirstSearch.find(m, m.start(), new Position(0, 3)));
    assertThrows(
        IllegalArgumentException.class,
        () -> BreadthFirstSearch.find(m, m.start(), new Position(0, 1)));
  }
}
