package mazequest.structures;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Lista circular propia que guarda las posiciones de patrulla de Enemy.
 * Sigue Listas, pp. 9-18: Nodo contiene dato/sig y un recorrido termina al volver a cabecera.
 * Invariantes: en vacío cabecera y ultimo son null; en otro caso ultimo.sig == cabecera.
 * El único nodo se enlaza a sí mismo. Se conserva ultimo para insertar en extremos en O(1),
 * sin buscarlo en cada inserción como en las diapositivas. Índices, búsqueda y recorrido O(n);
 * memoria O(n). El cursor añade avance O(1) y restauración O(n) para deshacer turnos.
 *
 * @param <T> tipo de dato; Enemy usa Position y se admiten datos null
 */
public final class CircularLinkedList<T> {
  /**
   * Nodo privado con información y referencia al siguiente elemento del ciclo.
   * @param <T> tipo de información
   */
  private static final class Nodo<T> {
    private final T dato;
    private Nodo<T> sig;

    /**
     * Crea un nodo todavía sin incorporar al ciclo.
     * @param dato información a conservar
     */
    private Nodo(T dato) {
      this.dato = dato;
      this.sig = null;
    }
  }

  private Nodo<T> cabecera;
  private Nodo<T> ultimo;
  private int cantidad;
  private int version;

  /** Crea una lista vacía sin centinelas ni nodos artificiales. */
  public CircularLinkedList() {
    cabecera = null;
    ultimo = null;
    cantidad = 0;
    version = 0;
  }

  /**
   * Inserta al inicio en O(1); equivale a InsertarInicio de clase.
   * @param dato información del nuevo primer nodo
   */
  public void addFirst(T dato) {
    Nodo<T> nuevo = new Nodo<>(dato);
    if (isEmpty()) {
      // Un solo nodo es a la vez principio y final: debe volver a sí mismo.
      cabecera = nuevo;
      ultimo = nuevo;
      nuevo.sig = nuevo;
    } else {
      // Conservamos el inicio antiguo antes de cambiar cabecera.
      nuevo.sig = cabecera;
      cabecera = nuevo;
      ultimo.sig = cabecera;
    }
    cantidad++;
    version++;
  }

  /**
   * Inserta al final en O(1); equivale a InsertarFinal de clase.
   * @param dato información del nuevo último nodo
   */
  public void addLast(T dato) {
    Nodo<T> nuevo = new Nodo<>(dato);
    if (isEmpty()) {
      cabecera = nuevo;
      ultimo = nuevo;
      nuevo.sig = nuevo;
    } else {
      // El nuevo enlaza a la cabecera y el último antiguo enlaza al nuevo.
      nuevo.sig = cabecera;
      ultimo.sig = nuevo;
      ultimo = nuevo;
    }
    cantidad++;
    version++;
  }

  /**
   * Inserta por posición en O(n), o en O(1) si es un extremo.
   * @param index posición entre cero y size(), ambos incluidos
   * @param dato información del nuevo nodo
   * @throws IndexOutOfBoundsException si index no es una posición de inserción
   */
  public void insert(int index, T dato) {
    if (index < 0 || index > cantidad) {
      throw new IndexOutOfBoundsException(index);
    }
    if (index == 0) {
      addFirst(dato);
    } else if (index == cantidad) {
      addLast(dato);
    } else {
      Nodo<T> anterior = nodoEn(index - 1);
      Nodo<T> nuevo = new Nodo<>(dato);
      // Primero retenemos el resto del ciclo; después sustituimos el enlace anterior.
      nuevo.sig = anterior.sig;
      anterior.sig = nuevo;
      cantidad++;
      version++;
    }
  }

  /**
   * Elimina por posición en O(n); quitar la cabecera cuesta O(1).
   * @param index posición existente, desde cero
   * @return dato del nodo retirado
   * @throws IndexOutOfBoundsException si index no identifica un nodo
   */
  public T remove(int index) {
    Nodo<T> actual = nodoEn(index);
    if (cantidad == 1) {
      // Comprobar singleton antes de mover cabecera evita confundirlo con dos nodos.
      cabecera = null;
      ultimo = null;
    } else if (index == 0) {
      cabecera = actual.sig;
      ultimo.sig = cabecera;
    } else {
      Nodo<T> anterior = nodoEn(index - 1);
      anterior.sig = actual.sig;
      if (actual == ultimo) {
        // El penúltimo pasa a ser último; su sig ya apunta a cabecera.
        ultimo = anterior;
      }
    }
    cantidad--;
    version++;
    return actual.dato;
  }

