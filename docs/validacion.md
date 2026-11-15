# Evidencia de validación
Fecha: 14 noviembre 2026. Plataforma local: Windows 11 x64.

## Pruebas automatizadas ejecutadas
21 pruebas JUnit: StructuresTest (8), GameEngineTest (7), BreadthFirstSearchTest (4), PlayableLevelsTest (2).
Resultado verificado en compilación limpia: cero fallos, cero errores, cero omitidas, tanto en Java 21.0.13 como en Java 25.0.5.
Javadoc con doclint y failOnWarnings: exitoso en ambas versiones.
La comprobación Java 21 se aisló en .tools/check21 para evitar que la compilación automática de VS Code (Java 25) mezclara bytecode en target.

## Casos adicionales que corrigieron defectos
- El tercer mapa era resoluble sin enemigos, pero imposible con las patrullas iniciales. La búsqueda de estados (celda, fase) en pruebas detectó el problema. Se abrieron corredores de paso.
- Las secuencias se reproducen contra GameEngine: El umbral 16 turnos, Las galerías 26, El santuario 32; en los tres casos WON y 3 vidas. Es evidencia automatizada, no una partida humana.
- Se añadió un intercambio auténtico jugador/enemigo y se comprobó pérdida de una vida y restauración completa.
- Se corrigió el ajuste de líneas del panel para no dividir claves numéricas.
- Se corrigió la colisión de nombre move(int,int) con java.awt.Component.
- Falló una compilación incremental Java 21 al encontrar clases generadas por VS Code para Java 25. La compilación limpia aislada pasó; no se ocultó el fallo.

## Evidencia visual real
Se abrió java -jar target/mazequest.jar. Se observaron menú, partida, movimiento con flecha, avance enemigo, pista BFS y panel BST. Se probó Tab y Enter para comenzar.
Capturas originales en docs/screenshots: menu.jpg, juego.jpg, tecnico.jpg.
VS Code abrió el proyecto, reconoció Java/Maven y mostró depuración activa. Se configuró un breakpoint en Main; la detención efectiva no se confirmó antes de interrumpirse el control de interfaz.

## Límites
El PDF original de la docente no fue proporcionado. La autoría académica está pendiente de datos del estudiante.
Solo existe docker-desktop en WSL; no se usó como escritorio Linux. La matriz de GitHub Actions permite verificar compilación y pruebas sin ventanas en Ubuntu/Windows, con Java 21/25; su resultado se registra después de ejecutarla. No equivale a una revisión gráfica en Linux.
Las capturas muestran estados reales; no se presentan como prueba de todas las combinaciones de botones ni de una partida completa manual.

## Continuación del 15 de noviembre de 2026

