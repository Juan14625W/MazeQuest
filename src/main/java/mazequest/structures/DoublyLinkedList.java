package mazequest.structures;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Lista doble propia para el historial MoveRecord de GameEngine.
 * Sigue Listas, pp. 19-29: cada Nodo guarda dato, sig y ant; se recorre en ambos sentidos.
 * Se conservan inicio y fin en vez de mover una cabecera compartida al consultar el historial.
 * Invariantes: inicio.ant y fin.sig son null; cada enlace sig tiene su ant recíproco;
 * en vacío ambos extremos son null y cantidad es cero. Extremos O(1), índices/búsqueda O(n),
 * recorridos O(n), memoria O(n). Consultar nunca modifica movimientos ni posiciones del juego.
 *
 * @param <T> tipo de información; el motor guarda MoveRecord y se admiten datos null
 */
public final class DoublyLinkedList<T> {
  /**
   * Nodo privado con información y enlaces al vecino anterior y siguiente.
   * @param <T> tipo de información
   */
  private static final class Nodo<T> {
    private final T dato;
    private Nodo<T> sig;
    private Nodo<T> ant;

    /**
     * Crea un nodo aún sin vecinos.
     * @param dato información del nodo
     */
    private Nodo(T dato) {
      this.dato = dato;
      this.sig = null;
      this.ant = null;
    }
  }

  private Nodo<T> inicio;
  private Nodo<T> fin;
  private int cantidad;

  /** Crea una lista vacía con sus dos referencias externas nulas. */
  public DoublyLinkedList() {
    inicio = null;
    fin = null;
    cantidad = 0;
  }

  /**
   * Inserta al inicio en O(1), como insertarAlPrincipio de clase.
   * @param dato información del nuevo primer nodo
   */
  public void addFirst(T dato) {
    Nodo<T> nuevo = new Nodo<>(dato);
    nuevo.sig = inicio;
    if (isEmpty()) {
      // El primer nodo también será el último; ambos enlaces externos son null.
      fin = nuevo;
    } else {
      // El antiguo primero reconoce al nuevo como anterior.
      inicio.ant = nuevo;
    }
    inicio = nuevo;
    cantidad++;
  }

  /**
   * Inserta al final en O(1), como insertarAlFinal de clase.
   * @param dato información del nuevo último nodo
   */
  public void addLast(T dato) {
    Nodo<T> nuevo = new Nodo<>(dato);
    nuevo.ant = fin;
    if (isEmpty()) {
      inicio = nuevo;
    } else {
      // Se completa el enlace recíproco antes de cambiar la referencia fin.
      fin.sig = nuevo;
    }
    fin = nuevo;
    cantidad++;
  }

  /**
   * Inserta por posición en O(n); en extremos cuesta O(1).
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
      Nodo<T> actual = nodoEn(index);
      Nodo<T> anterior = actual.ant;
      Nodo<T> nuevo = new Nodo<>(dato);
      // Situamos nuevo entre anterior y actual: hay cuatro referencias que enlazar.
      nuevo.ant = anterior;
      nuevo.sig = actual;
      anterior.sig = nuevo;
      actual.ant = nuevo;
      cantidad++;
    }
  }

  /**
   * Elimina un nodo por posición, O(n), o O(1) en extremos.
   * @param index posición existente desde cero
   * @return información del nodo retirado
   * @throws IndexOutOfBoundsException si index no identifica un nodo
   */
  public T remove(int index) {
    Nodo<T> actual = nodoEn(index);
    Nodo<T> anterior = actual.ant;
    Nodo<T> siguiente = actual.sig;
    if (anterior == null) {
      // Se retira el primero: el sucesor pasa a ser inicio.
      inicio = siguiente;
    } else {
      anterior.sig = siguiente;
    }
    if (siguiente == null) {
      // Se retira el último: el predecesor pasa a ser fin.
      fin = anterior;
    } else {
      // También limpia ant del nuevo inicio si acabamos de quitar el primero.
      siguiente.ant = anterior;
    }
    // Para singleton, ambos vecinos son null: las dos ramas vacían ambos extremos.
    cantidad--;
    return actual.dato;
  }

  /**
   * Consulta por posición sin modificar enlaces, O(n).
   * @param index posición existente desde cero
   * @return información del nodo
   * @throws IndexOutOfBoundsException si index no identifica un nodo
   */
  public T get(int index) {
    return nodoEn(index).dato;
  }

  /**
   * Localiza desde el extremo más cercano, O(1 + min(index, size()-1-index)).
   * @param index posición existente desde cero
   * @return referencia interna al nodo
   * @throws IndexOutOfBoundsException si index no identifica un nodo
   */
  private Nodo<T> nodoEn(int index) {
    if (index < 0 || index >= cantidad) {
      throw new IndexOutOfBoundsException(index);
    }
    Nodo<T> actual;
    if (index < cantidad / 2) {
      actual = inicio;
      for (int i = 0; i < index; i++) {
        actual = actual.sig;
      }
    } else {
      actual = fin;
      for (int i = cantidad - 1; i > index; i--) {
        actual = actual.ant;
      }
    }
    return actual;
  }

  /**
   * Busca el primer dato igual desde inicio, O(n).
   * @param dato objetivo; Objects.equals permite comparar también null
   * @return índice desde cero o -1 si no aparece
   */
  public int indexOf(T dato) {
    Nodo<T> actual = inicio;
    int indice = 0;
    while (actual != null) {
      if (Objects.equals(actual.dato, dato)) {
        return indice;
      }
      actual = actual.sig;
      indice++;
    }
    return -1;
  }

  /**
   * Consulta el contador mantenido en cada inserción/eliminación, O(1).
   * @return número de nodos
   */
  public int size() { return cantidad; }

  /**
   * Comprueba ausencia de inicio en O(1).
   * @return true si no hay nodos
   */
  public boolean isEmpty() { return inicio == null; }

  /**
   * Recorre desde el primero al último siguiendo sig, O(n).
   * @param action acción de consulta para cada dato; no debe modificar la lista
   */
  public void forEach(Consumer<? super T> action) {
    Nodo<T> actual = inicio;
    // El auxiliar termina en null; inicio y fin quedan intactos, incluso en vacío.
    while (actual != null) {
      action.accept(actual.dato);
      actual = actual.sig;
    }
  }

  /**
   * Recorre desde el último al primero siguiendo ant, O(n).
   * @param action acción de consulta para cada dato; no debe modificar la lista
   */
  public void forEachReverse(Consumer<? super T> action) {
    Nodo<T> actual = fin;
    while (actual != null) {
      action.accept(actual.dato);
      actual = actual.ant;
    }
  }
}
