# Adaptación académica de las estructuras

Fecha: 16 de noviembre de 2026. Equipo: Juan David Paz Rodriguez y Andrea Fernanda Sapuyez.

La revisión inicial no encontró operaciones obligatorias ausentes ni algoritmos incorrectos por usar nombres ingleses. La adaptación hace explícitos nodos, enlaces y casos límite, y cambia los recorridos del BST a recursividad conforme a la solicitud. Se conserva el videojuego existente.

## Fuentes y alcance

Se leyó el texto de las tres presentaciones y se inspeccionaron sus imágenes de código: Pilas y Colas pp. 14–21 y 34–42; Listas pp. 9–29; Árboles pp. 9–16. También se contrastó el PDF original del proyecto. La [matriz inicial](reviews/07-matriz-academica-inicial.md) conserva páginas, hashes, diferencias de estilo, requisitos y erratas antes de editar.

[OpenCode](reviews/07-opencode-academica.md) realizó una auditoría de solo lectura del código y el texto extraído. Su modelo no pudo leer imágenes; esa limitación consta en el informe. Codex realizó la inspección visual y decidió las correcciones. No se atribuyen a OpenCode pruebas ni revisiones visuales que no hizo.

## Evaluación final

«Coincide» significa que conserva los conceptos y la lógica enseñados, con las variantes justificadas debajo; no es una copia literal de las diapositivas. Todas las pruebas citadas se ejecutaron, con resultados por fase en [08-validacion-adaptacion.md](reviews/08-validacion-adaptacion.md).

| Estructura | Coincide con clase | Operaciones completas | Funciona en juego | Pruebas | Documentación |
|---|---|---|---|---|---|
| Pila | Nodo, dato/sig, tope; enlaces LIFO explícitos | apilar, desapilar, verTope, esVacia, tamano | GameEngine guarda GameSnapshot y restaura el turno completo | stackContract; colisión, victoria, derrota y deshacer | Javadoc español, O(1) por operación, O(n) recorrido; diagrama y manual |
| Cola | Nodo, inicio/fin; enlaces FIFO y vacío de ambos extremos | encolar, desencolar, verFrente, esVacia, tamano | Frontera real de BreadthFirstSearch | queueContract; cuatro casos BFS; controlador en EDT | Javadoc, O(1), singleton, diagrama y costo real de BFS |
| Circular | cabecera, ultimo, sig; último enlaza primero; do-while | addFirst, addLast, insert, remove, indexOf, forEach | Enemy conserva un cursor que avanza por la patrulla | circularInsertAndCycle, circularRemove, circularTwoNodesAndCursorInvalidation; fases y victorias | Javadoc de enlaces, cursor y versión; costos; diagrama |
| Doble | Nodo, sig/ant; inicio/fin y reparación simétrica | addFirst, addLast, insert, remove, indexOf, forEach, forEachReverse | Historial de MoveRecord en ambos sentidos | doublyLinks, doublyEndpointsAfterRemoval; victoria/historial/deshacer | Javadoc de cuatro enlaces, extremos, búsqueda desde extremo próximo y costos |
| BST | Nodo/info/izq/der, raiz; comparación y tres recorridos recursivos | insertar, buscar, eliminar, inorden, preorden, posorden | Registra claves de posiciones visitadas en BFS; SearchResult usa los tres órdenes | treeTraversals, treeRemoveCases, classroomTreeTraversals, degenerateTree; BFS | Javadoc de tres casos de eliminación, O(h), O(n), pila Java y límite de profundidad |

Código: [structures](../src/main/java/mazequest/structures/). Integración: [GameEngine](../src/main/java/mazequest/engine/GameEngine.java), [Enemy](../src/main/java/mazequest/model/Enemy.java), [BreadthFirstSearch](../src/main/java/mazequest/algorithms/BreadthFirstSearch.java) y [SearchResult](../src/main/java/mazequest/algorithms/SearchResult.java).

## Casos concretos para la sustentación

