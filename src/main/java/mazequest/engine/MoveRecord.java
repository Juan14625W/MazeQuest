package mazequest.engine;

import mazequest.model.Position;

/**
 * Registro del turno, incluyendo destino intentado y reaparición si hubo daño.
 *
 * @param turn número de turno
 * @param from origen
 * @param target destino transitado
 * @param result posición final
 * @param damaged si perdió una vida
 */
public record MoveRecord(
    int turn, Position from, Position target, Position result, boolean damaged) {
  /**
   * Texto de historial.
   *
   * @return resumen legible
   */
  @Override
  public String toString() {
    return "#" + turn + "  " + from + " → " + target + (damaged ? " · daño → " + result : "");
  }
}
