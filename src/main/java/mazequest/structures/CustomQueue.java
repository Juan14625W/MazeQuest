package mazequest.structures;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Cola FIFO propia para la frontera BFS. Frente y final hacen O(1) las operaciones básicas;
 * recorrido O(n), espacio O(n). Encolar A,B y desencolar devuelve A.
 *
 * @param <T> tipo de dato, admite null
 */
public final class CustomQueue<T> {
  /** Nodo con sucesor mutable para enlazar al final. */
  private static final class Nodo<T> {
    private final T valor;
    private Nodo<T> siguiente;

    /**
     * Almacena el dato.
     *
     * @param valor valor
     */
    private Nodo(T valor) {
      this.valor = valor;
    }
  }

  private Nodo<T> inicio;
  private Nodo<T> fin;
  private int size;

  /** Construye una cola vacía con ambos extremos nulos. */
  public CustomQueue() {}

  /**
   * Encola en O(1).
   *
   * @param valor valor
   */
  public void enqueue(T valor) {
    Nodo<T> node = new Nodo<>(valor);
    // En vacío ambos extremos apuntan al único nodo.
    if (fin == null) inicio = node;
    else fin.siguiente = node;
    fin = node;
    size++;
  }

  /**
   * Desencola en O(1).
   *
   * @return frente
   * @throws NoSuchElementException si vacía
   */
  public T dequeue() {
    T valor = peek();
    inicio = inicio.siguiente;
    size--;
    // No conservar un final huérfano al retirar el último elemento.
    if (inicio == null) fin = null;
    return valor;
  }

  /**
   * Consulta O(1).
   *
   * @return frente
   * @throws NoSuchElementException si vacía
   */
  public T peek() {
    if (isEmpty()) throw new NoSuchElementException("Cola vacía");
    return inicio.valor;
  }

  /**
   * Cantidad O(1).
   *
   * @return nodos
   */
  public int size() {
    return size;
  }

  /**
   * Estado O(1).
   *
   * @return true si vacía
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Recorre FIFO sin alterar, O(n).
   *
   * @param action visitante
   */
  public void forEach(Consumer<? super T> action) {
    for (Nodo<T> n = inicio; n != null; n = n.siguiente) action.accept(n.valor);
  }
}
