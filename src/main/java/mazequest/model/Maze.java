package mazequest.model;
import java.util.Objects;

/** Mapa inmutable validado. Arreglos representan geometría, no reemplazan estructuras enlazadas. */
public final class Maze {
    private final String name;
    private final String[] rows;
    private final Position start;
    private final Position exit;
    private final Position[][] routes;
    /**
     * Copia y valida geometría, símbolos y patrullas cerradas ortogonales.
     *
     * @param name nombre visible
     * @param rows filas con # muro, . suelo, S inicio y E salida
     *
     * @param routes rutas enemigas; pueden repetir celdas al regresar
     *
     * @throws IllegalArgumentException mapa o patrulla inválidos
     *
     * @throws NullPointerException argumentos nulos
     */
    public Maze(String name, String[] rows, Position[][] routes) {
        this.name = Objects.requireNonNull(name); Objects.requireNonNull(rows); Objects.requireNonNull(routes);
        if (rows.length == 0 || rows[0] == null || rows[0].isEmpty()) throw new IllegalArgumentException("Mapa vacío");
        this.rows = rows.clone(); Position s = null, e = null;
        for (int r = 0; r < rows.length; r++) {
            if (rows[r] == null || rows[r].length() != rows[0].length()) throw new IllegalArgumentException("Mapa no rectangular");
            for (int c = 0; c < rows[r].length(); c++) {
                char ch = rows[r].charAt(c);
                if ("#.SE".indexOf(ch) < 0) throw new IllegalArgumentException("Símbolo desconocido");
                if (ch == 'S') { if (s != null) throw new IllegalArgumentException("Entrada repetida"); s = new Position(r,c); }
                if (ch == 'E') { if (e != null) throw new IllegalArgumentException("Salida repetida"); e = new Position(r,c); }
            }
        }
        if (s == null || e == null) throw new IllegalArgumentException("Falta entrada o salida");
        start = s; exit = e; this.routes = new Position[routes.length][];
        for (int i = 0; i < routes.length; i++) {
            if (routes[i] == null || routes[i].length < 2) throw new IllegalArgumentException("Patrulla demasiado corta");
            this.routes[i] = routes[i].clone();
            for (int j = 0; j < routes[i].length; j++) {
                Position a = routes[i][j], b = routes[i][(j+1)%routes[i].length];
                if (!walkable(a) || !walkable(b) || a.equals(start) || a.equals(exit)
                    || Math.abs(a.row()-b.row()) + Math.abs(a.col()-b.col()) != 1)
                    throw new IllegalArgumentException("Patrulla inválida");
            }
        }
    }
    /** Identifica el nivel.
     * @return nombre */
    public String name() { return name; }
    /** Alto.
     * @return filas */
    public int rows() { return rows.length; }
    /** Ancho.
     * @return columnas */
    public int cols() { return rows[0].length(); }
    /** Origen seguro.
     * @return entrada */
    public Position start() { return start; }
    /** Objetivo seguro.
     * @return salida */
    public Position exit() { return exit; }
    /** Valida antes de acceder a matrices.
     * @param p coordenada
     * @return dentro del tablero */
    public boolean contains(Position p) { return p != null && p.row() >= 0 && p.col() >= 0 && p.row() < rows() && p.col() < cols(); }
    /** Comprueba límites y muros.
     * @param p coordenada
     * @return transitable */
    public boolean walkable(Position p) { return contains(p) && rows[p.row()].charAt(p.col()) != '#'; }
    /** Número de enemigos.
     * @return rutas */
    public int enemyCount() { return routes.length; }
    /** Copia defensiva de una ruta.
     * @param index enemigo
     * @return ruta independiente */
    public Position[] route(int index) { return routes[index].clone(); }
}