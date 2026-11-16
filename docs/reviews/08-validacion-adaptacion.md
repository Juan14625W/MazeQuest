# Validación incremental de la adaptación académica

Referencia inicial: 07-matriz-academica-inicial.md. Auditoría independiente y límites: 07-opencode-academica.md. JDK 21.0.13, Maven 3.9.11, Windows; todas las pruebas aquí indicadas se ejecutaron sin ventanas. No se fusiona main.

## Pila

Nodo/dato/sig y tope/cantidad explícitos. Operaciones académicas en español; los nombres previos delegan sin duplicar lógica. GameEngine usa apilar/desapilar/esVacia/tamano. T sigue guardando GameSnapshot con seguridad de tipos. No cambian las reglas ni la vista.

Comando: `mvn -B -ntp -Dtest=StructuresTest,GameEngineTest,PlayableLevelsTest,GameControllerTest test javadoc:javadoc`. Resultado 16/11/2026 03:11 America/Bogota: 20 pruebas, cero fallos/errores/omisiones; Javadoc correcto. Incluye restauración de daño, victoria, derrota y fases enemigas. Los tres niveles siguen ganándose en 16/26/32 turnos con tres vidas.

## Cola

Nodo/dato/sig, inicio/fin y cantidad explícitos. Operaciones encolar/desencolar/verFrente/esVacia/tamano y nombres previos delegados. BFS usa las operaciones españolas; la frontera conserva FIFO. Se mantiene limpieza de ambos extremos al retirar singleton.

Comando: `mvn -B -ntp -Dtest=StructuresTest,BreadthFirstSearchTest,PlayableLevelsTest,GameControllerTest test javadoc:javadoc`. Resultado 16/11/2026 03:14: 16 pruebas, cero fallos/errores/omisiones; Javadoc correcto. BFS conserva distancia mínima, falta de ruta, origen igual a destino y validación de extremos; las pistas siguen publicándose en EDT. Victorias con tres vidas en 16/26/32 turnos.
