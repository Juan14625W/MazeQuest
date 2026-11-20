# Evidencia de validación
Fecha: 14 noviembre 2026. Plataforma local: Windows 11 x64.

**Nota de autoría vigente (20 de noviembre de 2026):** Los integrantes actuales, según README, son Juan David Paz Rodriguez, José Ángel Lopez Paz y Carolina Diago Fernandez. Las menciones del grupo anterior en auditorías y registros fechados se conservan como evidencia histórica.

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

Pendientes identificados en la auditoría previa: contribuciones verificables de los integrantes registrados entonces en Git; ensayo y defensa; acceso docente y envío. Integración de la rama en main sujeta a autorización. Siguen pendientes GUI de Linux, detención en breakpoint y recorrido manual exhaustivo; no son sustituidos por CI.

## Adaptación a las presentaciones, 16 de noviembre de 2026

- Estado inicial `5611146`: 24 pruebas y Javadoc correctos antes de modificar fuentes. Se revisaron las tres presentaciones, incluidas las imágenes de código. La [matriz inicial](reviews/07-matriz-academica-inicial.md) distingue estilo de requisitos y conserva hashes y páginas.
- Cinco commits separados adaptan pila, cola, circular, doble y BST. Los nodos y referencias son explícitos; el árbol usa tres recorridos recursivos. Se conservan genéricos sencillos y delegaciones de compatibilidad, con su justificación en la [matriz final](adaptacion-academica.md).
- `mvn -B -ntp clean verify javadoc:javadoc`, JDK 21.0.13 y Maven 3.9.11, a las 03:34 America/Bogota: **27 pruebas, cero fallos, errores u omisiones**, Javadoc correcto y JAR generado. Estructuras 11, BFS 4, motor 8, jugabilidad 2, controlador 2. Validación incremental en [08-validacion-adaptacion.md](reviews/08-validacion-adaptacion.md).
- Casos nuevos: eliminar cabecera entre dos nodos circulares e invalidar cursores; limpiar enlaces de extremos dobles; reproducir los tres órdenes del árbol de la p. 10. Se conservaron 2000 nodos degenerados y se verificaron los tres recorridos recursivos. Ese resultado no garantiza una profundidad ilimitada en la pila Java.
- El JAR de esta construcción tiene SHA-256 `cc21189f5e298af2957c5e144c2a326ee9174913d2de4a908a9ef73363e62266`. La comparación de fuentes con el estado inicial no contiene cambios en la vista, los modelos de niveles ni los assets.
- [OpenCode](reviews/07-opencode-academica.md) auditó código y texto en modo de solo lectura. No pudo procesar imágenes; Codex hizo esa inspección. Se documenta la discrepancia sobre recursión: se atiende la solicitud explícita del estudiante con límites y pruebas.
- Manual de usuario: cuatro páginas. Manual técnico: doce páginas, con diagramas actuales, correspondencia con clase, complejidades y los 27 casos. Se renderizaron e inspeccionaron las 16 páginas; después de corregir las fechas se regeneraron ambos PDF y se revisaron otra vez las tres páginas afectadas. No se observaron cortes ni tablas desbordadas. El guion incluye 22 preguntas para practicar.
- El JAR abrió el menú en Windows. El primer intento de interacción se interrumpió mediante Escape físico del usuario; el segundo encontró el error de automatización `failed to activate captured window`, aunque el proceso seguía activo y respondía. Esto acredita el arranque, no una prueba gráfica completa de las funciones adaptadas. Las capturas anteriores conservan sus fechas reales.

Los pendientes académicos de entrega y las verificaciones de otras plataformas indicados arriba no se consideran resueltos por esta refactorización.

## Continuación desde la nueva carpeta, 16 de noviembre de 2026

La carpeta 1c15 estaba limpia en el commit base 2f9d04b. Se creó codex/cierre-academico desde a9e5cf7, conservando el otro worktree y main. Se ejecutó de nuevo `mvn -B -ntp clean verify javadoc:javadoc` para generar el JAR local: BUILD SUCCESS, 27 pruebas sin fallos, errores u omisiones y Javadoc correcto, JDK 21.0.13. La salida terminó a las 12:00 America/Bogota; no se usa su duración anómala como métrica. SHA-256 del nuevo JAR: `7a8c29e1b6fe05091807c3046ba51fbab9ff5827f799bb774bb3aae49a519a11`.

La [ejecución CI 43801185433](https://github.com/Juan14625W/MazeQuest/actions/runs/43801185433), asociada a a9e5cf7, completó con success sus cuatro trabajos Ubuntu/Windows y Java 21/25, incluidos compilación, pruebas y Javadoc.

La nueva sesión gráfica sí permitió iniciar y controlar la partida. Tras entrada manual del usuario, se observó Las galerías en turno cero. Se ejecutó un paso a derecha: jugador de (1,1) a (1,2), ambos guardianes avanzaron y apareció el registro. Z restauró jugador, ambos guardianes, turno cero e historial vacío. Luego dos pasos a derecha dejaron turno dos, tres vidas y jugador en (1,3). H calculó una pista de 24 pasos y la dibujó hasta la salida. Captura real de ese estado: [juego-academico.jpg](screenshots/juego-academico.jpg).

Posteriormente se detectaron entradas manuales; se observó turno 46 y dos vidas, sin atribuir a Codex la secuencia intermedia. Las cancelaciones de entrada de la herramienta protegen las acciones del usuario y no se presentan como fallos del juego. Esta evidencia actualiza la limitación anterior de arranque, sin convertir la sesión en una prueba exhaustiva de todas las pantallas.

Tras la entrega del control por el usuario se observó El santuario en turno cero. Se ejecutaron dos pasos a derecha y se abrió T. Comprobaciones visuales realizadas:

- Pila: dos estados reversibles; T1 (1,2), vidas 3, antes de T0 (1,1), vidas 3.
- Circular: tres patrullas, cada cursor en índice 2; posiciones (1,11), (7,16) y (11,7), con sus rutas cerradas visibles.
- Historial: dos movimientos en orden #1, #2; «Ver hacia atrás» mostró #2, #1 sin cambiar la partida.
- Cola: «Ejecutar BFS» produjo una frontera máxima de nueve elementos y 135 celdas descubiertas. Se mostró como instantánea histórica, no cola actual.
- BST: 135 claves, fórmula fila * 21 + columna. Se alternaron inorden, preorden y posorden; las secuencias cambiaron y permanecieron legibles. Captura real del último estado: [tecnico-academico.jpg](screenshots/tecnico-academico.jpg).

La prueba gráfica de las funciones afectadas queda realizada en Windows. Los órdenes exactos se verifican además mediante JUnit; una inspección visual de 135 claves no sustituye esas aserciones. No se afirma haber recorrido manualmente todas las pantallas de victoria/derrota ni toda combinación de controles. El juego queda abierto en el laboratorio.
