package mazequest.model;

/**
 * Coordenada inmutable para tablero, patrullas y predecesores.
 *
 * @param row fila, origen cero
 * @param col columna, origen cero
 */
public record Position(int row, int col) {
  /**
   * Desplaza sin validar el tablero.
   *
   * @param dr cambio de fila
   * @param dc cambio de columna
   * @return nueva coordenada
   */
  public Position step(int dr, int dc) {
    return new Position(row + dr, col + dc);
  }

  /**
   * Etiqueta compacta para el panel técnico.
   *
   * @return coordenada legible
   */
  @Override
  public String toString() {
    return "(" + row + "," + col + ")";
  }
}
