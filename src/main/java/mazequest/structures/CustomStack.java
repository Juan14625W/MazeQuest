package mazequest.structures;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Pila LIFO enlazada para estados anteriores del motor. Push/pop/peek: O(1);
 * recorrido: O(n); espacio: O(n). Por ejemplo, push(A), push(B), pop() devuelve B.
 *
     * @param <T> tipo de los valores, incluidos valores nulos
 */
public final class CustomStack<T> {
    /** Nodo privado: un valor y el enlace al elemento situado debajo. */
    private static final class Node<T> {
        private final T value;
        private final Node<T> next;
        /** Conecta un nuevo tope al anterior.
     * @param value dato
     * @param next anterior */
        private Node(T value, Node<T> next) { this.value = value; this.next = next; }
    }
    private Node<T> top;
    private int size;
    /** Crea una pila vacía sin nodo centinela. */
    public CustomStack() { }
    /** Apila en O(1).
     * @param value nuevo dato */
    public void push(T value) { top = new Node<>(value, top); size++; }
    /** Extrae en O(1).
     * @return tope
     * @throws NoSuchElementException si está vacía */
    public T pop() {
        T value = peek();
        // El siguiente nodo pasa a ser tope; el nodo retirado queda sin referencias.
        top = top.next; size--; return value;
    }
    /** Consulta en O(1).
     * @return tope
     * @throws NoSuchElementException si está vacía */
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Pila vacía");
        return top.value;
    }
    /** Consulta cantidad en O(1).
     * @return cantidad de nodos */
    public int size() { return size; }
    /** Consulta ausencia en O(1).
     * @return true si no hay nodos */
    public boolean isEmpty() { return size == 0; }
    /** Visita desde el tope sin extraer, O(n).
     * @param action acción de consulta */
    public void forEach(Consumer<? super T> action) {
        for (Node<T> n = top; n != null; n = n.next) action.accept(n.value);
    }
}
