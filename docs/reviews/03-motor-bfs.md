# Auditoría solo lectura — `mazequest.model`, `mazequest.engine`, `mazequest.algorithms` y pruebas asociadas

Alcance inspeccionado (solo lectura):
- Código: `src/main/java/mazequest/model/{Position,Maze,Enemy,Levels}.java`, `src/main/java/mazequest/engine/{GameStatus,MoveRecord,GameSnapshot,GameEngine}.java`, `src/main/java/mazequest/algorithms/{SearchResult,BreadthFirstSearch}.java`.
- Pruebas: `src/test/java/mazequest/engine/GameEngineTest.java`, `src/test/java/mazequest/algorithms/BreadthFirstSearchTest.java`.
- Estructuras propias consultadas como evidencia de soporte: `src/main/java/mazequest/structures/{BinarySearchTree,CustomQueue,CustomStack,CircularLinkedList,DoublyLinkedList}.java`.
- Verificación textual en fuentes del alcance: sin coincidencias para `javax.swing|java.awt` ni para `java.util.(Stack|Queue|ArrayDeque|LinkedList|ArrayList|HashSet|HashMap|TreeSet|TreeMap)` (`Select-String` sobre `src/main/java/mazequest/{model,engine,algorithms}/*.java`).

## 1. Colisiones previas y posteriores
**Evidencia de implementación (`GameEngine.move`)**
- `src/main/java/mazequest/engine/GameEngine.java:44-45`: solo permite turno en `PLAYING` y movimiento ortogonal unitario (`dr/dc` en {-1,0,1} con exactamente un eje activo).
- `:47-48`: calcula `target = player.step(dr,dc)` y rechaza si no es transitable (`false`, sin consumir turno).
- `:49`: guarda instantánea antes de mutar (`undo.push(snapshot())`).
- `:50-54`: para cada enemigo, **colisión previa** si ya ocupa el destino (`e.position().equals(target)` antes de `advance()`), luego avanza y comprueba **colisión posterior/entrante** si termina en el destino tras moverse (`if (e.position().equals(target)) hit = true;`). El flag `hit` acumula la detección.
- `:55`: mueve al jugador al destino e incrementa turno.
- `:56-62`: si hubo daño, aplica penalización/reaparición y registra el turno con `damaged=true`; si no, evalúa victoria.

**Evidencia de pruebas**
- `GameEngineTest.collisionAndUndo` (`src/test/java/mazequest/engine/GameEngineTest.java:19-26`): fixture con enemigo en ruta `(1,2),(1,3)` (`:13-16`); al mover `(0,1)` desde `(1,1)` hacia `(1,2)` (posición previa del enemigo) resulta `lives==2`, jugador reaparece en inicio `(1,1)`, `enemyIndex==0`, `moves==1` e historial marca daño.
- `GameEngineTest.incomingSwapAndMultiple` (`:46-53`, caso "entrante"): enemigo con ruta `{(1,3),(1,2)}`; tras `move(0,1)` hay daño (`lives==2`), cubriendo enemigo que entra en la celda destino.

## 2. Intercambio
**Implementación**
- No hay comparación explícita de cruce de trayectorias (origen/destino intercambiados entre jugador y enemigo) como caso nominal separado; la detección se basa en ocupación del `target` antes y después del avance enemigo (`GameEngine.java:50-54`). Esto captura el caso donde el enemigo termina en la celda a la que entra el jugador.

**Prueba**
- `GameEngineTest.incomingSwapAndMultiple` (`src/test/java/mazequest/engine/GameEngineTest.java:50-53`): define `swap` con ruta enemiga `{(1,1),(0,1)}` y jugador en `S (0,0)` que ejecuta `move(0,1)` → `lives==2`. En este escenario el enemigo avanza de `(1,1)` a `(0,1)` coincidiendo con el destino del jugador (detectado en la comprobación posterior al `advance`).

**Observación (hecho verificable):** el código no modela explícitamente “intercambio” por comparación de posiciones origen↔destino; lo cubre mediante coincidencia final en `target` dentro del mismo turno.

## 3. Una vida máxima por turno
**Implementación**
- `GameEngine.java:49,56-57`: `boolean hit = false`; múltiples detecciones solo fijan el flag, y la pérdida se aplica una única vez: `if (hit) { lives--; player = maze.start(); ... }`.

