# Adaptación académica — 19 de noviembre de 2026

## Propósito
Conservar la jugabilidad y la interfaz de MazeQuest y acercar la implementación de las cinco estructuras a la notación y los algoritmos de las diapositivas de la docente. La evaluación exige nodos y referencias propios, todas las operaciones mínimas y uso real de las estructuras.

## Correspondencia de código y presentaciones

| Tema | Código de MazeQuest | Convención de la clase | Uso dentro del juego |
| --- | --- | --- | --- |
| Pila (LIFO) | `CustomStack` con `Nodo`, `inicio`, `siguiente` | Enlace al siguiente nodo y operaciones apilar/desapilar | Guardar instantáneas para deshacer turnos |
| Cola (FIFO) | `CustomQueue` con `inicio`, `fin`, `siguiente` | Encolar por atrás y desencolar por delante | Frontera del algoritmo BFS |
| Lista circular simple | `CircularLinkedList` con `Nodo`, `sig`, `ultimo` | Cada nodo referencia al siguiente y el último enlaza al primero | Rutas circulares de enemigos |
| Lista doble | `DoublyLinkedList` con `sig`, `ant`, `inicio`, `fin` | Doble referencia al siguiente y al anterior | Historial de movimientos en ambas direcciones |
| Árbol binario | `BinarySearchTree` con `raiz`, `izq`, `der` | Recorridos recursivos inorden/preorden/posorden con `if (aux != null)` | Claves de celdas exploradas en BFS |

Los métodos públicos del juego se conservaron en inglés para no modificar todos los módulos: `push/pop/peek`, `enqueue/dequeue/peek`, `addFirst/addLast`, `inOrder/preOrder/postOrder`. Lo importante en la sustentación es explicar su equivalente en español, no renombrar artificialmente todos los puntos de entrada.

## Qué explicar en la sustentación
- Pila: cada nodo guarda un estado de la partida y enlaza al anterior. El último estado guardado es el primero que se recupera.
- Cola: la búsqueda en anchura encola nuevas posiciones por `fin` y desencola desde `inicio`, respetando FIFO.
- Lista circular: los enemigos siguen los enlaces `sig`; al finalizar la ruta regresan al primer nodo.
- Lista doble: cada movimiento tiene enlace hacia el anterior y el siguiente; se puede leer el historial en ambos sentidos.
- Árbol: cada celda explorada recibe una clave entera, los valores menores están a izquierda y los mayores a derecha; los tres recorridos son recursivos.

## Condiciones y límites
- La pila y cola admiten valores nulos como antes. No usar colecciones preconstruidas para sustituir ninguna de las cinco estructuras.
- El algoritmo BFS usa una cola propia y un árbol de búsqueda no balanceado. El árbol puede degenerarse; su peor caso es O(n) por búsqueda/inserción.
- Los recorridos recursivos son O(n) tiempo y O(h) espacio de llamadas, donde h es altura. Árboles artificialmente profundos pueden agotar la pila del proceso; para los tableros pequeños de este proyecto es un compromiso didáctico razonable.
- Se debe confirmar compilación y pruebas automatizadas en la rama y revisar visualmente el juego antes de fusionar.
- No se cambió la interfaz ni las reglas del juego.

## Referencias internas
- `11. Pilas y Colas(1).pdf`, funcionamiento FIFO/LIFO y ejemplos Java.
- `12. Listas - Modo de compatibilidad(1).pdf`, referencias `sig`, cabecera, listas circulares y dobles.
- `13. Arboles(1).pdf`, definición de árbol, recorridos preorden/inorden/posorden y código Java de los métodos.
- `Proyecto final – Estructuras de Datos(1).pdf`, operaciones mínimas, criterios y entregables.