1. **Pila:** en El umbral, cuatro movimientos a derecha conservan tres vidas; el quinto causa daño y reaparición. Deshacer recupera el cuarto turno, tres vidas, jugador y fase del guardián. Caso automatizado `presentationCollisionOccursOnFifthStep`; no se guarda solo la posición del jugador.
2. **Cola:** al pedir H, BFS encola el origen y descubre vecinos por capas. El caso `knownShortestPath` usa el mapa `S.#.E / .....` y verifica seis pasos para rodear el muro. El laboratorio muestra una copia de la máxima frontera, no una cola decorativa ni una animación en vivo.
3. **Circular:** cada turno válido del jugador mueve el cursor del guardián a `sig`; al llegar al último nodo vuelve a cabecera. `circularTurnsAndRestoration` comprueba la fase exacta después de deshacer. `circularTwoNodesAndCursorInvalidation` cubre el error de eliminar cabecera entre dos nodos que no debe copiarse de la diapositiva.
4. **Doble:** el motor añade un MoveRecord al final en cada turno válido; el panel consulta el historial en ambos sentidos y deshacer retira el último. `victoryAndHistory` verifica el contrato y `doublyEndpointsAfterRemoval` comprueba que al recorrer desde el extremo opuesto no reaparezcan nodos eliminados.
5. **BST:** H registra cada celda con clave `fila * columnas + columna`; el panel permite ver sus tres órdenes. `classroomTreeTraversals` reproduce el árbol de la p. 10, insertando `10,5,12,3,7,11,15`: inorden `3 5 7 10 11 12 15`, preorden `10 5 3 7 12 11 15`, posorden `3 7 5 11 15 12 10`. Eliminar 10 prueba la raíz con dos hijos. Es un caso didáctico distinto del árbol de claves del laberinto.

## Variantes justificadas

- **Genéricos sencillos:** T guarda GameSnapshot, Position o MoveRecord sin duplicar estructuras ni convertir Object. Los algoritmos de enlaces siguen siendo básicos. El BST guarda int.
- **Nodo interno y privado:** compatible con la segunda cola de la p. 21. Impide que la vista modifique enlaces. Los accesores públicos de nodos no aportan una ventaja en este proyecto.
- **fin y tamano:** `final` es palabra reservada; `fin` también aparece en clase. `tamano` representa «tamaño» con identificadores ASCII. Los nombres anteriores push/pop/peek/size/isEmpty, enqueue/dequeue y los del BST delegan, sin duplicar algoritmos.
- **Extremos y contador:** ultimo en circular e inicio/fin en doble conservan inserción O(1). cantidad permite tamaño O(1); contarlo recorriendo, como en algunos ejemplos, sería O(n). Ambas representaciones son correctas.
- **Consumer/IntConsumer:** entregan el dato visitado a la vista o a las pruebas. No deciden enlaces ni orden; sustituyen imprimir en consola. No hay streams ni lambdas en la lógica interna de estas estructuras.
- **Recursión:** los tres recorridos del árbol son recursivos, O(n) tiempo y O(h) memoria de llamadas. Se ejecutaron con 2000 nodos degenerados en la JVM local; eso no garantiza profundidad ilimitada ni evita StackOverflowError con árboles externos mucho mayores. Los mapas actuales tienen como máximo 273 celdas.
- **Errores de ejemplos:** no se copian constructores con void, nombres inconsistentes, referencias obsoletas ni eliminaciones incompletas. Las páginas y correcciones precisas están en la matriz inicial.

## Validación y límites

`mvn -B -ntp clean verify javadoc:javadoc`, Windows, JDK 21.0.13 y Maven 3.9.11: **27 pruebas, cero fallos, errores u omisiones**, Javadoc correcto y JAR generado. Estructuras: 11; BFS: 4; motor: 8; jugabilidad: 2; controlador: 2. Las tres victorias automatizadas siguen requiriendo 16/26/32 turnos y conservan tres vidas.

La evidencia gráfica y el hash de la construcción se registran en [validacion.md](validacion.md). La comprobación visual no equivale a recorrer exhaustivamente todas las pantallas. Los manuales PDF y el guion de ocho minutos se actualizan con los conceptos y casos anteriores. No se alteran la vista, los assets, los niveles ni las reglas de juego.
