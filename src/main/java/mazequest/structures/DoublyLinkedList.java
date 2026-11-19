package mazequest.structures;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Historial con enlaces sig/ant propios. Inserciones en extremos O(1); índice y búsqueda O(n),
 * recorrido O(n), memoria O(n). A↔B permite mostrar A,B hacia adelante y B,A hacia atrás.
 *
 * @param <T> tipo de entrada
 */
public final class DoublyLinkedList<T> {
  /** Nodo con dos enlaces; los extremos externos permanecen nulos. */
  private static final class Nodo<T> {
    private final T valor;
    private Nodo<T> sig;
    private Nodo<T> ant;

    /**
     * Almacena contenido.
     *
     * @param valor dato
     */
    private Nodo(T valor) {
      this.valor = valor;
    }
  }

  private Nodo<T> inicio;
  private Nodo<T> fin;
  private int size;

  /** Crea un historial vacío. */
  public DoublyLinkedList() {}

  /**
   * Inserta primero en O(1).
   *
   * @param valor dato
   */
  public void addFirst(T valor) {
    Nodo<T> n = new Nodo<>(valor);
    n.sig = inicio;
    if (inicio == null) fin = n;
    else inicio.ant = n;
    inicio = n;
    size++;
  }

  /**
   * Inserta último en O(1).
   *
   * @param valor dato
   */
  public void addLast(T valor) {
    Nodo<T> n = new Nodo<>(valor);
    n.ant = fin;
    if (fin == null) inicio = n;
    else fin.sig = n;
    fin = n;
    size++;
  }

  /**
   * Inserta por índice O(n).
   *
   * @param index posición
   * @param valor dato
   * @throws IndexOutOfBoundsException posición inválida
   */
  public void insert(int index, T valor) {
    if (index < 0 || index > size) throw new IndexOutOfBoundsException(index);
    if (index == 0) {
      addFirst(valor);
      return;
    }
    if (index == size) {
      addLast(valor);
      return;
    }
    Nodo<T> right = nodeAt(index), n = new Nodo<>(valor);
    // Se enlazan ambas direcciones antes de sustituir el enlace de right.
    n.ant = right.ant;
    n.sig = right;
    right.ant.sig = n;
    right.ant = n;
    size++;
  }

  /**
   * Retira O(n), O(1) en extremos.
   *
   * @param index posición
   * @return dato
   * @throws IndexOutOfBoundsException posición inválida
   */
  public T remove(int index) {
    Nodo<T> n = nodeAt(index);
    if (n.ant == null) inicio = n.sig;
    else n.ant.sig = n.sig;
    if (n.sig == null) fin = n.ant;
    else n.sig.ant = n.ant;
    size--;
    return n.valor;
  }

  /**
   * Consulta O(n).
   *
   * @param index posición
   * @return dato
   * @throws IndexOutOfBoundsException posición inválida
   */
  public T get(int index) {
    return nodeAt(index).valor;
  }

  /**
   * Usa el extremo más cercano.
   *
   * @param index posición
   * @return nodo
   * @throws IndexOutOfBoundsException posición inválida
   */
  private Nodo<T> nodeAt(int index) {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index);
    Nodo<T> n;
    if (index < size / 2) {
      n = inicio;
      for (int i = 0; i < index; i++) n = n.sig;
    } else {
      n = fin;
      for (int i = size - 1; i > index; i--) n = n.ant;
    }
    return n;
  }

  /**
   * Busca O(n).
   *
   * @param valor dato
   * @return índice o -1
   */
  public int indexOf(T valor) {
    int i = 0;
    for (Nodo<T> n = inicio; n != null; n = n.sig, i++)
      if (Objects.equals(n.valor, valor)) return i;
    return -1;
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
   * @return true si vacío
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Recorre sig O(n).
   *
   * @param action visitante
   */
  public void forEach(Consumer<? super T> action) {
    for (Nodo<T> n = inicio; n != null; n = n.sig) action.accept(n.valor);
  }

  /**
   * Recorre ant O(n).
   *
   * @param action visitante
   */
  public void forEachReverse(Consumer<? super T> action) {
    for (Nodo<T> n = fin; n != null; n = n.ant) action.accept(n.valor);
  }
}
