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
