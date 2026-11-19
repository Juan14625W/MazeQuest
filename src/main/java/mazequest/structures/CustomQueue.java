package mazequest.structures;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Cola FIFO propia para la frontera BFS: la celda descubierta primero se expande primero.
 * Sigue Pilas y Colas, pp. 14-21: Nodo enlazado y referencias inicio/fin. Se usa fin porque
 * final es palabra reservada en Java. En vacío ambos extremos son null y cantidad es cero;
 * fuera de vacío fin.sig es null. Operaciones básicas O(1), recorrido O(n), espacio O(n).
 * T conserva Position en BFS y enteros en el verificador, sin duplicar la cola.
 *
 * @param <T> tipo de dato, admite null
 */
public final class CustomQueue<T> {
  /** Nodo propio con un dato y el enlace al siguiente elemento de la fila. */
  private static final class Nodo<T> {
    private final T dato;
    private Nodo<T> sig;

    /**
     * Almacena el dato.
     *
     * @param dato información del nodo, también puede ser null
     */
    private Nodo(T dato) {
      this.dato = dato;
      this.sig = null;
    }
  }

  private Nodo<T> inicio;
  private Nodo<T> fin;
  private int cantidad;

  /** Construye una cola vacía con ambos extremos nulos. */
  public CustomQueue() {
    inicio = null;
    fin = null;
    cantidad = 0;
  }

  /**
   * Encola en O(1).
   *
   * @param dato información que se añade detrás de los elementos existentes
   */
  public void encolar(T dato) {
    Nodo<T> nuevo = new Nodo<>(dato);
    if (esVacia()) {
      // El primer elemento es a la vez el frente y el final de la fila.
      inicio = nuevo;
    } else {
      // El antiguo final deja de terminar en null y enlaza al nuevo nodo.
      fin.sig = nuevo;
    }
    fin = nuevo;
    cantidad++;
  }

  /**
   * Desencola en O(1).
   *
   * @return frente
   * @throws NoSuchElementException si vacía
   */
  public T desencolar() {
    T dato = verFrente();
    inicio = inicio.sig;
    cantidad--;
    if (inicio == null) {
      // Al retirar el único nodo también debe desaparecer la referencia final.
      // De lo contrario, una inserción posterior enlazaría una cadena ya retirada.
      fin = null;
    }
    return dato;
  }

  /**
   * Consulta O(1).
   *
   * @return frente
   * @throws NoSuchElementException si vacía
   */
  public T verFrente() {
    if (esVacia()) {
      throw new NoSuchElementException("Cola vacía");
    }
    return inicio.dato;
  }

  /**
   * Cantidad O(1).
   *
   * @return nodos
   */
  public int tamano() {
    return cantidad;
  }

  /**
   * Estado O(1).
   *
   * @return true si vacía
   */
  public boolean esVacia() {
    return inicio == null;
  }

  /**
   * Recorre FIFO sin alterar, O(n).
   *
   * @param action acción de consulta para cada dato; no debe modificar la cola
   */
  public void forEach(Consumer<? super T> action) {
    Nodo<T> actual = inicio;
    // Avanzamos un auxiliar para consultar sin desencolar ni cambiar los extremos.
    while (actual != null) {
      action.accept(actual.dato);
      actual = actual.sig;
    }
  }

  /**
   * Conserva el nombre previo de encolar, O(1).
   * @param dato información a encolar
   */
  public void enqueue(T dato) { encolar(dato); }

  /**
   * Conserva el nombre previo de desencolar, O(1).
   * @return dato retirado del frente
   * @throws NoSuchElementException si la cola está vacía
   */
  public T dequeue() { return desencolar(); }

  /**
   * Conserva el nombre previo de verFrente, O(1).
   * @return dato del frente sin retirarlo
   * @throws NoSuchElementException si la cola está vacía
   */
  public T peek() { return verFrente(); }

  /**
   * Conserva el nombre previo de tamano, O(1).
   * @return número de nodos
   */
  public int size() { return tamano(); }

  /**
   * Conserva el nombre previo de esVacia, O(1).
   * @return true si no hay nodos
   */
  public boolean isEmpty() { return esVacia(); }
}
