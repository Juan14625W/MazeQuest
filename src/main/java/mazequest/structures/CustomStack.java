package mazequest.structures;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Pila LIFO de instantáneas de GameEngine para deshacer el último turno primero.
 * Sigue Pilas y Colas, pp. 34-40: Nodo guarda información y sig; tope identifica el inicio.
 * Invariante: tope es null exactamente cuando cantidad es cero. Apilar, desapilar, verTope,
 * esVacia y tamano cuestan O(1); recorrer cuesta O(n); los nodos ocupan O(n).
 * T permite guardar GameSnapshot sin convertirlo a int ni duplicar la estructura.
 *
 * @param <T> tipo de los valores, incluidos valores nulos
 */
public final class CustomStack<T> {
  /** Nodo propio: información y referencia al elemento situado debajo del tope. */
  private static final class Nodo<T> {
    private final T dato;
    private Nodo<T> sig;

    /**
     * Crea un nodo aún sin enlazar a la pila.
     *
     * @param dato información que se conservará, también puede ser null
     */
    private Nodo(T dato) {
      this.dato = dato;
      this.sig = null;
    }
  }

  private Nodo<T> tope;
  private int cantidad;

  /** Crea una pila vacía sin nodo centinela. */
  public CustomStack() {
    tope = null;
    cantidad = 0;
  }

  /**
   * Añade un dato al inicio en O(1), sin perder la cadena anterior.
   *
   * @param dato información del nuevo tope
   */
  public void apilar(T dato) {
    Nodo<T> nuevo = new Nodo<>(dato);
    // Primero conservamos el enlace al tope anterior. En vacío ese enlace es null.
    nuevo.sig = tope;
    // Solo después movemos el inicio: el último que entró será el primero en salir.
    tope = nuevo;
    cantidad++;
  }

  /**
   * Extrae en O(1).
   *
   * @return tope
   * @throws NoSuchElementException si está vacía
   */
  public T desapilar() {
    T dato = verTope();
    // Al quitar el único nodo, sig es null y la pila queda vacía.
    tope = tope.sig;
    cantidad--;
    return dato;
  }

  /**
   * Consulta en O(1).
   *
   * @return tope
   * @throws NoSuchElementException si está vacía
   */
  public T verTope() {
    if (esVacia()) {
      throw new NoSuchElementException("Pila vacía");
    }
    return tope.dato;
  }

  /**
   * Consulta cantidad en O(1).
   *
   * @return cantidad de nodos
   */
  public int tamano() {
    return cantidad;
  }

  /**
   * Consulta ausencia en O(1).
   *
   * @return true si no hay nodos
   */
  public boolean esVacia() {
    return tope == null;
  }

  /**
   * Visita desde el tope sin extraer, O(n).
   *
   * @param action acción de consulta para cada dato; no debe modificar la pila
   */
  public void forEach(Consumer<? super T> action) {
    Nodo<T> actual = tope;
    // Se mueve una referencia auxiliar; tope permanece intacto y no se desapila nada.
    while (actual != null) {
      action.accept(actual.dato);
      actual = actual.sig;
    }
  }

  /**
   * Conserva el nombre previo de apilar, O(1), sin duplicar el algoritmo.
   * @param dato información a apilar
   */
  public void push(T dato) { apilar(dato); }

  /**
   * Conserva el nombre previo de desapilar, O(1).
   * @return dato retirado
   * @throws NoSuchElementException si la pila está vacía
   */
  public T pop() { return desapilar(); }

  /**
   * Conserva el nombre previo de verTope, O(1).
   * @return dato del tope sin retirarlo
   * @throws NoSuchElementException si la pila está vacía
   */
  public T peek() { return verTope(); }

  /**
   * Conserva el nombre previo de tamano, O(1).
   * @return número de nodos
   */
  public int size() { return tamano(); }

  /**
   * Conserva el nombre previo de esVacia, O(1).
   * @return true si no hay nodos
   */
  public boolean isEmpty() {
    return esVacia();
  }
}
