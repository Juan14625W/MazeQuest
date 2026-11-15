# Arquitectura y reglas
Diseño inicial basado en el prompt; PDF original no disponible.

## Dependencias
view → controller → engine → model y structures. algorithms → model y structures.
Main abre la vista en el EDT. El motor no conoce Swing. BFS se ejecuta en SwingWorker; la vista descarta resultados de turnos o niveles anteriores.

## Reglas
- Un movimiento ortogonal transitable consume un turno; muros, bordes y partidas finalizadas no modifican estado.
- Cada turno avanza todos los enemigos un nodo de su lista circular.
- Hay colisión si el destino del jugador coincide con la posición de cualquier enemigo antes o después del avance. Incluye intercambios y entrada simultánea. Máximo una vida perdida por turno.
- Tras daño, jugador reaparece en la entrada y todas las patrullas vuelven al inicio. Entrada y salida quedan fuera de patrullas.
- Tres vidas iniciales. Cero implica derrota. Alcanzar la salida sin colisión implica victoria.
- Deshacer puede revertir victoria y derrota: restaura jugador, vidas, turnos, estado y cursores enemigos; elimina la última entrada del historial.
- Historial navegable es de consulta: no modifica el mundo. Reiniciar vacía pila e historial.
- BFS devuelve ruta mínima estática; ignora enemigos móviles y no garantiza seguridad.
- Pausa bloquea movimiento. Animación bloquea brevemente nuevas acciones. Ajustes: animaciones y exploración.
- El reloj mide sesión y no se rebobina con deshacer. No se persiste progreso.

## Clases y representación
Position inmutable. Maze valida mapas y patrullas. Levels define tres niveles.
Enemy conserva cursor real sobre CircularLinkedList. GameSnapshot copia datos reversibles.
GameEngine usa CustomStack<GameSnapshot> y DoublyLinkedList<MoveRecord>.
BreadthFirstSearch usa CustomQueue<Position>, BinarySearchTree y matriz de predecesores.
SearchResult conserva ruta, orden de exploración y una instantánea real de la frontera máxima.

## Complejidades
Pila/cola O(1) por operación, O(n) espacio.
Listas: insertar extremos O(1), índice/búsqueda O(n).
BST no balanceado O(h), peor O(n) para buscar/insertar/eliminar; recorridos O(n).
BFS O(A+(V+E)h+F²), peor O(A²) en cuadrícula; espacio O(A). A es el área del tablero, V las celdas descubiertas, h la altura del BST y F la máxima frontera. F² contempla copias diagnósticas al crecer el máximo de cola. No se promete O(V+E).

## Verificación
Cada fase necesita compilación y pruebas ejecutadas. La entrega requiere JAR real, GUI revisada, manuales PDF con capturas reales y guion. Registrar explícitamente plataformas y requisitos no verificados.
