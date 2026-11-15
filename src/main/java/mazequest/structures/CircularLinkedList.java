package mazequest.structures;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Lista circular simple para patrullas. El último enlaza siempre al primero. Extremos O(1) al
 * insertar; operaciones por índice y búsqueda O(n); espacio O(n). Una patrulla A,B,C vuelve a A
 * siguiendo next desde C.
 *
 * @param <T> dato almacenado
 */
public final class CircularLinkedList<T> {
  /** Nodo propio cuyo sucesor nunca es null mientras pertenece a la lista. */
  private static final class Node<T> {
    private final T value;
    private Node<T> next;

    /**
     * Guarda el dato.
     *
     * @param value contenido
     */
    private Node(T value) {
      this.value = value;
    }
  }

  private Node<T> tail;
  private int size;
  private int version;

  /** Crea el ciclo vacío. */
  public CircularLinkedList() {}

  /**
   * Inserta primero en O(1).
   *
   * @param value dato
   */
  public void addFirst(T value) {
    Node<T> n = new Node<>(value);
    if (tail == null) {
      n.next = n;
      tail = n;
    } else {
      n.next = tail.next;
      tail.next = n;
    }
    size++;
    version++;
  }

  /**
   * Inserta último en O(1).
   *
   * @param value dato
   */
  public void addLast(T value) {
    addFirst(value);
    tail = tail.next;
  }

  /**
   * Inserta en O(n).
   *
   * @param index entre 0 y size
   * @param value dato
   * @throws IndexOutOfBoundsException índice inválido
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
    Node<T> prev = nodeAt(index - 1), n = new Node<>(value);
    // Se conserva el resto del ciclo antes de sustituir el enlace anterior.
    n.next = prev.next;
    prev.next = n;
    size++;
    version++;
  }

  /**
   * Elimina en O(n).
   *
   * @param index posición
   * @return dato retirado
   * @throws IndexOutOfBoundsException índice inválido
   */
  public T remove(int index) {
    Node<T> victim = nodeAt(index);
    if (size == 1) tail = null;
    else {
      Node<T> prev = index == 0 ? tail : nodeAt(index - 1);
      prev.next = victim.next;
      if (victim == tail) tail = prev;
    }
    size--;
    version++;
    return victim.value;
  }

  /**
   * Busca por igualdad en O(n).
   *
   * @param value objetivo
   * @return índice o -1
   */
  public int indexOf(T value) {
    Node<T> n = tail == null ? null : tail.next;
    for (int i = 0; i < size; i++, n = n.next) if (Objects.equals(n.value, value)) return i;
    return -1;
  }

  /**
   * Consulta O(n).
   *
   * @param index índice
   * @return dato
   * @throws IndexOutOfBoundsException índice inválido
   */
  public T get(int index) {
    return nodeAt(index).value;
  }

  /**
   * Localiza un nodo en O(n).
   *
   * @param index índice
   * @return nodo
   * @throws IndexOutOfBoundsException índice inválido
   */
  private Node<T> nodeAt(int index) {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index);
    Node<T> n = tail.next;
    for (int i = 0; i < index; i++) n = n.next;
    return n;
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
   * Vacío O(1).
   *
   * @return ausencia de nodos
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Visita exactamente una vuelta, O(n).
   *
   * @param action visitante
   */
  public void forEach(Consumer<? super T> action) {
    Node<T> n = tail == null ? null : tail.next;
    for (int i = 0; i < size; i++, n = n.next) action.accept(n.value);
  }

  /**
   * Crea un cursor circular.
   *
   * @return cursor al inicio
   * @throws NoSuchElementException si no hay nodos
   */
  public Cursor cursor() {
    if (isEmpty()) throw new NoSuchElementException("Patrulla vacía");
    return new Cursor();
  }

  /** Cursor de patrulla O(1) por avance; se invalida si se edita la lista. */
  public final class Cursor {
    private Node<T> current = tail.next;
    private final int expectedVersion = version;
    private int index;

    /** Captura el nodo inicial y la versión actual. */
    private Cursor() {}

    /**
     * Comprueba que los enlaces no cambiaron.
     *
     * @throws ConcurrentModificationException si se modificó la lista
     */
    private void check() {
      if (version != expectedVersion) throw new ConcurrentModificationException();
    }

    /**
     * Consulta O(1).
     *
     * @return dato actual
     */
    public T value() {
      check();
      return current.value;
    }

    /** Avanza por next, incluso último→primero, O(1). */
    public void advance() {
      check();
      current = current.next;
      index = (index + 1) % size;
    }

    /**
     * Índice para instantáneas.
     *
     * @return posición actual
     */
    public int index() {
      check();
      return index;
    }

    /**
     * Restaura posición en O(n).
     *
     * @param index posición
     * @throws IndexOutOfBoundsException posición inválida
     */
    public void seek(int index) {
      check();
      current = nodeAt(index);
      this.index = index;
    }
  }
}
