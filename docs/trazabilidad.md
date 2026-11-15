# Trazabilidad
Fuente: prompt; pendiente contrastar PDF original.
| Requisito | Implementación | Evidencia / estado |
|---|---|---|
| Pila y deshacer íntegro | CustomStack / GameSnapshot | StructuresTest / GameEngineTest |
| Cola FIFO O(1) | CustomQueue | StructuresTest / BreadthFirstSearchTest |
| Circular y patrullas | CircularLinkedList / Enemy | StructuresTest / GameEngineTest |
| Historial bidireccional | DoublyLinkedList / MoveRecord | StructuresTest / GameEngineTest |
| BST, duplicados, eliminaciones | BinarySearchTree | StructuresTest |
| BFS mínimo, obstáculos, inválidos | BreadthFirstSearch | BreadthFirstSearchTest |
| Tres niveles, victoria, derrota | Levels / GameEngine | GameEngineTest y PlayableLevelsTest: victoria con enemigos en los tres mapas |
| Colisión, vidas, reinicio | GameEngine | GameEngineTest |
| Menú, HUD, pausa, controles | GamePanel / GameController | Revisión GUI parcial Windows del 14/11; auditoría estática 04; no se afirma recorrido manual exhaustivo |
| Panel real de estructuras | GamePanel / SearchResult | Pruebas y revisión GUI |
| Java 21, VS Code, Maven, JAR | .vscode / pom.xml | Build limpio Java 21 del 15/11; arranque Windows registrado el 14/11; breakpoint pendiente |
| Javadoc español | Clases y métodos | javadoc:javadoc y auditoría |
| Manuales PDF y sustentación | docs/manual-usuario.md, docs/manual-tecnico.md, docs/sustentacion.md y output/pdf | Dos PDF generados, páginas renderizadas e inspeccionadas; guion de ocho minutos y 18 preguntas |
| GitHub privado | Juan14625W/MazeQuest | Visibilidad private consultada el 15/11; commit base 2f9d04b remoto confirmado |

Las pruebas automáticas citadas pasaron en la validación limpia (21 en total). Detalles, fechas y límites en [validación](validacion.md). La evidencia de CI del código base cubre Ubuntu/Windows con Java 21/25, sin ventanas. No demuestra revisión gráfica en Linux ni conformidad con el PDF académico ausente.
