# Trazabilidad
Fuente: prompt; pendiente contrastar PDF original.
| Requisito | Implementación | Prueba prevista |
|---|---|---|
| Pila y deshacer íntegro | CustomStack / GameSnapshot | StructuresTest / GameEngineTest |
| Cola FIFO O(1) | CustomQueue | StructuresTest / BreadthFirstSearchTest |
| Circular y patrullas | CircularLinkedList / Enemy | StructuresTest / GameEngineTest |
| Historial bidireccional | DoublyLinkedList / MoveRecord | StructuresTest / GameEngineTest |
| BST, duplicados, eliminaciones | BinarySearchTree | StructuresTest |
| BFS mínimo, obstáculos, inválidos | BreadthFirstSearch | BreadthFirstSearchTest |
| Tres niveles, victoria, derrota | Levels / GameEngine | GameEngineTest |
| Colisión, vidas, reinicio | GameEngine | GameEngineTest |
| Menú, HUD, pausa, controles | GamePanel / GameController | Revisión GUI |
| Panel real de estructuras | GamePanel / SearchResult | Pruebas y revisión GUI |
| Java 21, VS Code, Maven, JAR | .vscode / pom.xml | Build y arranque |
| Javadoc español | Clases y métodos | javadoc:javadoc y auditoría |
| Manuales PDF y sustentación | docs / output/pdf | Renderizado y revisión |
| GitHub privado | origin/main | SHA remoto = SHA local |
