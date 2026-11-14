# Auditoría de solo lectura — `mazequest.structures`

**Alcance leído:** `src/main/java/mazequest/structures/{BinarySearchTree,CircularLinkedList,CustomQueue,CustomStack,DoublyLinkedList}.java` y `src/test/java/mazequest/structures/StructuresTest.java`. No se modificaron archivos ni se ejecutaron comandos de escritura.

## Resumen
- **Estructuras:** implementaciones propias con nodos/enlaces (sin `Stack`, `Queue`, `ArrayDeque`, `LinkedList`, `ArrayList`, `HashSet`, `HashMap`, `TreeSet`, `TreeMap` ni equivalentes). `BinarySearchTree` usa `CustomStack<Node>` para recorridos iterativos.
- **Defectos concretos encontrados:** documentación Javadoc incompleta en clases internas (incumplimiento de “documentar en español todas las clases, constructores y métodos”).
- **Defectos funcionales/lógicos concretos detectados:** ninguno evidente en las operaciones revisadas (invariantes, enlaces, circularidad, duplicados BST y casos de eliminación observados coherentes con la implementación y las pruebas existentes).

## Defectos reales (documentación)

| Archivo | Clase/Método | Hallazgo |
|---|---|---|
| `src/main/java/mazequest/structures/CustomStack.java` | `CustomStack.Node` (clase interna, línea ~13) | Clase interna sin Javadoc de clase en español. Tiene Javadoc de constructor (`Node(T value, Node<T> next)`), pero falta la documentación propia de la clase. |
| `src/main/java/mazequest/structures/CustomQueue.java` | `CustomQueue.Node` (clase interna, línea ~13) | Clase interna sin Javadoc de clase en español. Constructor documentado. |
| `src/main/java/mazequest/structures/CircularLinkedList.java` | `CircularLinkedList.Node` (clase interna, línea ~16) | Clase interna sin Javadoc de clase en español. Constructor documentado. |
| `src/main/java/mazequest/structures/DoublyLinkedList.java` | `DoublyLinkedList.Node` (clase interna, línea ~14) | Clase interna sin Javadoc de clase en español. Constructor documentado. |
| `src/main/java/mazequest/structures/BinarySearchTree.java` | `BinarySearchTree.Node` (clase interna, línea ~11) | Clase interna sin Javadoc de clase en español. Constructor documentado. |

**Nota:** las clases públicas, sus constructores y métodos públicos/privados relevantes (`CustomStack`/`CustomQueue`/`CircularLinkedList`/`DoublyLinkedList`/`BinarySearchTree`, incluidos `Cursor`, constructores y operaciones) sí presentan Javadoc en español en los archivos revisados; el incumplimiento concreto observado es la ausencia de Javadoc a nivel de las clases internas `Node`.

## Verificación por estructura (operaciones, invariantes y límites observados)

### `CustomStack<T>` (`CustomStack.java`)
- **Operaciones:** `push`, `pop`, `peek`, `size`, `isEmpty`, `forEach`; constructor `CustomStack()`. Todas presentes.
- **Invariantes/enlaces:** pila LIFO por `top`; `push` enlaza nuevo nodo sobre `top` (`top = new Node<>(value, top)`); `pop` avanza `top = top.next` y decrementa tamaño; no hay nodo centinela.
- **Límites:** `pop`/`peek` lanzan `NoSuchElementException("Pila vacía")` si vacía; admite `null` (`Node.value` final sin restricción), coherente con prueba.
- **Complejidades documentadas/consistentes:** push/pop/peek/size/isEmpty O(1), recorrido `forEach` O(n), espacio O(n). Sin defectos concretos detectados.

### `CustomQueue<T>` (`CustomQueue.java`)
- **Operaciones:** `enqueue`, `dequeue`, `peek`, `size`, `isEmpty`, `forEach`; constructor `CustomQueue()`.
- **Invariantes/enlaces:** FIFO con `first`/`last`; en cola vacía `enqueue` asigna ambos extremos al mismo nodo; `dequeue` avanza `first` y, si queda vacía, limpia `last = null` (evita extremo huérfano).
- **Límites:** `dequeue`/`peek` lanzan `NoSuchElementException("Cola vacía")`; admite `null`.
- **Complejidades:** operaciones básicas O(1), recorrido O(n), espacio O(n). Sin defectos concretos detectados.

### `CircularLinkedList<T>` (`CircularLinkedList.java`)
- **Operaciones:** `addFirst`, `addLast`, `insert`, `remove`, `indexOf`, `get`, `size`, `isEmpty`, `forEach`, `cursor`; cursor `value`, `advance`, `index`, `seek`.
- **Circularidad/invariantes:** lista representada por `tail`; cuando no vacía `tail.next` es la cabeza y el último enlaza al primero. Singleton: `addFirst` crea `n.next = n`, `tail = n`. `remove` en tamaño 1 hace `tail = null`; en otros casos re-enlaza `prev.next = victim.next` y actualiza `tail` si se elimina el último. `forEach` recorre exactamente `size` nodos desde la cabeza.
- **Enlaces/edición:** `insert` preserva ciclo (conecta nuevo nodo antes de sustituir enlace previo), con casos borde 0 y `size`. `remove(index)` usa `nodeAt(index)` y para cabeza toma `prev = tail`.
- **Límites/excepciones:** `insert` valida `[0,size]`; `remove/get/nodeAt/seek` validan `[0,size)` con `IndexOutOfBoundsException`; `cursor()` lanza `NoSuchElementException("Patrulla vacía")` si vacía; cursor invalida tras modificación estructural (`version`/`expectedVersion` → `ConcurrentModificationException`).
- **Búsqueda/duplicados/nulos:** `indexOf` usa `Objects.equals` (admite `null`, devuelve primera coincidencia). `get` es O(n) según documentación/implementación.
- **Complejidades:** extremos O(1) en inserción según doc (`addLast` reutiliza `addFirst` y mueve `tail`), operaciones por índice/búsqueda O(n), avance de cursor O(1), `seek` O(n). Observación no defectuosa: `addLast` provoca dos incrementos de `version` (vía `addFirst`), lo que invalida cursores de forma conservadora.

