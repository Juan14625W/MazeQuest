# Revisión inicial externa

Revisor: OpenCode, sesión ses_e243ad6baffeGFLi8d0vYJ1N11, 2026-11-14. Revisó el esqueleto inicial antes de implementar las estructuras. No es una auditoría del juego final.

## Observaciones priorizadas (revisión de solo lectura)

### Alta

1. **Arquitectura Java 21/MVC y paquetes exigidos no implementados (solo esqueleto Maven)**
   - **Evidencia:** `src/main/java/mazequest/Main.java:1-13` solo imprime `"MazeQuest: base Maven preparada."` y el comentario indica que la vista se conectará después; el inventario del proyecto muestra únicamente `src\main\java\mazequest\Main.java` (sin paquetes `structures`, `model`, `algorithms`, `engine`, `controller`, `view` ni pruebas).
   - **Evidencia de diseño esperado:** `docs/arquitectura.md:4-7,20-25` define dependencias `view → controller → engine → model/structures`, `algorithms → model/structures`, ejecución de BFS en `SwingWorker`, y clases/estructuras concretas (`Position`, `Maze`, `Levels`, `Enemy`, `GameSnapshot`, `CustomStack`, `DoublyLinkedList`, `CustomQueue`, `BinarySearchTree`, matriz de predecesores, `SearchResult`).
   - **Impacto/riesgo:** incumplimiento directo de `AGENTS.md:12-18` (paquetes, independencia de Swing en motor/estructuras, estructuras propias, reglas de colisión/deshacer/BFS) y de la trazabilidad (`docs/trazabilidad.md:4-18`) que ya mapea requisitos a implementaciones/pruebas aún inexistentes. No hay base verificable para evaluar MVC real, reglas de juego ni cumplimiento académico.

2. **Reglas de colisión y deshacer integral: documentadas, pero sin implementación ni pruebas que las respalden**
   - **Evidencia:** `docs/arquitectura.md:8-18` especifica turno/colisión (antes o después del avance enemigo, intercambios, entrada simultánea, máx. 1 vida/turno), reaparición, victoria/derrota, deshacer reversible de victoria/derrota restaurando jugador/vidas/turnos/estado/cursores y eliminando historial, historial solo de consulta, reinicio que vacía pila e historial.
   - **Evidencia de ausencia:** no existen clases de motor/modelo/historial/snapshots en `src/**` (solo `Main.java`); `docs/trazabilidad.md:5,8,11-12` asigna estas reglas a `CustomStack/GameSnapshot`, `DoublyLinkedList/MoveRecord`, `Levels/GameEngine` y pruebas `StructuresTest/GameEngineTest`, ninguna presente.
   - **Impacto/riesgo:** alto riesgo de rúbrica porque `AGENTS.md:17,24,26` exige reglas explícitas y probadas, pruebas verificables de motor/estructuras y trazabilidad requisito→implementación→prueba con evidencia real.

3. **BFS con cola propia y BST no balanceado: complejidades y componentes declarados, pero no materializados ni medidos**
   - **Evidencia:** `docs/arquitectura.md:16,24-25,27-31` exige BFS estático (ignora enemigos), `CustomQueue<Position>`, `BinarySearchTree` y matriz de predecesores; `SearchResult` con ruta, orden de exploración e instantánea real de la frontera máxima; documenta `BST O(h)` peor `O(n)` y `BFS O((V+E)h)` peor `O(V²)`.
   - **Evidencia de ausencia:** no hay paquete `algorithms` ni `structures` en `src/**`; `docs/trazabilidad.md:6,9-10,14` prevé `CustomQueue`, `BinarySearchTree`, `BreadthFirstSearch`, `SearchResult` y pruebas asociadas, sin artefactos.
   - **Impacto/riesgo:** no es verificable el cumplimiento de `AGENTS.md:13-16` (prohibición de colecciones estándar para esas estructuras, cola propia, BST de visitados, matriz de predecesores y costo real del BST no balanceado documentado en código/pruebas). Riesgo de penalización fuerte en la parte algorítmica/estructural.

### Media

