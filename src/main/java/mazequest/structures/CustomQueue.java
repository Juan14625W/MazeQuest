package mazequest.structures;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Cola FIFO propia para la frontera BFS. Frente y final hacen O(1) las operaciones
 * básicas; recorrido O(n), espacio O(n). Encolar A,B y desencolar devuelve A.
 *
     * @param <T> tipo de dato, admite null
 */
public final class CustomQueue<T> {
    /** Nodo con sucesor mutable para enlazar al final. */
    private static final class Node<T> {
        private final T value;
        private Node<T> next;
        /** Almacena el dato.
     * @param value valor */
        private Node(T value) { this.value = value; }
    }
    private Node<T> first;
    private Node<T> last;
    private int size;
    /** Construye una cola vacía con ambos extremos nulos. */
    public CustomQueue() { }
    /** Encola en O(1).
     * @param value valor */
    public void enqueue(T value) {
        Node<T> node = new Node<>(value);
        // En vacío ambos extremos apuntan al único nodo.
        if (last == null) first = node; else last.next = node;
        last = node; size++;
    }
    /** Desencola en O(1).
     * @return frente
     * @throws NoSuchElementException si vacía */
    public T dequeue() {
        T value = peek(); first = first.next; size--;
        // No conservar un final huérfano al retirar el último elemento.
        if (first == null) last = null;
        return value;
    }
    /** Consulta O(1).
     * @return frente
     * @throws NoSuchElementException si vacía */
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Cola vacía");
        return first.value;
    }
    /** Cantidad O(1).
     * @return nodos */
    public int size() { return size; }
    /** Estado O(1).
     * @return true si vacía */
    public boolean isEmpty() { return size == 0; }
    /** Recorre FIFO sin alterar, O(n).
     * @param action visitante */
    public void forEach(Consumer<? super T> action) {
        for (Node<T> n = first; n != null; n = n.next) action.accept(n.value);
    }
}
