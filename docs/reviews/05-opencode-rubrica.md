# Revisión externa: contraste del enunciado

OpenCode, agente plan, sesión ses_e1f56ee04ffe6MBBj5ngQW4CK9. Fecha: 15/11/2026. Ayuda instalada consultada antes de ejecutar; sin --auto. Transcripción de su respuesta final. Las pruebas independientes y la lectura del PDF las realizó Codex, no OpenCode.

## Auditoría de solo lectura (sin ediciones, comandos ni delegación)

Alcance revisado: `README.md`, `docs/manual-tecnico.md`, `docs/manual-usuario.md`, `docs/trazabilidad.md`, `scripts/maven.cmd`, `scripts/dev.cmd`, `src/main/java/mazequest/engine/GameEngine.java`.

### 1. Requisitos de autoría / README
- **Faltante — integrantes en README**: `README.md:77` indica “Datos académicos de autores pendientes de confirmación” y `README.md:78` no lista integrantes; no se cumple el requisito de que el README incluya integrantes. (`README.md:77-78`).
- **No verificable desde los archivos auditados — grupos de 2 o 3 con commits de todos**: los archivos revisados no permiten comprobar la cantidad de integrantes ni la existencia de commits de todos. `README.md:7` y `README.md:18` mencionan commit/CI, pero no evidencian autoría por integrante. (`README.md:7,18,77-78`; evidencia de commits no verificable en este alcance).

### 2. Manual técnico
- **Faltante — diagrama de clases**: `docs/manual-tecnico.md:16-24` presenta una “tabla de paquetes/responsabilidades”, no un diagrama de clases. No se identificó un diagrama de clases en el manual. (`docs/manual-tecnico.md:16-24`).
- **Presente (parcialmente verificable) — uso real/justificación y complejidad por estructura**: `docs/manual-tecnico.md:34-39` incluye tabla por estructura con “Invariante y uso” y “Costos”; `docs/manual-tecnico.md:32,41-46` amplían justificación/invariantes/costos. (Sin incumplimiento concreto detectado en este punto dentro del alcance).
- **Faltante — pruebas con esperado y obtenido**: `docs/manual-tecnico.md:85-96` resume validación y clases de prueba (cantidades y evidencia principal), pero no presenta, por prueba/caso, los valores de **esperado** y **obtenido** exigidos. (`docs/manual-tecnico.md:85-96`).
- **No verificable desde los archivos auditados — exactitud de los resultados de pruebas citados**: las cifras/afirmaciones de pruebas en `docs/manual-tecnico.md:85,89-95` no pueden contrastarse aquí con reportes de ejecución ni con el código de pruebas (no incluido en el alcance).

### 3. Manual de usuario
- **Sin incumplimiento concreto detectado en los puntos exigidos**: incluye instalación/preparación (`docs/manual-usuario.md:9-24`), reglas (`docs/manual-usuario.md:7,46-56,66-69`), controles (`docs/manual-usuario.md:35-45`), capturas/imágenes (`docs/manual-usuario.md:26,33,79`), y condiciones de ganar/perder (`docs/manual-usuario.md:54`). La validez visual/operativa real de las capturas y pasos no es verificable solo por lectura.

### 4. Javadoc con excepciones pertinentes (`AGENTS.md`, `GameEngine.java`)
- **Omisión — excepciones no documentadas en Javadoc**:
  - Constructor: valida `maze` con `Objects.requireNonNull(...)` y puede lanzar `NullPointerException` si es `null`, pero el Javadoc no declara `@throws`. (`src/main/java/mazequest/engine/GameEngine.java:18-24`).
  - `historyAt(int index)`: delega en `history.get(index)`; un índice inválido puede producir excepción de la estructura subyacente, no documentada con `@throws`. (`src/main/java/mazequest/engine/GameEngine.java:168-175`).
  - `enemyPosition(int index)`, `enemyIndex(int index)` y `patrolText(int index)`: acceden a `enemies[index]` y pueden lanzar `ArrayIndexOutOfBoundsException` (u otra según uso), sin `@throws` pertinente en Javadoc. (`src/main/java/mazequest/engine/GameEngine.java:219-246`).
- **Presencia parcial**: la clase y los métodos auditados cuentan con Javadoc en español y, en general, documentan parámetros/retornos donde aplica (`src/main/java/mazequest/engine/GameEngine.java:7,18-22,30,34-39,52-58,86-90,104-246`).

### 5. Trazabilidad y otros archivos revisados
- **Observación de trazabilidad (no verificable como conformidad total)**: `docs/trazabilidad.md:2` reconoce fuente parcial y PDF original pendiente; `docs/trazabilidad.md:13` declara revisión GUI parcial/no exhaustiva y `docs/trazabilidad.md:20` limita la evidencia. La correspondencia completa “requisito → implementación → prueba” no es verificable íntegramente con este subconjunto de archivos. (`docs/trazabilidad.md:2,13,20`).
- **Sin hallazgos concretos frente a los requisitos enunciados** en `scripts/maven.cmd` y `scripts/dev.cmd`: son scripts auxiliares de entorno/ejecución (`scripts/maven.cmd:1-12`, `scripts/dev.cmd:1-8`); no aportan evidencia sobre diagrama de clases, esperado/obtenido, integrantes, commits ni Javadoc.

### Resumen breve
- **Faltantes concretos**: integrantes en README (`README.md:77-78`), diagrama de clases en manual técnico (`docs/manual-tecnico.md:16-24`), formato de pruebas con esperado y obtenido (`docs/manual-tecnico.md:85-96`), y `@throws` pertinentes en varios métodos/constructor de `GameEngine` (`src/main/java/mazequest/engine/GameEngine.java:18-24,168-175,219-246`).
- **No verificables en este alcance**: commits de todos los integrantes, resultados reales de pruebas citados, validez operativa/visual completa de capturas y conformidad total frente al PDF original. No se identificaron otros incumplimientos concretos en los archivos y requisitos revisados.