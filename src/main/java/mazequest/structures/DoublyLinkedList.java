package mazequest.structures;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Historial con enlaces next/prev propios. Inserciones en extremos O(1); índice y búsqueda O(n),
 * recorrido O(n), memoria O(n). A↔B permite mostrar A,B hacia adelante y B,A hacia atrás.
 *
 * @param <T> tipo de entrada
 */
public final class DoublyLinkedList<T> {
  /** Nodo con dos enlaces; los extremos externos permanecen nulos. */
  private static final class Node<T> {
    private final T value;
    private Node<T> next;
    private Node<T> prev;

    /**
     * Almacena contenido.
     *
     * @param value dato
     */
    private Node(T value) {
      this.value = value;
    }
  }

  private Node<T> first;
  private Node<T> last;
  private int size;

  /** Crea un historial vacío. */
  public DoublyLinkedList() {}

  /**
   * Inserta primero en O(1).
   *
   * @param value dato
   */
  public void addFirst(T value) {
    Node<T> n = new Node<>(value);
    n.next = first;
    if (first == null) last = n;
    else first.prev = n;
    first = n;
    size++;
  }

  /**
   * Inserta último en O(1).
   *
   * @param value dato
   */
  public void addLast(T value) {
    Node<T> n = new Node<>(value);
    n.prev = last;
    if (last == null) first = n;
    else last.next = n;
    last = n;
    size++;
  }

  /**
   * Inserta por índice O(n).
   *
   * @param index posición
   * @param value dato
   * @throws IndexOutOfBoundsException posición inválida
   */
  public void insert(int index, T value) {
    if (index < 0 || index > size) throw new IndexOutOfBoundsException(index);
    if (index == 0) {
      addFirst(value);
      return;
    }
    if (index == size) {
      addLast(value);
      return;
    }
    Node<T> right = nodeAt(index), n = new Node<>(value);
    // Se enlazan ambas direcciones antes de sustituir el enlace de right.
    n.prev = right.prev;
    n.next = right;
    right.prev.next = n;
    right.prev = n;
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
    Node<T> n = nodeAt(index);
    if (n.prev == null) first = n.next;
    else n.prev.next = n.next;
    if (n.next == null) last = n.prev;
    else n.next.prev = n.prev;
    size--;
    return n.value;
  }

  /**
   * Consulta O(n).
   *
   * @param index posición
   * @return dato
   * @throws IndexOutOfBoundsException posición inválida
   */
  public T get(int index) {
    return nodeAt(index).value;
  }

  /**
   * Usa el extremo más cercano.
   *
   * @param index posición
   * @return nodo
   * @throws IndexOutOfBoundsException posición inválida
   */
  private Node<T> nodeAt(int index) {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index);
    Node<T> n;
    if (index < size / 2) {
      n = first;
      for (int i = 0; i < index; i++) n = n.next;
    } else {
      n = last;
      for (int i = size - 1; i > index; i--) n = n.prev;
    }
    return n;
  }

  /**
   * Busca O(n).
   *
   * @param value dato
   * @return índice o -1
   */
  public int indexOf(T value) {
    int i = 0;
    for (Node<T> n = first; n != null; n = n.next, i++)
      if (Objects.equals(n.value, value)) return i;
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
   * Recorre next O(n).
   *
   * @param action visitante
   */
  public void forEach(Consumer<? super T> action) {
    for (Node<T> n = first; n != null; n = n.next) action.accept(n.value);
  }

  /**
   * Recorre prev O(n).
   *
   * @param action visitante
   */
  public void forEachReverse(Consumer<? super T> action) {
    for (Node<T> n = last; n != null; n = n.prev) action.accept(n.value);
  }
}
