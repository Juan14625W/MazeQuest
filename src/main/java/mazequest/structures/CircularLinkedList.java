package mazequest.structures;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Lista circular simple para patrullas. El último enlaza siempre al primero. Extremos O(1) al
 * insertar; operaciones por índice y búsqueda O(n); espacio O(n). Una patrulla A,B,C vuelve a A
 * siguiendo sig desde C.
 *
 * @param <T> dato almacenado
 */
public final class CircularLinkedList<T> {
  /** Nodo propio cuyo sucesor nunca es null mientras pertenece a la lista. */
  private static final class Nodo<T> {
    private final T valor;
    private Nodo<T> sig;

    /**
     * Guarda el dato.
     *
     * @param valor contenido
     */
    private Nodo(T valor) {
      this.valor = valor;
    }
  }

  private Nodo<T> ultimo;
  private int size;
  private int version;

  /** Crea el ciclo vacío. */
  public CircularLinkedList() {}

  /**
   * Inserta primero en O(1).
   *
   * @param valor dato
   */
  public void addFirst(T valor) {
    Nodo<T> n = new Nodo<>(valor);
    if (ultimo == null) {
      n.sig = n;
      ultimo = n;
    } else {
      n.sig = ultimo.sig;
      ultimo.sig = n;
    }
    size++;
    version++;
  }

  /**
   * Inserta último en O(1).
   *
   * @param valor dato
   */
  public void addLast(T valor) {
    addFirst(valor);
    ultimo = ultimo.sig;
  }

  /**
   * Inserta en O(n).
   *
   * @param index entre 0 y size
   * @param valor dato
   * @throws IndexOutOfBoundsException índice inválido
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
    Nodo<T> prev = nodeAt(index - 1), n = new Nodo<>(valor);
    // Se conserva el resto del ciclo antes de sustituir el enlace anterior.
    n.sig = prev.sig;
    prev.sig = n;
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
    Nodo<T> victim = nodeAt(index);
    if (size == 1) ultimo = null;
    else {
      Nodo<T> prev = index == 0 ? ultimo : nodeAt(index - 1);
      prev.sig = victim.sig;
      if (victim == ultimo) ultimo = prev;
    }
    size--;
    version++;
    return victim.valor;
  }

  /**
   * Busca por igualdad en O(n).
   *
   * @param valor objetivo
   * @return índice o -1
   */
  public int indexOf(T valor) {
    Nodo<T> n = ultimo == null ? null : ultimo.sig;
    for (int i = 0; i < size; i++, n = n.sig) if (Objects.equals(n.valor, valor)) return i;
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
    return nodeAt(index).valor;
  }

  /**
   * Localiza un nodo en O(n).
   *
   * @param index índice
   * @return nodo
   * @throws IndexOutOfBoundsException índice inválido
   */
  private Nodo<T> nodeAt(int index) {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index);
    Nodo<T> n = ultimo.sig;
    for (int i = 0; i < index; i++) n = n.sig;
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
    Nodo<T> n = ultimo == null ? null : ultimo.sig;
    for (int i = 0; i < size; i++, n = n.sig) action.accept(n.valor);
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
    private Nodo<T> current = ultimo.sig;
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
    public T valor() {
      check();
      return current.valor;
    }

    /** Avanza por sig, incluso último→primero, O(1). */
    public void advance() {
      check();
      current = current.sig;
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
