# Correcciones de la auditoría integral

Fecha: 15 de noviembre de 2026. Alcance autorizado: corregir código y documentación conservando cambios y consultando antes de acciones críticas. No se fusionó la rama en main ni se alteró identidad o autoría de Git.

## Hallazgos atendidos

| Hallazgo de la revisión 05 | Corrección | Evidencia |
|---|---|---|
| Falta diagrama de clases | Dos diagramas vectoriales: aplicación y estructuras con nodos privados. | Manual técnico, secciones 1.1/1.2; scripts/class_diagrams.py. |
| Faltan integrantes | Incorporados Juan David Paz Rodriguez y Andrea Fernanda Sapuyez en README y ambos manuales. | Datos confirmados por el solicitante. Los commits individuales siguen siendo un requisito distinto. |
| Faltan casos con esperado/obtenido | Tablas de los 24 métodos JUnit, contrastadas con las aserciones. | Manual técnico, secciones 8/9/10; informes Surefire. |
| Guion indica choque en el sexto paso | Corregido a cinco pasos a la derecha y Z inmediato. | Nueva prueba presentationCollisionOccursOnFifthStep: vida 3→2→3 y restauración de posiciones. |
| Contratos Javadoc incompletos | Añadidos límites de índices y excepciones propagadas en motor, modelos y cursor; aclarado EDT en controlador. | Javadoc con doclint y failOnWarnings correcto. |
| Documentación declara PDF ausente y autoría desconocida | README, arquitectura, trazabilidad y manuales actualizados con el PDF original y equipo confirmado. | Matriz distingue requisitos docentes de compromisos internos y pendientes externos. |
| Defensa individual incompleta | Incorporados los doce minutos y práctica de modificación en vivo para ambos integrantes. | docs/sustentacion.md; ensayo todavía pendiente. |
| Scripts sobrescriben JAVA_HOME | Helper compartido: respeta selección explícita; busca solo JDK 21 local si falta; valida java/javac. | Tres comprobaciones aisladas: explícito 25 conservado, selección local 21 y error de ruta inválida. |
| Justificación y complejidades resumidas | Ubicación exacta, razón de uso y contratos completos de las cinco estructuras. | Manual técnico, secciones 2/2.1; costo del BST no balanceado explícito. |
| Falta prueba permanente de controlador | Dos pruebas sin ventanas: publicación de BFS en EDT e invalidación/reinicio. | GameControllerTest; no se afirma cobertura exhaustiva de carreras o de la vista. |
| Captura de BST anterior al ajuste de línea | Nueva captura real del JAR actual con 51 claves; 74 completa al comienzo de segunda línea. | docs/screenshots/tecnico-actual.jpg; incorporada a ambos PDF. |

## Verificación real

- `clean verify javadoc:javadoc`, JDK 21.0.13 y Maven 3.9.11 en Windows, 15/11/2026 17:13 America/Bogota: BUILD SUCCESS. 24 pruebas, cero fallos, errores u omisiones. JAR y API generados.
- Distribución: StructuresTest 8, BreadthFirstSearchTest 4, GameEngineTest 8, PlayableLevelsTest 2, GameControllerTest 2.
- El verificador de niveles reprodujo WON con tres vidas en 16, 26 y 32 turnos. Es ejecución automatizada, no una partida humana.
- Selección de Java comprobada con fixtures temporales fuera del árbol versionado; no cambió el entorno persistente del usuario.
- OpenCode, agente plan sin --auto, auditó el diff, contratos, scripts, diagramas y tablas frente al código. No reportó hallazgos concretos. Respuesta íntegra y límites en [06-opencode-correcciones.md](06-opencode-correcciones.md); no ejecutó pruebas ni revisó visualmente los PDF.
- Control de interfaz: se abrió el JAR, se inició El umbral, se calculó H, se abrió T y se seleccionó BST. Se observó el resultado de 51 claves y se archivó la captura sin retocarla. Revisión parcial en Windows.
- Manual de usuario: cuatro páginas. Manual técnico: once páginas. Se renderizaron todas; se inspeccionaron las quince páginas y se volvieron a revisar las afectadas por el ajuste de espaciado y la nueva captura. Sin recortes ni superposiciones observadas.
- `git diff --check` correcto. Cachés Python ignoradas; no se incluyen herramientas, compilados, secretos ni registros brutos en los commits.

## Pendientes que no se deben simular

Contribuciones reales e identificables de Andrea y Juan en Git, ensayo humano y defensa de ambos, acceso privado de la docente y envío final. La fusión en main requiere autorización del solicitante. GUI Linux, breakpoint real de VS Code y recorrido manual exhaustivo de controles siguen pendientes.

La sugerencia de optimizar la serialización del panel técnico en partidas muy largas queda como mejora opcional: no se midió un problema de rendimiento ni se introdujo un rediseño del motor para resolver un riesgo hipotético. Las pruebas de controlador añadidas cubren publicación e invalidación, no toda la vista ni todas las carreras de cancelación.
