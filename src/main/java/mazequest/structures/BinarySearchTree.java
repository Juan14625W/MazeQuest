package mazequest.structures;

import java.util.function.IntConsumer;

/**
 * Árbol binario de búsqueda propio de claves de celdas visitadas por BFS.
 * Sigue Árboles, pp. 11-15: raiz, Nodo con info/izq/der, comparación e inserción mediante
 * referencias auxiliares, y recorridos recursivos. Invariante: izquierda menor y derecha mayor;
 * no se repiten claves. Insertar/buscar/eliminar O(h), peor O(n) sin balanceo; recorridos O(n).
 * Memoria de nodos O(n); recorridos O(h) en la pila de llamadas Java. Una profundidad externa
 * muy grande puede agotar esa pila; no se promete tamaño ilimitado. Los niveles actuales tienen
 * como máximo 273 celdas. IntConsumer solo recibe cada clave, en lugar del print de clase;
 * no decide el orden ni modifica enlaces. Así la estructura no depende de Swing ni de consola.
 */
public final class BinarySearchTree {
  /** Nodo propio: información entera y referencias a los dos subárboles. */
  private static final class Nodo {
    private int info;
    private Nodo izq;
    private Nodo der;

    /**
     * Crea una hoja sin hijos, como Nodo(int dato) en la presentación.
     * @param dato clave de la celda
     */
    private Nodo(int dato) {
      info = dato;
      izq = null;
      der = null;
    }
  }

  private Nodo raiz;
  private int cantidad;

  /** Inicializa el árbol vacío, sin imprimir mensajes ni abrir ventanas. */
  public BinarySearchTree() {
    raiz = null;
    cantidad = 0;
  }

  /**
   * Busca el punto de inserción por comparación y enlaza una hoja en O(h).
   * @param dato clave que se quiere insertar
   * @return true si se añadió, false si ya estaba y el árbol no cambió
   */
  public boolean insertar(int dato) {
    Nodo actual = raiz;
    Nodo anterior = null;
    // actual corresponde a q y anterior a p en la búsqueda de punto de inserción de clase.
    while (actual != null) {
      anterior = actual;
      if (dato == actual.info) {
        return false;
      } else if (dato < actual.info) {
        actual = actual.izq;
      } else {
        actual = actual.der;
      }
    }
    Nodo nuevo = new Nodo(dato);
    if (anterior == null) {
      // No había raíz: la primera hoja inicia el árbol.
      raiz = nuevo;
    } else if (dato < anterior.info) {
      anterior.izq = nuevo;
    } else {
      anterior.der = nuevo;
    }
    cantidad++;
    return true;
  }

  /**
   * Busca una clave siguiendo solo el subárbol donde podría encontrarse, O(h).
   * @param dato clave buscada
   * @return true si existe, false si se llega a una referencia null
   */
  public boolean buscar(int dato) {
    Nodo actual = raiz;
    while (actual != null) {
      if (dato == actual.info) {
        return true;
      } else if (dato < actual.info) {
        actual = actual.izq;
      } else {
        actual = actual.der;
      }
    }
    return false;
  }

  /**
   * Elimina hoja, nodo con un hijo o nodo con dos hijos, O(h).
   * Completa la operación exigida en la rúbrica, no desarrollada en las pp. 11-15.
   * @param dato clave a retirar
   * @return true si existía y se eliminó; false deja el árbol intacto
   */
  public boolean eliminar(int dato) {
    Nodo actual = raiz;
    Nodo anterior = null;
    while (actual != null && actual.info != dato) {
      anterior = actual;
      if (dato < actual.info) {
        actual = actual.izq;
      } else {
        actual = actual.der;
      }
    }
    if (actual == null) {
      return false;
    }
    if (actual.izq != null && actual.der != null) {
      // Dos hijos: buscamos el menor del subárbol derecho (sucesor inorden).
      // Copiar esa clave conserva el orden; luego quitamos su nodo original.
      Nodo sucesor = actual.der;
      anterior = actual;
      while (sucesor.izq != null) {
        anterior = sucesor;
        sucesor = sucesor.izq;
      }
      actual.info = sucesor.info;
      actual = sucesor;
    }
    // Ahora actual tiene como máximo un hijo: el sucesor nunca tiene hijo izquierdo.
    Nodo hijo;
    if (actual.izq != null) {
      hijo = actual.izq;
    } else {
      hijo = actual.der;
    }
    if (anterior == null) {
      raiz = hijo;
    } else if (anterior.izq == actual) {
      anterior.izq = hijo;
    } else {
      anterior.der = hijo;
    }
    // Para una hoja, hijo es null. Si era la única raíz, el árbol queda vacío.
    cantidad--;
    return true;
  }

