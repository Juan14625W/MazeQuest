package mazequest.model;
/** Catálogo de tres mapas fijos; su resolubilidad se comprueba con BFS en pruebas. */
public final class Levels {
    /** Evita instancias del catálogo. */
    private Levels() { }
    /** Número de niveles.
     * @return tres */
    public static int count() { return 3; }
    /** Construye un nivel independiente.
     * @param index índice 0..2
     * @return mapa validado
     *
     * @throws IllegalArgumentException nivel desconocido */
    public static Maze get(int index) {
        return switch(index) {
            case 0 -> new Maze("El umbral", new String[]{
                "#############", "#S..........#", "#.###.###.#.#",
                "#...#.....#.#", "###.#.###.#.#", "#...#...#...#",
                "#.#####.###.#", "#..........E#", "#############"
            }, new Position[][]{line(1,6,8)});
            case 1 -> new Maze("Las galerías", new String[]{
                "#################", "#S....#.........#", "#.##..#.#####.#.#",
                "#..#....#.....#.#", "##.######.###.#.#", "#.......#...#...#",
                "#.#####.###.###.#", "#.#...#.....#...#", "#.#.#.#######.#.#",
                "#...#..........E#", "#################"
            }, new Position[][]{line(5,1,4), line(9,9,12)});
            case 2 -> new Maze("El santuario", new String[]{
                "#####################", "#S....#.............#", "#.###.#.#######.###.#",
                "#...#...#.....#...#.#", "###.#####.###.###.#.#", "#...#.....#...#...#.#",
                "#.###.#####.###.###.#", "#.....#.....#.......#", "#.#####.#####.#####.#",
                "#.#.....#.....#.....#", "#.#.#####.#####.###.#", "#.................#E#",
                "#####################"
            }, new Position[][]{line(1,9,12), line(7,14,17), line(11,5,8)});
            default -> throw new IllegalArgumentException("Nivel desconocido");
        };
    }
    /** Construye ida y vuelta sin teletransporte al cerrar.
     * @param row fila
     *
     * @param from columna inicial
     * @param to columna final
     * @return ciclo adyacente */
    private static Position[] line(int row, int from, int to) {
        Position[] result = new Position[2*(to-from)]; int n = 0;
        for (int c = from; c <= to; c++) result[n++] = new Position(row,c);
        for (int c = to-1; c > from; c--) result[n++] = new Position(row,c);
        return result;
    }
}