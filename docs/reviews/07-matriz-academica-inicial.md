# Auditoría académica inicial: estructuras de MazeQuest

Fecha: 16 de noviembre de 2026. Estado previo a modificar estructuras: rama codex/documentacion-entrega, commit `561114614dc8c5585caf6746feed3d0ff3392917`, árbol de trabajo limpio. No había cambios del usuario por preservar. Se conserva el proyecto y su historial.

Baseline ejecutado con JDK 21.0.13 y Maven 3.9.11: `clean verify javadoc:javadoc`, BUILD SUCCESS a las 01:07 America/Bogota. 24 pruebas: 8 estructuras, 4 BFS, 8 motor, 2 jugabilidad y 2 controlador; cero fallos, errores u omisiones. No se confunde esta ejecución inicial con las validaciones posteriores.

## Fuentes y lectura

Presentaciones de Ana María Caviedes Castillo, suministradas en Downloads. Texto leído completo: Pilas y Colas (45 páginas), Listas (29), Árboles (17), y rúbrica original (4). Se renderizaron los PDF; se inspeccionó visualmente el código de Pilas y Colas pp. 14-21 y 34-42, Listas pp. 9-29, Árboles pp. 9-16. Las capturas de código de las dos primeras presentaciones no aparecen en su extracción textual: la comparación se apoya también en su lectura visual. Numeración: página del PDF, empezando en uno.

| Fuente | SHA-256 |
|---|---|
| 11. Pilas y Colas.pdf | 101f70396c71495f91aaaaeca4dfe1e7af20a7b7cffdca976c4eaa084903be8a |
| 12. Listas - Modo de compatibilidad.pdf | c7cb20a33db168e0a7e5b5d79e9c5358131029051adfcc6145fa55b523bb08e0 |
| 13. Arboles.pdf | 1fbf158f4e6173831ca57fbdc6401f3538ba5e9ebfcd9245ac2b8f190fbb7866 |
| Proyecto final - Estructuras de Datos.pdf | 78140ee7e7d79fa5fe9733a07d7adfe5199cd4c7d8da0e99eceaf0d8b9b76f22 |

Los originales y sus renderizados temporales no se incorporan al repositorio. Los nombres anteriores normalizan espacios y guiones para lectura.

## Matriz antes de refactorizar

| Estructura / código actual | Referencia académica | Diferencia e impacto | Decisión |
|---|---|---|---|
| CustomStack: Node, top, push/pop/peek, size/isEmpty; inserción en una expresión | Pilas pp. 34-40: Nodo, inicio, nuevo; enlaces explícitos | Estilo. LIFO, vacío y operaciones completos. La rúbrica no obliga nombres literales. | Nodo, tope, dato/sig, pasos explícitos; operaciones en español y delegación desde API previa. |
| CustomQueue: first/last; limpia ambos al retirar singleton | Colas pp. 14-21: inicio/final o tope/fin, Nodo separado o interno | Estilo. Misma lógica FIFO y O(1), sin operación ausente. | inicio/fin, Nodo, encolar/desencolar/verFrente; explicar vacío y singleton. |
| CircularLinkedList: tail.next identifica inicio; addLast llama addFirst y rota tail; recorrido contado | Listas pp. 9-18: cabecera y aux, cierre de ciclo, do-while | Representación válida; la rotación compacta dificulta seguir las diapositivas. | cabecera y ultimo explícitos; inserciones separadas; do-while para una vuelta. Conservar cursor O(1) y validación de cambios. |
| DoublyLinkedList: first/last, next/prev; búsqueda desde extremo próximo | Listas pp. 19-29: cabecera móvil, sig/ant y recorridos opuestos | Variante válida: dos extremos evitan recorrer antes de cada inserción. | Conservar ambos extremos con nombres españoles, expandir condicionales y explicar enlaces recíprocos. |
| BinarySearchTree: root/key/left/right; inserción while(true); recorridos con pila explícita | Árboles pp. 11-15: raiz/info/izq/der, p/q, inorden recursivo | No hay fallo de orden ni operaciones ausentes. Recorrido iterativo no coincide con recursividad solicitada ahora. | Inserción con actual/anterior; tres recorridos recursivos; eliminación completa se conserva y explica. Documentar límite de pila Java. |

