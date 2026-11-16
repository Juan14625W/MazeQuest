# Validación incremental de la adaptación académica

Referencia inicial: 07-matriz-academica-inicial.md. Auditoría independiente y límites: 07-opencode-academica.md. JDK 21.0.13, Maven 3.9.11, Windows; todas las pruebas aquí indicadas se ejecutaron sin ventanas. No se fusiona main.

## Pila

Nodo/dato/sig y tope/cantidad explícitos. Operaciones académicas en español; los nombres previos delegan sin duplicar lógica. GameEngine usa apilar/desapilar/esVacia/tamano. T sigue guardando GameSnapshot con seguridad de tipos. No cambian las reglas ni la vista.

Comando: `mvn -B -ntp -Dtest=StructuresTest,GameEngineTest,PlayableLevelsTest,GameControllerTest test javadoc:javadoc`. Resultado 16/11/2026 03:11 America/Bogota: 20 pruebas, cero fallos/errores/omisiones; Javadoc correcto. Incluye restauración de daño, victoria, derrota y fases enemigas. Los tres niveles siguen ganándose en 16/26/32 turnos con tres vidas.

## Cola

Nodo/dato/sig, inicio/fin y cantidad explícitos. Operaciones encolar/desencolar/verFrente/esVacia/tamano y nombres previos delegados. BFS usa las operaciones españolas; la frontera conserva FIFO. Se mantiene limpieza de ambos extremos al retirar singleton.

Comando: `mvn -B -ntp -Dtest=StructuresTest,BreadthFirstSearchTest,PlayableLevelsTest,GameControllerTest test javadoc:javadoc`. Resultado 16/11/2026 03:14: 16 pruebas, cero fallos/errores/omisiones; Javadoc correcto. BFS conserva distancia mínima, falta de ruta, origen igual a destino y validación de extremos; las pistas siguen publicándose en EDT. Victorias con tres vidas en 16/26/32 turnos.

## Lista circular

Cabecera y ultimo explícitos; inserciones separadas; Nodo/dato/sig; búsqueda y recorrido con do-while. Se conserva el cursor de patrulla y su control de versión. Nueva prueba circularTwoNodesAndCursorInvalidation: quitar cabecera de dos nodos conserva el otro, su ciclo y fase cero; los cuatro accesos a un cursor antiguo fallan después de editar la ruta. Se comprueban también último, vacío y reutilización.

Comando: `mvn -B -ntp -Dtest=StructuresTest,GameEngineTest,PlayableLevelsTest,GameControllerTest test javadoc:javadoc`. Resultado 16/11/2026 03:16: 21 pruebas, cero fallos/errores/omisiones; Javadoc correcto. Fases, daño, deshacer y victorias 16/26/32 con tres vidas permanecen correctos.

## Lista doble

Nodo/dato/sig/ant e inicio/fin/cantidad explícitos. Inserción interior muestra las cuatro referencias y eliminación separa anterior/actual/siguiente. Se conserva búsqueda desde el extremo próximo y la API del historial. Nueva prueba doublyEndpointsAfterRemoval verifica que no se vuelva a visitar un nodo eliminado al recorrer desde el extremo opuesto y permite reutilizar la lista vaciada.

Comando: `mvn -B -ntp -Dtest=StructuresTest,GameEngineTest,PlayableLevelsTest,GameControllerTest test javadoc:javadoc`. Resultado 16/11/2026 03:24: 22 pruebas, cero fallos/errores/omisiones; Javadoc correcto. Historial en ambas direcciones, deshacer y victoria/derrota mantienen sus contratos.
