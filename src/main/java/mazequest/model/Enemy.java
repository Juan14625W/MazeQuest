package mazequest.model;
import mazequest.structures.CircularLinkedList;

/** Enemigo cuyo desplazamiento sigue enlaces reales de una lista circular propia. */
public final class Enemy {
    private final CircularLinkedList<Position> route = new CircularLinkedList<>();
    private final CircularLinkedList<Position>.Cursor cursor;
    /** Copia posiciones a nodos y crea cursor.
     * @param positions patrulla validada por Maze */
    public Enemy(Position[] positions) {
        for (Position p : positions) route.addLast(p);
        cursor = route.cursor();
    }
    /** Avanza un enlace en O(1). */
    public void advance() { cursor.advance(); }
    /** Posición actual.
     * @return coordenada */
    public Position position() { return cursor.value(); }
    /** Índice para deshacer.
     * @return índice actual */
    public int index() { return cursor.index(); }
    /** Restaura una instantánea O(n).
     * @param index índice guardado */
    public void restore(int index) { cursor.seek(index); }
    /** Recorrido real para el panel, sin exponer nodos.
     * @return etiqueta cíclica */
    public String routeText() {
        StringBuilder b = new StringBuilder();
        route.forEach(p -> b.append(p).append(" → "));
        return b.append(route.get(0)).toString();
    }
}