- Repositorio `Juan14625W/MazeQuest`: visibilidad privada confirmada mediante el conector de GitHub. Commit remoto base `2f9d04b11a6280aa1ac50bf8158ab13672fa76d6` confirmado.
- [GitHub Actions 43664777093](https://github.com/Juan14625W/MazeQuest/actions/runs/43664777093): cuatro trabajos completados con `success`: Ubuntu y Windows, Java 21 y 25. Incluyen compilación, pruebas y Javadoc. La ejecución anterior de `89d7217` falló; la corrección de PowerShell en `2f9d04b` resolvió el problema.
- Se ejecutó `mvn -B -ntp clean verify javadoc:javadoc` con JDK 21.0.13 y Maven 3.9.11 en este worktree: BUILD SUCCESS, 21 pruebas, cero fallos, errores u omitidas. Se repitió después del ajuste de foco con el mismo resultado (11:40 America/Bogota).
- El primer intento aislado falló al acceder a la caché de Maven. El segundo, con permiso para ejecutar fuera del aislamiento, pasó. No fue un error del juego.
- OpenCode realizó la auditoría acotada de controlador y vista. Informe y evaluación en [04-vista-controlador.md](reviews/04-vista-controlador.md). Se corrigió la asignación del foco al botón pulsado. Se descartó un falso positivo de Javadoc comprobando el código. El ajuste de foco tiene compilación y pruebas de regresión generales; no se atribuye una prueba visual específica todavía.
- JAR generado en `target/mazequest.jar`, con Main-Class `mazequest.Main`, compilado para Java 21. SHA-256 de esta construcción: `d139127c9af03f204203bdce320be9433bd7170045d7d925d058fc9920444ac0`. El hash identifica este binario; no se promete identidad de bytes entre compilaciones.
- Manual de usuario de cuatro páginas y manual técnico de cinco páginas generados en `output/pdf`, con fuentes Markdown y generador reproducible. Todas las páginas se renderizaron con PyMuPDF y se inspeccionaron. Se corrigieron dos párrafos que quedaban aislados en páginas adicionales. Se usaron las capturas reales ya archivadas; la de BST precede al arreglo del salto entre claves y así se etiqueta.
- Guion propuesto de ocho minutos y 18 preguntas en `docs/sustentacion.md`. No se presenta como ensayo ya realizado.

Pendientes exactos: recibir y contrastar el PDF original; completar autoría académica confirmada; revisión gráfica en Linux; verificar detención efectiva en breakpoint; ensayo humano del guion y recorrido manual exhaustivo de controles. Ninguna validación automática sustituye esos puntos.

## Correcciones tras recibir la rúbrica, 15 de noviembre de 2026

Este apartado actualiza los pendientes históricos anteriores. El PDF original de cuatro páginas ya se contrastó y el solicitante confirmó a Juan David Paz Rodriguez y Andrea Fernanda Sapuyez como equipo. Trazabilidad actual en [trazabilidad.md](trazabilidad.md); revisión inicial en [05-auditoria-integral.md](reviews/05-auditoria-integral.md) y cierre en [06-cierre-correcciones.md](reviews/06-cierre-correcciones.md).

- Compilación limpia a las 17:13 America/Bogota, JDK 21.0.13 y Maven 3.9.11: **24 pruebas, cero fallos, errores u omisiones**. Javadoc correcto. Las tres nuevas pruebas cubren daño en el quinto paso del guion, publicación de la pista en EDT e invalidación/reinicio del controlador.
- JAR ejecutable generado: `target/mazequest.jar`. SHA-256 de esta construcción: `8e12cbbd25f5afe2b62200ea253b3e71520f503c41bf7303b6852ab7753d2326`.
- Helper `scripts/java-env.cmd` verificado en tres escenarios aislados: conserva JDK 25 explícito aunque exista JDK 21 local; sin JAVA_HOME selecciona JDK 21; una ruta explícita inexistente retorna código 1 con mensaje. Son comprobaciones de selección, no ejecuciones de JDK simulados.
- Manuales regenerados con autoría, diagramas, ubicación/justificación/costos de estructuras y resultados esperados/obtenidos de 24 casos. Usuario: cuatro páginas; técnico: once. Quince páginas renderizadas e inspeccionadas, con revisión posterior de las páginas modificadas.
- Se abrió el JAR actual en Windows y se observó inicio de El umbral, cálculo H, laboratorio T y selección de BST con 51 claves. Nueva captura real `docs/screenshots/tecnico-actual.jpg`; ya no divide la clave 74 entre líneas. La captura anterior se conserva como evidencia histórica.
- Auditoría OpenCode de solo lectura sin hallazgos concretos en su alcance. Respuesta íntegra en [06-opencode-correcciones.md](reviews/06-opencode-correcciones.md). No se presenta como ejecución de pruebas ni inspección visual externa.

Pendientes actuales: contribuciones verificables de ambos integrantes en Git; ensayo y defensa; acceso docente y envío. Integración de la rama en main sujeta a autorización. Siguen pendientes GUI de Linux, detención en breakpoint y recorrido manual exhaustivo; no son sustituidos por CI.