  /**
   * Busca el primer dato igual en una vuelta O(n), sin alterar cabecera.
   * @param dato información buscada; Objects.equals permite comparar también null
   * @return índice desde cero, o -1 cuando no aparece
   */
  public int indexOf(T dato) {
    if (isEmpty()) {
      return -1;
    }
    Nodo<T> actual = cabecera;
    int indice = 0;
    do {
      if (Objects.equals(actual.dato, dato)) {
        return indice;
      }
      actual = actual.sig;
      indice++;
    } while (actual != cabecera);
    return -1;
  }

  /**
   * Consulta un dato por posición en O(n).
   * @param index posición existente, desde cero
   * @return información del nodo
   * @throws IndexOutOfBoundsException si index no identifica un nodo
   */
  public T get(int index) {
    return nodoEn(index).dato;
  }

  /**
   * Localiza un nodo avanzando desde cabecera, O(n).
   * @param index posición existente, desde cero
   * @return referencia interna al nodo
   * @throws IndexOutOfBoundsException si index no identifica un nodo
   */
  private Nodo<T> nodoEn(int index) {
    if (index < 0 || index >= cantidad) {
      throw new IndexOutOfBoundsException(index);
    }
    Nodo<T> actual = cabecera;
    for (int i = 0; i < index; i++) {
      actual = actual.sig;
    }
    return actual;
  }

  /**
   * Consulta el contador de nodos en O(1).
   * @return cantidad de nodos de la lista
   */
  public int size() { return cantidad; }

  /**
   * Comprueba ausencia de cabecera en O(1).
   * @return true cuando la lista está vacía
   */
  public boolean isEmpty() { return cabecera == null; }

  /**
   * Recorre exactamente una vuelta desde cabecera, O(n), como MostrarCompleta de clase.
   * @param action acción de consulta para cada dato; no debe modificar la lista
   */
  public void forEach(Consumer<? super T> action) {
    if (isEmpty()) {
      return;
    }
    Nodo<T> actual = cabecera;
    // No se busca null: todos los nodos del ciclo tienen sucesor.
    do {
      action.accept(actual.dato);
      actual = actual.sig;
    } while (actual != cabecera);
  }

  /**
   * Crea un cursor en cabecera en O(1), sin exponer enlaces al motor.
   * @return cursor de patrulla independiente de otros cursores
   * @throws NoSuchElementException si la lista está vacía
   */
  public Cursor cursor() {
    if (isEmpty()) {
      throw new NoSuchElementException("Patrulla vacía");
    }
    return new Cursor();
  }

  /**
   * Referencia auxiliar persistente de Enemy: avanzar sigue sig; restaurar busca un índice.
   * La versión detecta ediciones de la ruta que invalidarían una fase guardada para deshacer.
   * No copia la lista ni cambia cabecera. Cada cursor ocupa O(1).
   */
  public final class Cursor {
    private Nodo<T> actual;
    private final int versionEsperada;
    private int indice;

    /** Captura la cabecera y la versión actual de los enlaces. */
    private Cursor() {
      actual = cabecera;
      versionEsperada = version;
      indice = 0;
    }

    /**
     * Comprueba que la ruta no fue editada desde la creación del cursor, O(1).
     * @throws ConcurrentModificationException si cambiaron sus nodos
     */
    private void comprobarVersion() {
      if (version != versionEsperada) {
        throw new ConcurrentModificationException();
      }
    }

    /**
     * Consulta la posición de patrulla en O(1).
     * @return dato del nodo actual
     * @throws ConcurrentModificationException si se modificó la lista
     */
    public T value() {
      comprobarVersion();
      return actual.dato;
    }

    /**
     * Avanza un enlace en O(1); ultimo.sig devuelve a cabecera sin reconstruir la ruta.
     * @throws ConcurrentModificationException si se modificó la lista
     */
    public void advance() {
      comprobarVersion();
      actual = actual.sig;
      indice++;
      if (indice == cantidad) {
        indice = 0;
      }
    }

    /**
     * Consulta la fase que se guarda en GameSnapshot, O(1).
     * @return índice actual entre cero y size()-1
     * @throws ConcurrentModificationException si se modificó la lista
     */
    public int index() {
      comprobarVersion();
      return indice;
    }

    /**
     * Restaura la fase anterior al deshacer un turno, O(n).
     * @param index índice de fase que se desea restaurar
     * @throws IndexOutOfBoundsException si el índice no existe
     * @throws ConcurrentModificationException si se modificó la lista
     */
    public void seek(int index) {
      comprobarVersion();
      actual = nodoEn(index);
      indice = index;
    }
  }
}
