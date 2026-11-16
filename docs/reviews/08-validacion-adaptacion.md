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

## Árbol y validación limpia de las cinco estructuras

Nodo/info/izq/der y raiz; inserción por comparación con actual/anterior (equivalentes a q/p). Buscar y eliminar conservan sus contratos; eliminación explica hoja, un hijo y sucesor inorden. Recorridos recursivos reales inorden/preorden/posorden usados por SearchResult. Los nombres previos delegan y BFS inserta mediante insertar. No hay pila explícita en estos recorridos.

Nueva prueba classroomTreeTraversals reproduce el árbol de Árboles p. 10: inorden `3 5 7 10 11 12 15`, preorden `10 5 3 7 12 11 15`, posorden `3 7 5 11 15 12 10`; también elimina la raíz con dos hijos. Se mantiene degenerateTree con 2000 nodos y se amplía a los tres recorridos. Pasar ese caso no garantiza profundidad arbitraria en otras JVM.

Comando: `mvn -B -ntp clean verify javadoc:javadoc`. Resultado 16/11/2026 03:34: **27 pruebas, cero fallos, errores u omisiones**, Javadoc correcto, JAR ejecutable generado. Distribución: estructuras 11, BFS 4, motor 8, jugabilidad 2, controlador 2. Las victorias con enemigos mantienen 16/26/32 turnos y tres vidas.