En las cinco estructuras: nodos propios, ninguna colección prohibida como sustituto, sin streams ni lambdas en su lógica interna. Los consumidores de recorrido son callbacks para entregar datos al panel o a una prueba; no implementan enlaces ni orden. Se conservarán para separar salida y estructura.

## Decisiones que no requieren reescribir el juego

- Genéricos sencillos: conservar T para GameSnapshot, Position y MoveRecord. Usar solo int como en las diapositivas perdería los datos del juego; Object introduciría conversiones inseguras; duplicar estructuras por tipo no aporta un beneficio académico.
- Nodos internos: la propia diapositiva de Colas p. 21 incluye Nodo interno y acceso directo a campos. Mantenerlo privado evita que la vista rompa enlaces. No se añaden getters/setters que expongan referencias sin necesidad.
- Contador de tamaño: mantener O(1), actualizado en cada inserción/eliminación; las pp. 20/41 cuentan mediante recorrido O(n). Ambas versiones son correctas.
- Excepciones de vacío: conservar NoSuchElementException, sin confundir vacío con Integer.MAX_VALUE o null, ambos datos admisibles según el tipo. Las diapositivas también presentan excepciones al consultar vacío.
- API anterior: los métodos ingleses necesarios para compatibilidad delegarán en la operación académica, sin duplicar algoritmos. El motor y BFS usarán las operaciones españolas de pila, cola y árbol.
- Recursión: O(n) tiempo y O(h) pila de llamadas. Los niveles actuales tienen a lo sumo 273 celdas; no se promete profundidad ilimitada para árboles externos degenerados. La prueba de 2000 nodos existente debe mantenerse y ejecutarse, no ocultarse.

## Diferencias técnicas necesarias respecto de las diapositivas

| Página | Observación comprobada en el fragmento | Tratamiento correcto |
|---|---|---|
| Pilas/Colas 14-15, 34-35; Listas 9-10 y 19-20 | Varios constructores escritos con void no son constructores Java. | Constructor sin tipo de retorno e inicialización explícita. |
| Colas 15-19 | final se usa como identificador, pero es palabra reservada. La p. 21 usa fin. | Referencia fin. |
| Colas 19 | retirar avanza inicio sin limpiar final al quitar el único nodo. | Vaciar ambos extremos. |
| Listas 10-14 | cabecera/Cabecera alternan mayúsculas. | Un identificador consistente. |
| Listas 17 | EliminaCabecera compara aux con cabecera después de moverla; con dos nodos puede descartar también el sobreviviente. | Tratar singleton antes de modificar; probar dos nodos por separado. |
| Listas 18 | El fragmento de eliminación por valor usa encontrado en la condición de repetición y está incompleto. | Búsqueda termina al encontrar o completar la vuelta; eliminación por índice validado cubre la rúbrica. |
| Listas 19, 20, 23, 25 | dato/valor, getAnt/getAnterior y ABuscar/valor no son consistentes. | Campos y parámetros consistentes. |
| Listas 26 | Al retirar primero falta limpiar ant del nuevo inicio; en la rama interior no se avanza aux tras desconectarlo. | Reparar ambas direcciones y finalizar una eliminación. |
| Árboles 11-16 | Se muestran inserción e inorden; se invocan preorden/posorden sin su implementación en esas páginas. No se desarrolla eliminación. | Completar los tres órdenes y eliminación hoja/un hijo/dos hijos exigidos por la rúbrica. |

## Orden de trabajo

Auditoría OpenCode de solo lectura, sin --auto y con ayuda instalada comprobada. Codex decide los cambios. Adaptar pila, cola, circular, doble y árbol por separado; ejecutar pruebas de estructura e integración después de cada paso y registrar commit descriptivo. Al final: validación limpia, inicio real del JAR, manual técnico y matriz final actualizados. Main no se fusiona sin autorización.
