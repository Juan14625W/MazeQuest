package mazequest.algorithms;

import java.util.Arrays;
import mazequest.model.*;
import mazequest.structures.*;

/**
 * BFS estático con cola y BST propios; no predice enemigos móviles. Tiempo O(A+(V+E)h+F²), peor
 * O(A²) en cuadrícula. A es el área, V las celdas descubiertas, h la altura del BST y F la frontera
 * máxima (copias de diagnóstico). Memoria O(A), incluidos predecesores, ruta e instantánea de
 * frontera.
 */
public final class BreadthFirstSearch {
  /** Clase de algoritmo sin estado global mutable. */
  private BreadthFirstSearch() {}

  /**
   * Busca ruta mínima no ponderada.
   *
   * @param maze mapa
   * @param start origen
   * @param goal destino
   * @return resultado verificable
   * @throws IllegalArgumentException posiciones inválidas o muros
   */
  public static SearchResult find(Maze maze, Position start, Position goal) {
    if (maze == null || !maze.walkable(start) || !maze.walkable(goal))
      throw new IllegalArgumentException("Origen y destino deben ser transitables");
    CustomQueue<Position> queue = new CustomQueue<>();
    BinarySearchTree visited = new BinarySearchTree();
    Position[][] previous = new Position[maze.rows()][maze.cols()];
    Position[] discovered = new Position[maze.rows() * maze.cols()];
    Position[] peak = {start};
    int count = 0;
    boolean found = false;
    queue.enqueue(start);
    visited.insert(key(start, maze.cols()));
    discovered[count++] = start;
    int[] dr = {-1, 0, 1, 0}, dc = {0, 1, 0, -1};
    while (!queue.isEmpty()) {
      Position p = queue.dequeue();
      if (p.equals(goal)) {
        found = true;
        break;
      }
      // Marcar al ENCOLAR impide que dos vecinos añadan la misma celda.
      for (int i = 0; i < 4; i++) {
        Position next = p.step(dr[i], dc[i]);
        if (maze.walkable(next) && visited.insert(key(next, maze.cols()))) {
          previous[next.row()][next.col()] = p;
          queue.enqueue(next);
          discovered[count++] = next;
        }
      }
      if (queue.size() > peak.length) {
        // Captura FIFO genuina; la vista la etiqueta como instantánea histórica.
        Position[] snapshot = new Position[queue.size()];
        int[] index = {0};
        queue.forEach(pending -> snapshot[index[0]++] = pending);
        peak = snapshot;
      }
    }
    Position[] path = new Position[0];
    if (found) {
      int length = 1;
      for (Position p = goal; !p.equals(start); p = previous[p.row()][p.col()]) length++;
      path = new Position[length];
      Position p = goal;
      // Predecesores apuntan al origen; llenar desde el final invierte el recorrido.
      for (int i = length - 1; i >= 0; i--) {
        path[i] = p;
        p = previous[p.row()][p.col()];
      }
    }
    return new SearchResult(path, Arrays.copyOf(discovered, count), peak, visited);
  }

  /**
   * Codifica sin colisiones en el mapa.
   *
   * @param p posición
   * @param columns ancho
   * @return clave única
   */
  private static int key(Position p, int columns) {
    return p.row() * columns + p.col();
  }
}