4. **Configuración de VS Code portable/incompleta respecto a ejecución portable declarada**
   - **Evidencia:** `.vscode/tasks.json:4-41` depende de `scripts/maven.sh` y `scripts\maven.cmd` (presentes en `scripts/`: `dev.cmd`, `maven.cmd`, `maven.sh`); `.vscode/launch.json:4-11` lanza `mazequest.Main` (`projectName: mazequest`); `.vscode/settings.json:1-8` solo fija build automático Java, encoding/EOL/indentación; `.vscode/extensions.json:1-6` recomienda Java/Maven.
   - **Evidencia faltante:** no hay configuración portátil explícita de JDK/Maven/toolchain en `.vscode` (p.ej. rutas/runtime específicas), ni evidencia de validación de esas tareas/scripts en este entorno.
   - **Impacto/riesgo:** `AGENTS.md:7` pide preparar VS Code con configuración portable; la dependencia de scripts externos y del entorno local puede dificultar reproducibilidad en otros equipos, aunque la estructura base de tareas/depuración existe.

5. **Trazabilidad y entregables académicos adelantados en documentación, pero sin evidencia de cierre**
   - **Evidencia:** `docs/trazabilidad.md:1-18` parte de “prompt; pendiente contrastar PDF original”, marca “Prueba prevista/Build/Revisión/Renderizado/SHA…” y lista entregables (Javadoc, manuales PDF, JAR, sustentación, GitHub privado). `docs/arquitectura.md:33-34` exige compilación/pruebas por fase y entrega con JAR real, GUI revisada, manuales con capturas reales y registro de pendientes. `AGENTS.md:5,18,26,40` refuerza PDF original, no inventar evidencias, validación limpia y pendientes exactos.
   - **Evidencia:** no se observan en el árbol archivos de pruebas, manuales PDF, guion, capturas, reportes en `docs/reviews/` ni artefactos de build/JAR.
   - **Impacto/riesgo:** la documentación planifica bien, pero hoy no demuestra cumplimiento; declarar avance sin artefactos/pruebas reales contradiría las reglas de calidad y entrega.

### Baja

6. **Calidad del build base: correcta en intención (Java 21/JUnit5/JAR/Javadoc), con márgenes de mejora operativa**
   - **Evidencia:** `pom.xml:4` fija `maven.compiler.release=21` y codificación UTF-8; `pom.xml:5` agrega JUnit Jupiter 5.11.4; `pom.xml:8` configura Surefire headless (`-Djava.awt.headless=true`), alineado con `AGENTS.md:24`; `pom.xml:9` define `mainClass` `mazequest.Main`; `pom.xml:10` activa Javadoc con `failOnWarnings=true`.
   - **Observación:** el POM está muy compacto en una sola línea por bloques (`pom.xml:3-11`), lo que reduce legibilidad/mantenibilidad; además `failOnWarnings` y la ausencia de código/documentación completa harán que la generación de Javadoc sea exigente cuando se implemente (riesgo operativo, no defecto actual demostrado).
   - **Impacto/riesgo:** base razonable para el stack requerido, pero convendrá normalizar formato y validar temprano `compile/test/javadoc/package` conforme crezca el código.

**Resumen:** el repositorio contiene principalmente planificación (`AGENTS.md`, `docs/arquitectura.md`, `docs/trazabilidad.md`, `pom.xml`, `.vscode/*`) y un punto de entrada mínimo (`src/main/java/mazequest/Main.java:10-12`). No hay evidencia implementada de MVC, estructuras propias, motor con colisión/deshacer, BFS/BST ni pruebas/entregables; esos son los riesgos críticos de rúbrica en el estado actual. No se buscaron ni leyeron credenciales.

## Evaluación de Codex
Las ausencias de implementación corresponden a fases futuras y no demuestran defectos del diseño. Se aceptan la necesidad de evidencia por fase, validar VS Code y mejorar legibilidad del POM. No se añadirán rutas absolutas al repositorio: Java 21/Maven son requisitos del entorno y scripts/dev.cmd configura las herramientas locales en Windows.
