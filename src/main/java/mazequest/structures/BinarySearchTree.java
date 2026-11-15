package mazequest.structures;

import java.util.function.IntConsumer;

/**
 * BST de claves enteras visitadas por BFS, sin balanceo ni colecciones externas. Duplicados se
 * ignoran. Operaciones O(h), peor O(n); recorridos O(n). Espacio total O(n); recorridos usan pila
 * propia O(h), evitando desbordar la pila Java.
 */
public final class BinarySearchTree {
  /** Nodo: izquierda menor, derecha mayor, sin duplicados. */
  private static final class Node {
    private int key;
    private Node left;
    private Node right;

    /**
     * Guarda una clave.
     *
     * @param key clave única
     */
    private Node(int key) {
      this.key = key;
    }
  }

  private Node root;
  private int size;

  /** Crea un árbol vacío. */
  public BinarySearchTree() {}

  /**
   * Inserta en O(h).
   *
   * @param key clave
   * @return true si se añadió
   */
  public boolean insert(int key) {
    if (root == null) {
      root = new Node(key);
      size++;
      return true;
    }
    Node n = root;
    while (true) {
      if (key == n.key) return false;
      if (key < n.key) {
        if (n.left == null) {
          n.left = new Node(key);
          break;
        }
        n = n.left;
      } else {
        if (n.right == null) {
          n.right = new Node(key);
          break;
        }
        n = n.right;
      }
    }
    size++;
    return true;
  }

  /**
   * Busca O(h).
   *
   * @param key clave
   * @return presencia
   */
  public boolean contains(int key) {
    Node n = root;
    while (n != null) {
      if (key == n.key) return true;
      n = key < n.key ? n.left : n.right;
    }
    return false;
  }

  /**
   * Elimina hoja, un hijo o dos hijos en O(h).
   *
   * @param key clave
   * @return si existía
   */
  public boolean remove(int key) {
    Node parent = null, n = root;
    while (n != null && n.key != key) {
      parent = n;
      n = key < n.key ? n.left : n.right;
    }
    if (n == null) return false;
    if (n.left != null && n.right != null) {
      // Sustituir por el sucesor inorden conserva el orden; ese sucesor
      // tiene como máximo un hijo y se desconecta con el caso simple.
      Node successor = n.right;
      parent = n;
      while (successor.left != null) {
        parent = successor;
        successor = successor.left;
      }
      n.key = successor.key;
      n = successor;
    }
    Node child = n.left != null ? n.left : n.right;
    if (parent == null) root = child;
    else if (parent.left == n) parent.left = child;
    else parent.right = child;
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
   * Inorden ascendente O(n), auxiliar O(h).
   *
   * @param action visitante
   */
  public void inOrder(IntConsumer action) {
    CustomStack<Node> stack = new CustomStack<>();
    Node n = root;
    while (n != null || !stack.isEmpty()) {
      while (n != null) {
        stack.push(n);
        n = n.left;
      }
      n = stack.pop();
      action.accept(n.key);
      n = n.right;
    }
  }

  /**
   * Preorden raíz-izquierda-derecha O(n).
   *
   * @param action visitante
   */
  public void preOrder(IntConsumer action) {
    CustomStack<Node> stack = new CustomStack<>();
    if (root != null) stack.push(root);
    while (!stack.isEmpty()) {
      Node n = stack.pop();
      action.accept(n.key);
      // Apilar derecha primero hace que izquierda se visite antes.
      if (n.right != null) stack.push(n.right);
      if (n.left != null) stack.push(n.left);
    }
  }

  /**
   * Posorden izquierda-derecha-raíz O(n), auxiliar O(h).
   *
   * @param action visitante
   */
  public void postOrder(IntConsumer action) {
    CustomStack<Node> stack = new CustomStack<>();
    Node n = root, visited = null;
    while (n != null || !stack.isEmpty()) {
      if (n != null) {
        stack.push(n);
        n = n.left;
      } else {
        Node top = stack.peek();
        if (top.right != null && top.right != visited) n = top.right;
        else {
          action.accept(top.key);
          visited = stack.pop();
        }
      }
    }
  }
}
