package mazequest.structures;

import java.util.function.IntConsumer;

/**
 * BST de claves enteras visitadas por BFS, sin balanceo ni colecciones externas. Duplicados se
 * ignoran. Operaciones O(h), peor O(n); recorridos O(n). Espacio total O(n); recorridos son recursivos O(h) de memoria auxiliar, como en la clase.
 */
public final class BinarySearchTree {
  /** Nodo: izquierda menor, derecha mayor, sin duplicados. */
  private static final class Nodo {
    private int dato;
    private Nodo izq;
    private Nodo der;

    /**
     * Guarda una clave.
     *
     * @param dato clave única
     */
    private Nodo(int dato) {
      this.dato = dato;
    }
  }

  private Nodo raiz;
  private int size;

  /** Crea un árbol vacío. */
  public BinarySearchTree() {}

  /**
   * Inserta en O(h).
   *
   * @param dato clave
   * @return true si se añadió
   */
  public boolean insert(int dato) {
    if (raiz == null) {
      raiz = new Nodo(dato);
      size++;
      return true;
    }
    Nodo n = raiz;
    while (true) {
      if (dato == n.dato) return false;
      if (dato < n.dato) {
        if (n.izq == null) {
          n.izq = new Nodo(dato);
          break;
        }
        n = n.izq;
      } else {
        if (n.der == null) {
          n.der = new Nodo(dato);
          break;
        }
        n = n.der;
      }
    }
    size++;
    return true;
  }

  /**
   * Busca O(h).
   *
   * @param dato clave
   * @return presencia
   */
  public boolean contains(int dato) {
    Nodo n = raiz;
    while (n != null) {
      if (dato == n.dato) return true;
      n = dato < n.dato ? n.izq : n.der;
    }
    return false;
  }

  /**
   * Elimina hoja, un hijo o dos hijos en O(h).
   *
   * @param dato clave
   * @return si existía
   */
  public boolean remove(int dato) {
    Nodo parent = null, n = raiz;
    while (n != null && n.dato != dato) {
      parent = n;
      n = dato < n.dato ? n.izq : n.der;
    }
    if (n == null) return false;
    if (n.izq != null && n.der != null) {
      // Sustituir por el sucesor inorden conserva el orden; ese sucesor
      // tiene como máximo un hijo y se desconecta con el caso simple.
      Nodo successor = n.der;
      parent = n;
      while (successor.izq != null) {
        parent = successor;
        successor = successor.izq;
      }
      n.dato = successor.dato;
      n = successor;
    }
    Nodo child = n.izq != null ? n.izq : n.der;
    if (parent == null) raiz = child;
    else if (parent.izq == n) parent.izq = child;
    else parent.der = child;
    size--;
    return true;
  }

  /**
   * Cantidad O(1).
   *
   * @return claves distintas
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
   * Recorrido inorden: izquierda, raíz, derecha.
   * Su estructura recursiva coincide con el ejemplo de clase.
   *
   * @param action acción para mostrar cada dato
   */
  public void inOrder(IntConsumer action) {
    inorden(raiz, action);
  }

  /**
   * Visita recursiva inorden O(n), memoria auxiliar O(h).
   *
   * @param aux nodo actual
   * @param action salida de datos
   */
  private void inorden(Nodo aux, IntConsumer action) {
    if (aux != null) {
      inorden(aux.izq, action);
      action.accept(aux.dato);
      inorden(aux.der, action);
    }
  }

  /**
   * Recorrido preorden: raíz, izquierda, derecha.
   *
   * @param action acción para mostrar cada dato
   */
  public void preOrder(IntConsumer action) {
    preorden(raiz, action);
  }

  /**
   * Visita recursiva preorden O(n), memoria auxiliar O(h).
   *
   * @param aux nodo actual
   * @param action salida de datos
   */
  private void preorden(Nodo aux, IntConsumer action) {
    if (aux != null) {
      action.accept(aux.dato);
      preorden(aux.izq, action);
      preorden(aux.der, action);
    }
  }

  /**
   * Recorrido posorden: izquierda, derecha, raíz.
   *
   * @param action acción para mostrar cada dato
   */
  public void postOrder(IntConsumer action) {
    posorden(raiz, action);
  }

  /**
   * Visita recursiva posorden O(n), memoria auxiliar O(h).
   *
   * @param aux nodo actual
   * @param action salida de datos
   */
  private void posorden(Nodo aux, IntConsumer action) {
    if (aux != null) {
      posorden(aux.izq, action);
      posorden(aux.der, action);
      action.accept(aux.dato);
    }
  }

}
