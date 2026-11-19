package mazequest.structures;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Pila LIFO enlazada para estados anteriores del motor. Push/pop/peek: O(1); recorrido: O(n);
 * espacio: O(n). Por ejemplo, push(A), push(B), pop() devuelve B.
 *
 * @param <T> tipo de los valores, incluidos valores nulos
 */
public final class CustomStack<T> {
  /** Nodo privado: un valor y el enlace al elemento situado debajo. */
  private static final class Nodo<T> {
    private final T valor;
    private final Nodo<T> siguiente;

    /**
     * Conecta un nuevo tope al anterior.
     *
     * @param valor dato
     * @param siguiente anterior
     */
    private Nodo(T valor, Nodo<T> siguiente) {
      this.valor = valor;
      this.siguiente = siguiente;
    }
  }

  private Nodo<T> inicio;
  private int size;

  /** Crea una pila vacía sin nodo centinela. */
  public CustomStack() {}

  /**
   * Apila en O(1).
   *
   * @param valor nuevo dato
   */
  public void push(T valor) {
    inicio = new Nodo<>(valor, inicio);
    size++;
  }

  /**
   * Extrae en O(1).
   *
   * @return tope
   * @throws NoSuchElementException si está vacía
   */
  public T pop() {
    T valor = peek();
    // El siguiente nodo pasa a ser tope; el nodo retirado queda sin referencias.
    inicio = inicio.siguiente;
    size--;
    return valor;
  }

  /**
   * Consulta en O(1).
   *
   * @return tope
   * @throws NoSuchElementException si está vacía
   */
  public T peek() {
    if (isEmpty()) throw new NoSuchElementException("Pila vacía");
    return inicio.valor;
  }

  /**
   * Consulta cantidad en O(1).
   *
   * @return cantidad de nodos
   */
  public int size() {
    return size;
  }

  /**
   * Consulta ausencia en O(1).
   *
   * @return true si no hay nodos
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Visita desde el tope sin extraer, O(n).
   *
   * @param action acción de consulta
   */
  public void forEach(Consumer<? super T> action) {
    for (Nodo<T> n = inicio; n != null; n = n.siguiente) action.accept(n.valor);
  }
}