**Prueba**
- `GameEngineTest.incomingSwapAndMultiple` (`src/test/java/mazequest/engine/GameEngineTest.java:46-49`): ruta con dos enemigos `{{(1,3),(1,2)},{(1,2),(1,3)}}` sobre el mismo destino; tras `move(0,1)` las vidas pasan a `2` (una sola pérdida), acorde al nombre/documentación del caso “daño múltiple limitado a una vida”.

## 4. Deshacer integral, incluyendo fin de partida
**Implementación**
- Instantánea: `GameEngine.snapshot()` (`src/main/java/mazequest/engine/GameEngine.java:30-36`) copia `player,lives,moves,status` e índices de enemigos (`new GameSnapshot(...)`).
- `GameSnapshot` (`src/main/java/mazequest/engine/GameSnapshot.java:13-26`) es inmutable respecto a los índices: clona en constructor compacto (`enemies = enemies.clone()`) y devuelve copia defensiva en `enemies()`.
- Apilado: `move` hace `undo.push(snapshot())` antes de mutar (`GameEngine.java:49`); historial registra el turno (`:61`).
- Restauración: `undo()` (`:65-74`) desapila y restaura `player,lives,moves,status`, reubica cada enemigo con `restore(indices[i])` y elimina la última entrada de historial (`history.remove(history.size()-1)`); devuelve `false` si no hay estados.
- `restart()` (`:25-29`) limpia estado lógico, crea pila/historial nuevos y restaura enemigos a índice 0.

**Pruebas**
- Colisión + deshacer íntegro: `GameEngineTest.collisionAndUndo` (`:23-26`) verifica restauración de vidas, turno, historial, índice enemigo y ausencia de más deshacer.
- Derrota reversible y reinicio: `GameEngineTest.defeatRestart` (`:28-34`) llega a `LOST` con 0 vidas y bloquea `move`; `undo()` vuelve a `PLAYING` con 1 vida; `restart()` deja `3/0/0/0`.
- Victoria reversible e historial: `GameEngineTest.victoryAndHistory` (`:36-44`) llega a `WON` y bloquea movimiento; `undo()` restaura `PLAYING` y posición previa; historial/pila coherentes (`sizes`, orden forward/reverse, `historyAt`).
- Fase de enemigos restaurada: `GameEngineTest.circularTurnsAndRestoration` (`:55-62`) comprueba que `undo` restaura índices de patrulla y posición/turnos.

## 5. Independencia de Swing
- Búsqueda en `src/main/java/mazequest/model/*.java`, `engine/*.java`, `algorithms/*.java`: sin referencias a `javax.swing` ni `java.awt`.
- Las clases auditadas operan sobre modelo/estructuras propias y tipos básicos (`Position`, arreglos, `StringBuilder`, `java.util.function`, `java.util.Arrays` solo en BFS para copia de descubrimiento). No hay dependencias de UI en motor, modelo ni algoritmo inspeccionados.

## 6. BFS mínimo con BST propio y sin revisitas
**Implementación (`BreadthFirstSearch`)**
- `src/main/java/mazequest/algorithms/BreadthFirstSearch.java:3,24-29`: usa `CustomQueue<Position>` (frontera FIFO) y `BinarySearchTree visited` (propios); matriz `previous[row][col]`; arreglo `discovered`; encola origen, marca visitado al encolar e inserta en descubiertos.
- `:33-41`: expansión en 4 direcciones; condición de expansión `maze.walkable(next) && visited.insert(key(next, maze.cols()))` — **sin revisitas** porque el marcado ocurre al encolar y `insert` devuelve `false` si ya existe (`BinarySearchTree.insert`, `src/main/java/mazequest/structures/BinarySearchTree.java:26-39`).
- `:32-33`: termina al desencolar el objetivo (`found=true; break`), propio de ruta mínima en grafo no ponderado.
- `:42-47`: conserva instantánea de frontera en su máximo de ocupación (copia FIFO real vía `queue.forEach`).
- `:48-56`: reconstruye camino mínimo invirtiendo predecesores desde objetivo a origen; devuelve `SearchResult` con `path`, descubiertos, frontera pico y BST real.
- `:62`: clave única `row*columns+col`.
- `CustomQueue` (`src/main/java/mazequest/structures/CustomQueue.java:27-59`) garantiza FIFO con `enqueue/dequeue/peek` O(1).

