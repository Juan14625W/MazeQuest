# Trazabilidad de requisitos

Fuente principal: «Proyecto final – Estructuras de Datos.pdf», cuatro páginas, entregado por el solicitante y contrastado el 15 de noviembre de 2026. SHA-256: `78140ee7e7d79fa5fe9733a07d7adfe5199cd4c7d8da0e99eceaf0d8b9b76f22`.

Equipo vigente (según README, actualizado el 20 de noviembre de 2026): Juan David Paz Rodriguez, José Ángel Lopez Paz y Carolina Diago Fernandez. Los estados de documentación se apoyan en los archivos de esta rama; no implican que la entrega a la docente o la defensa hayan ocurrido.

| Requisito de la profesora | Implementación / documento | Evidencia / estado |
|---|---|---|
| Grupo de dos o tres; contribuciones de cada integrante | README, historial Git | Tres integrantes identificados en README. Pendiente comprobar contribuciones reales e individuales de los tres: la auditoría anterior solo había identificado a Juan David. No se simula autoría. |
| Juego de laberinto, reglas claras, menú y finalización | Levels, GameEngine, GamePanel, manual de usuario | Tres niveles; colisiones, victoria, derrota y entradas inválidas en GameEngineTest y PlayableLevelsTest. Revisión gráfica parcial Windows documentada. |
| Pila: push, pop, peek, size, isEmpty | structures/CustomStack.java; GameEngine.undo | StructuresTest.stackContract; deshacer turno completo probado en GameEngineTest. |
| Cola: enqueue, dequeue, peek, size, isEmpty | structures/CustomQueue.java; BreadthFirstSearch | StructuresTest.queueContract y cuatro pruebas BFS. |
| Lista circular: insertar al inicio/final/índice, eliminar, buscar y recorrer | structures/CircularLinkedList.java; Enemy | circularInsertAndCycle y circularRemove; fase de patrulla y restauración en motor. |
| Lista doble: operaciones de lista y ambos recorridos | structures/DoublyLinkedList.java; historial MoveRecord | doublyLinks y victoryAndHistory. |
| Árbol: insertar, buscar, eliminar y tres recorridos | structures/BinarySearchTree.java; visitados BFS | treeTraversals, treeRemoveCases y degenerateTree. |
| Nodos propios y uso justificado de las cinco estructuras | structures y manual técnico, secciones 1.2, 2 y 2.1 | Nodos privados; sin colecciones sustitutas. BST de visitados justificado como variante de uso. |
| Código organizado y documentado | Seis paquetes, Javadoc en español | Compilación y Javadoc con doclint/failOnWarnings correctos; contratos de excepciones completados. |
| README: nombre, integrantes, lenguaje y ejecución | README.md | Datos incorporados; instrucciones JDK/Maven/JAR y límites de plataformas. |
| Manual de usuario PDF: instalar, ejecutar, reglas, controles, capturas y finales | output/pdf/manual-usuario.pdf | Fuente docs/manual-usuario.md; capturas reales, victoria/derrota y deshacer explicados. |
| Manual técnico PDF: tecnologías, construcción y organización | output/pdf/manual-tecnico.pdf | Versiones concretas, paquetes y generación del JAR. |
| Diagrama de clases | Manual técnico, secciones 1.1 y 1.2 | Diagramas vectoriales de aplicación y nodos; fuente scripts/class_diagrams.py. |
| Ubicación, elección y tabla de complejidades de cada estructura | Manual técnico, secciones 2 y 2.1 | Operaciones, costos de peor caso y costo real del BST/BFS. |
| Casos de prueba con resultado esperado y obtenido | Manual técnico, secciones 8 a 10 | Tabla de los 27 métodos JUnit, contrastada con sus aserciones y ejecución limpia. |
| Presentación de ocho minutos, preguntas de doce y modificación en vivo | docs/sustentacion.md | Guion, secuencia de daño comprobada, 22 preguntas y propuesta de práctica; ensayo y defensa pendientes. |
| Entrega el viernes 20 de noviembre de 2026 a las 18:00 | README y guion | Fecha documentada; envío y acceso docente pendientes de comprobar. |

## Compromisos internos adicionales

Adaptación académica solicitada el 16/11: [matriz final y casos concretos](adaptacion-academica.md). Las cinco estructuras conservan sus operaciones y usos; el árbol usa recorridos recursivos reales. Diferencias de estilo y correcciones necesarias de los ejemplos: [matriz inicial](reviews/07-matriz-academica-inicial.md).

| Compromiso | Evidencia / límite |
|---|---|
| Java 21, Maven, JUnit 5, JAR ejecutable | `clean verify javadoc:javadoc` exitoso con JDK 21.0.13 y Maven 3.9.11; target/mazequest.jar generado. |
| Motor/BFS sin Swing; EDT, Timer y SwingWorker | Dependencias separadas; GameControllerTest verifica publicación en EDT e invalidación. |
| BFS con cola propia, BST y matriz de predecesores | BreadthFirstSearch; prueba de mínimo, inaccesible, misma celda e inválidos. Peor tiempo O(A²), no O(V+E). |
| VS Code portable | .vscode sin rutas personales; detención efectiva en breakpoint pendiente. |
| GitHub privado y revisiones reales | Repositorio privado confirmado; informes OpenCode en docs/reviews. Integración de la rama en main requiere autorización. |
| Windows y Linux | GUI Windows parcialmente observada; CI base Ubuntu/Windows con Java 21/25 exitoso. GUI Linux pendiente. |

Las 27 pruebas pasaron sin fallos, errores u omisiones en la validación limpia local. Ver [validación](validacion.md) y [auditoría original](reviews/05-auditoria-integral.md). Los informes históricos conservan los pendientes que existían entonces; esta matriz describe el estado después de las correcciones.
