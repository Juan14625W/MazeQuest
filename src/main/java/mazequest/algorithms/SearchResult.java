package mazequest.algorithms;

import mazequest.model.Position;
import mazequest.structures.BinarySearchTree;

/** Resultado verificable y copia de la frontera máxima real; conserva el BST construido por BFS. */
public final class SearchResult {
  private final Position[] path;
  private final Position[] explored;
  private final Position[] frontier;
  private final BinarySearchTree visited;

  /**
   * Recibe datos exclusivos del algoritmo.
   *
   * @param path ruta incluyendo extremos
   * @param explored orden de descubrimiento
   * @param frontier cola en su máximo
   * @param visited BST real de la búsqueda
   */
  SearchResult(
      Position[] path, Position[] explored, Position[] frontier, BinarySearchTree visited) {
    this.path = path.clone();
    this.explored = explored.clone();
    this.frontier = frontier.clone();
    this.visited = visited;
  }

  /**
   * Ruta defensiva.
   *
   * @return celdas ordenadas, vacío si no hay solución
   */
  public Position[] path() {
    return path.clone();
  }

  /**
   * Orden de descubrimiento sin repetidos.
   *
   * @return copia de celdas
   */
  public Position[] explored() {
    return explored.clone();
  }

  /**
   * Cola real en el instante de máxima ocupación, no cola final.
   *
   * @return copia FIFO
   */
  public Position[] frontier() {
    return frontier.clone();
  }

  /**
   * Existencia de solución.
   *
   * @return true si encontró ruta
   */
  public boolean found() {
    return path.length > 0;
  }

  /**
   * Distancia mínima.
   *
   * @return pasos o -1 sin solución
   */
  public int distance() {
    return found() ? path.length - 1 : -1;
  }

  /**
   * Cantidad real de claves visitadas.
   *
   * @return tamaño del BST
   */
  public int visitedCount() {
    return visited.size();
  }

  /**
   * Recorre el árbol real.
   *
   * @param order 0 inorden, 1 preorden, 2 posorden
   * @return claves en orden
   * @throws IllegalArgumentException orden inválido
   */
  public String treeText(int order) {
    StringBuilder b = new StringBuilder();
    java.util.function.IntConsumer action = k -> b.append(k).append(' ');
    switch (order) {
      case 0 -> visited.inorden(action);
      case 1 -> visited.preorden(action);
      case 2 -> visited.posorden(action);
      default -> throw new IllegalArgumentException("Recorrido inválido");
    }
    return b.toString().trim();
  }
}