**BST propio y complejidad documentada**
- `BinarySearchTree` (`src/main/java/mazequest/structures/BinarySearchTree.java:5-8,23,26,41,55,80,89,101`): BST entero sin balanceo ni colecciones externas; duplicados ignorados; `insert/contains/remove` O(h) peor O(n), recorridos O(n), espacio O(n), recorridos con `CustomStack` auxiliar O(h).
- Complejidad BFS documentada en código: `BreadthFirstSearch.java:7-10` indica tiempo `O((V+E)h)`, peor `O(V²)` en cuadrícula por BST no balanceado, memoria `O(V)` (incluye predecesores, ruta e instantánea de frontera).

**Pruebas**
- `BreadthFirstSearchTest.knownShortestPath` (`src/test/java/mazequest/algorithms/BreadthFirstSearchTest.java:11-25`): distancia mínima conocida (6), extremos correctos, adyacencia ortogonal en cada paso del camino, unicidad de explorados, `visitedCount == explored.length`, frontera no vacía, recorridos de árbol no vacíos y copia defensiva del resultado.
- `unreachable` (`:27-31`): sin ruta → `found=false`, `distance=-1`, `visitedCount==1`.
- `sameCell` (`:33-37`): distancia 0 y camino de un solo nodo.
- `invalidPositions` (`:39-45`): rechaza nulo, fuera de límites y muro.
- `GameEngineTest.levelsReachable` (`src/test/java/mazequest/engine/GameEngineTest.java:64-71`): los 3 niveles tienen solución estática encontrada por BFS.

## 7. Límites y validaciones observadas
- `Maze` (`src/main/java/mazequest/model/Maze.java:23-47,65-78`): valida nulidad, mapa no vacío/rectangular, símbolos permitidos, unicidad de `S/E`, existencia de entrada/salida; valida patrullas (no nulas, longitud ≥2, celdas transitables, distintas de inicio/salida, adyacencia ortogonal consecutiva y cierre cíclico). Copia defensiva de filas/rutas y `route(index)`; `contains/walkable` controlan límites.
- `Enemy`/patrullas (`Enemy.java:10-24`, `Levels.line :43-48`, `CircularLinkedList`): patrulla almacenada en lista circular propia con cursor; restauración por índice.
- `GameEngine` (`:44-48`): rechaza estado no jugable, diagonales/magnitudes no unitarias y destinos no transitables sin consumir turno.
- `BFS` (`:22-23`): exige `maze` no nulo y `start/goal` transitables.
- `SearchResult` (`:18-29`, `:44-54`): copias defensivas de arreglos, expone BST/recorridos y valida orden de recorrido.
- `GameEngineTest.mapValidation` (`:73-83`): cubre mapas no rectangulares, símbolo inválido, entrada repetida, salida ausente, patrulla inválida en `S`, inmutabilidad lógica frente a mutación externa de filas y copia defensiva en `GameSnapshot`.

## 8. Javadoc real (sin afirmar ausencias sin evidencia)
- Conteo de bloques `/**` detectados: 68 en `src/main/java/mazequest/{model,engine,algorithms}/*.java` y 16 en `src/test/java/mazequest/{engine,algorithms}/*.java`.
- En las declaraciones inspeccionadas de estas unidades, los tipos, constructores y métodos revisados presentan Javadoc inmediatamente precedente (p. ej. `Position` `:2-18`, `Maze` constructor/métodos `:4-78`, `Enemy` `:4-31`, `Levels` `:2-42`, `GameEngine` `:6-130`, `MoveRecord/GameSnapshot` records y miembros, `BreadthFirstSearch`/`SearchResult` y sus métodos, clases y métodos de prueba). No se identificaron declaraciones sin `/**` en el muestreo realizado de los archivos del alcance; no se afirma ausencia global fuera de lo evidenciado.

## Resumen ejecutivo
- **Cumple/soportado por evidencia:** colisiones previa y posterior en el mismo turno, límite de una vida por turno, deshacer integral con restauración de estado y fase enemiga (incluidos `WON/LOST`) y limpieza al reinicio, independencia de Swing en las capas auditadas, BFS con `CustomQueue`+`BinarySearchTree` propios y marcado al encolar (sin revisitas), validaciones de límites/mapa/movimiento y documentación de complejidad/costos.
- **Matiz a considerar:** el “intercambio” no está implementado como regla explícita de cruce origen↔destino, sino cubierto por la detección de coincidencia con el destino tras el avance enemigo (caso probado). La frontera expuesta por `SearchResult` es la instantánea de **máxima ocupación** histórica (`BFS.java:42-47`, `SearchResult.java:9,27-29`), no la cola final.