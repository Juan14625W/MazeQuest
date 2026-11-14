package mazequest.engine;
import mazequest.model.Position;
/**
 * Instantánea inmutable del estado reversible, almacenada en la pila.
 *
     * @param player posición anterior
     * @param lives vidas
     * @param moves turnos
 *
     * @param status estado anterior
     * @param enemies índices de patrulla
 */
public record GameSnapshot(Position player, int lives, int moves, GameStatus status, int[] enemies) {
    /** Copia índices para no compartir estado mutable con el motor.
     *
     * @param player posición
     * @param lives vidas
     * @param moves turnos
     *
     * @param status estado
     * @param enemies índices */
    public GameSnapshot { enemies = enemies.clone(); }
    /** Devuelve copia defensiva.
     * @return índices independientes */
    @Override public int[] enemies() { return enemies.clone(); }
}