### `DoublyLinkedList<T>` (`DoublyLinkedList.java`)
- **Operaciones:** `addFirst`, `addLast`, `insert`, `remove`, `get`, `indexOf`, `size`, `isEmpty`, `forEach`, `forEachReverse`.
- **Invariantes/enlaces:** doble enlace `next/prev`; extremos externos nulos. `addFirst/addLast` actualizan extremo opuesto en lista vacía. `insert` en medio enlaza en ambas direcciones antes de fijar `right.prev`. `remove` reconecta vecino previo/siguiente y actualiza `first/last` en extremos.
- **Límites:** `insert` `[0,size]`, `get/remove/nodeAt` `[0,size)` con `IndexOutOfBoundsException`.
- **Búsqueda/nulos:** `indexOf` con `Objects.equals`, primera coincidencia, -1 si no existe.
- **Complejidades:** extremos O(1), índice/búsqueda O(n), recorridos O(n); `nodeAt` optimiza desde extremo más cercano. Sin defectos concretos detectados.

### `BinarySearchTree` (`BinarySearchTree.java`, claves `int`)
- **Operaciones:** `insert`, `contains`, `remove`, `size`, `isEmpty`, `inOrder`, `preOrder`, `postOrder`.
- **Invariantes BST/duplicados:** izquierda `< key`, derecha `> key`; `insert` retorna `false` y no modifica tamaño si `key == n.key` (duplicados ignorados). `contains` navega por comparación.
- **Eliminación BST:** localiza nodo; caso dos hijos sustituye por sucesor inorden (mínimo del subárbol derecho), copia clave y elimina el sucesor (que tiene a lo sumo hijo derecho) con reconexión simple; casos hoja/un hijo reconectan `child`; actualización de raíz/parent correcta.
- **Límites/complejidad real:** operaciones O(h), peor caso O(n) en árbol degenerado (sin balanceo), recorridos O(n), espacio total O(n). Recorridos iterativos con `CustomStack<Node>` y auxiliar O(h), evitando recursión de pila Java (coherente con prueba degenerada).
- **Documentación de coste:** la clase documenta explícitamente ausencia de balanceo y costes O(h)/O(n) y uso de pila propia.

## Pruebas revisadas (`StructuresTest.java`) — cobertura pendiente vs. errores reales
- **Cobertura ejercitada (presente):** contratos LIFO/FIFO, excepciones en vacío, `null`, `forEach`, inserciones/búsquedas/eliminaciones y circularidad/cursor en `CircularLinkedList`, enlaces bidireccionales y recorridos inversos en `DoublyLinkedList`, recorridos in/pre/postorden, duplicados ignorados y casos de eliminación BST (hoja, un hijo, raíz con dos hijos, vaciado), y árbol degenerado grande sin recursión.
- **Cobertura pendiente (no implica error real observado):**
  - No hay prueba directa de `CircularLinkedList.get(int)` aislado (se ejerce indirectamente por `nodeAt`/`seek`/cursor y otras operaciones).
  - No hay pruebas explícitas de múltiples eliminaciones consecutivas de la cabeza/cola en `DoublyLinkedList` más allá de los casos incluidos, ni de `indexOf` con elementos repetidos en listas (solo primera ocurrencia, comportamiento implícito por `Objects.equals`).
  - No hay pruebas de estrés de `CustomQueue`/`CustomStack` con muchos elementos (las estructuras son simples y O(1)/O(n) esperable), ni pruebas que verifiquen explícitamente ausencia de fugas lógicas más allá de vaciado (`last` en cola sí se valida indirectamente por reuso).
  - `BinarySearchTree`: no se prueba explícitamente eliminación de nodo con dos hijos cuando el sucesor es hijo derecho directo en todos los escenarios posibles, aunque la lógica implementada cubre ese caso (el bucle de sucesor no avanza y `parent` queda apuntando al nodo sustituido); esto se considera **cobertura pendiente**, no error demostrado por la lectura/pruebas actuales.

## Conclusión
- **Errores reales concretos:** 5 hallazgos de documentación (falta Javadoc de clase en `Node` internos de las cinco estructuras), con archivo y clase indicados.
- **Sin errores funcionales concretos identificados** en invariantes, circularidad, enlaces, manejo de duplicados BST, eliminaciones BST, límites/excepciones y complejidades declaradas según la inspección estática y las pruebas existentes; los puntos restantes se clasifican como cobertura pendiente.