  /**
   * Consulta el número de claves distintas en O(1).
   * @return cantidad de nodos
   */
  public int size() { return cantidad; }

  /**
   * Consulta si no hay raíz, O(1).
   * @return true cuando el árbol está vacío
   */
  public boolean isEmpty() { return raiz == null; }

  /**
   * Recorre izquierda-raíz-derecha en O(n), con O(h) llamadas pendientes.
   * @param action receptor de cada clave; no debe modificar el árbol
   */
  public void inorden(IntConsumer action) { inorden(raiz, action); }

  /**
   * Visita recursivamente un subárbol en orden ascendente.
   * @param aux raíz del subárbol, o null para terminar esa rama
   * @param action receptor de cada clave
   */
  private void inorden(Nodo aux, IntConsumer action) {
    if (aux != null) {
      inorden(aux.izq, action);
      action.accept(aux.info);
      inorden(aux.der, action);
    }
  }

  /**
   * Recorre raíz-izquierda-derecha en O(n), con O(h) llamadas pendientes.
   * @param action receptor de cada clave; no debe modificar el árbol
   */
  public void preorden(IntConsumer action) { preorden(raiz, action); }

  /**
   * Visita la raíz antes de descender a sus hijos.
   * @param aux raíz del subárbol, o null para terminar esa rama
   * @param action receptor de cada clave
   */
  private void preorden(Nodo aux, IntConsumer action) {
    if (aux != null) {
      action.accept(aux.info);
      preorden(aux.izq, action);
      preorden(aux.der, action);
    }
  }

  /**
   * Recorre izquierda-derecha-raíz en O(n), con O(h) llamadas pendientes.
   * @param action receptor de cada clave; no debe modificar el árbol
   */
  public void posorden(IntConsumer action) { posorden(raiz, action); }

  /**
   * Visita ambos hijos antes de entregar la raíz; null es el caso base.
   * @param aux raíz del subárbol, o null para terminar esa rama
   * @param action receptor de cada clave
   */
  private void posorden(Nodo aux, IntConsumer action) {
    if (aux != null) {
      posorden(aux.izq, action);
      posorden(aux.der, action);
      action.accept(aux.info);
    }
  }

  /**
   * Conserva el nombre previo de insertar, O(h).
   * @param dato clave a insertar
   * @return true si se añadió
   */
  public boolean insert(int dato) { return insertar(dato); }

  /**
   * Conserva el nombre previo de buscar, O(h).
   * @param dato clave buscada
   * @return true si existe
   */
  public boolean contains(int dato) { return buscar(dato); }

  /**
   * Conserva el nombre previo de eliminar, O(h).
   * @param dato clave a retirar
   * @return true si existía
   */
  public boolean remove(int dato) { return eliminar(dato); }

  /**
   * Conserva el nombre previo de inorden, O(n), auxiliar O(h).
   * @param action receptor de cada clave; no debe modificar el árbol
   */
  public void inOrder(IntConsumer action) { inorden(action); }

  /**
   * Conserva el nombre previo de preorden, O(n), auxiliar O(h).
   * @param action receptor de cada clave; no debe modificar el árbol
   */
  public void preOrder(IntConsumer action) { preorden(action); }

  /**
   * Conserva el nombre previo de posorden, O(n), auxiliar O(h).
   * @param action receptor de cada clave; no debe modificar el árbol
   */
  public void postOrder(IntConsumer action) { posorden(action); }
}
