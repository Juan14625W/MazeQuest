package mazequest.structures;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

/** Pruebas de contratos, límites y enlaces, sin colecciones preparadas. */
class StructuresTest {
  /** Constructor usado por JUnit. */
  StructuresTest() {}

  /** Comprueba LIFO, tamaño, peek, null y reutilización tras vaciar. */
  @Test
  void stackContract() {
    CustomStack<Integer> s = new CustomStack<>();
    assertTrue(s.isEmpty());
    assertThrows(NoSuchElementException.class, s::pop);
    assertThrows(NoSuchElementException.class, s::peek);
    s.push(1);
    s.push(2);
    s.push(3);
    assertEquals(3, s.size());
    assertEquals(3, s.peek());
    StringBuilder b = new StringBuilder();
    s.forEach(b::append);
    assertEquals("321", b.toString());
    assertEquals(3, s.pop());
    assertEquals(2, s.pop());
    assertEquals(1, s.pop());
    s.push(null);
    assertNull(s.pop());
    assertEquals(0, s.size());
  }

  /** Comprueba FIFO y que final se limpia al retirar el único nodo. */
  @Test
  void queueContract() {
    CustomQueue<Integer> q = new CustomQueue<>();
    assertTrue(q.isEmpty());
    assertThrows(NoSuchElementException.class, q::dequeue);
    assertThrows(NoSuchElementException.class, q::peek);
    q.enqueue(1);
    q.enqueue(2);
    q.enqueue(3);
    assertEquals(3, q.size());
    assertEquals(1, q.peek());
    StringBuilder b = new StringBuilder();
    q.forEach(b::append);
    assertEquals("123", b.toString());
    assertEquals(1, q.dequeue());
    assertEquals(2, q.dequeue());
    assertEquals(3, q.dequeue());
    q.enqueue(null);
    assertNull(q.dequeue());
    assertTrue(q.isEmpty());
  }

  /** Verifica vacío, singleton circular, todas las inserciones y búsquedas. */
  @Test
  void circularInsertAndCycle() {
    CircularLinkedList<Integer> l = new CircularLinkedList<>();
    assertThrows(NoSuchElementException.class, l::cursor);
    assertEquals(-1, l.indexOf(7));
    l.addLast(2);
    var one = l.cursor();
    for (int i = 0; i < 5; i++) {
      assertEquals(2, one.value());
      one.advance();
    }
    l.addFirst(0);
    l.insert(1, 1);
    l.insert(3, 3);
    assertThrows(ConcurrentModificationException.class, one::value);
    assertEquals(4, l.size());
    assertEquals(2, l.indexOf(2));
    StringBuilder b = new StringBuilder();
    l.forEach(b::append);
    assertEquals("0123", b.toString());
    var cursor = l.cursor();
    for (int i = 0; i < 16; i++) {
      assertEquals(i % 4, cursor.value());
      cursor.advance();
    }
    cursor.seek(3);
    cursor.advance();
    assertEquals(0, cursor.index());
    assertThrows(IndexOutOfBoundsException.class, () -> l.insert(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> l.insert(5, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> cursor.seek(4));
  }

  /** Elimina primero, medio, último y singleton, comprobando el cierre del ciclo. */
  @Test
  void circularRemove() {
    CircularLinkedList<Integer> l = new CircularLinkedList<>();
    for (int i = 0; i < 5; i++) l.addLast(i);
    assertEquals(0, l.remove(0));
    assertEquals(2, l.remove(1));
    assertEquals(4, l.remove(2));
    var c = l.cursor();
    for (int i = 0; i < 8; i++) {
      assertEquals(i % 2 == 0 ? 1 : 3, c.value());
      c.advance();
    }
    assertEquals(3, l.remove(1));
    assertEquals(1, l.remove(0));
    assertTrue(l.isEmpty());
    assertThrows(IndexOutOfBoundsException.class, () -> l.remove(0));
    l.addFirst(null);
    assertEquals(0, l.indexOf(null));
    assertNull(l.remove(0));
  }

  /** Dos nodos no son singleton: retirar cabecera conserva el sobreviviente y su ciclo. */
  @Test
  void circularTwoNodesAndCursorInvalidation() {
    CircularLinkedList<Integer> lista = new CircularLinkedList<>();
    lista.addLast(11);
    lista.addLast(8);
    var antiguo = lista.cursor();
    assertEquals(11, lista.remove(0));
    assertEquals(1, lista.size());
    var sobreviviente = lista.cursor();
    for (int i = 0; i < 4; i++) {
      assertEquals(8, sobreviviente.value());
      sobreviviente.advance();
      assertEquals(0, sobreviviente.index());
    }
    assertThrows(ConcurrentModificationException.class, antiguo::value);
    assertThrows(ConcurrentModificationException.class, antiguo::advance);
    assertThrows(ConcurrentModificationException.class, antiguo::index);
    assertThrows(ConcurrentModificationException.class, () -> antiguo.seek(0));
    lista.addFirst(23);
    assertEquals(8, lista.remove(1));
    assertEquals(0, lista.indexOf(23));
    assertEquals(-1, lista.indexOf(8));
    assertEquals(23, lista.remove(0));
    StringBuilder recorrido = new StringBuilder();
    lista.forEach(recorrido::append);
    assertEquals("", recorrido.toString());
    lista.addLast(17);
    assertEquals(17, lista.get(0));
  }

  /** Usa ambos recorridos para detectar enlaces prev o next rotos. */
  @Test
  void doublyLinks() {
    DoublyLinkedList<Integer> l = new DoublyLinkedList<>();
    assertTrue(l.isEmpty());
    assertThrows(IndexOutOfBoundsException.class, () -> l.get(0));
    l.addLast(2);
    l.addFirst(0);
    l.insert(1, 1);
    l.insert(3, 3);
    l.addLast(4);
    assertEquals(5, l.size());
    assertEquals(3, l.indexOf(3));
    assertEquals(-1, l.indexOf(9));
    StringBuilder f = new StringBuilder(), r = new StringBuilder();
    l.forEach(f::append);
    l.forEachReverse(r::append);
    assertEquals("01234", f.toString());
    assertEquals("43210", r.toString());
    assertEquals(0, l.remove(0));
    assertEquals(2, l.remove(1));
    assertEquals(4, l.remove(2));
    f.setLength(0);
    r.setLength(0);
    l.forEach(f::append);
    l.forEachReverse(r::append);
    assertEquals("13", f.toString());
    assertEquals("31", r.toString());
    assertEquals(3, l.get(1));
    l.remove(1);
    l.remove(0);
    assertTrue(l.isEmpty());
    l.addFirst(null);
    assertEquals(0, l.indexOf(null));
    assertNull(l.remove(0));
    assertThrows(IndexOutOfBoundsException.class, () -> l.insert(1, 1));
    assertThrows(IndexOutOfBoundsException.class, () -> l.remove(-1));
  }

  /** Quitar cabecera debe limpiar el enlace anterior; quitar último debe limpiar el siguiente. */
  @Test
  void doublyEndpointsAfterRemoval() {
    DoublyLinkedList<Integer> lista = new DoublyLinkedList<>();
    lista.addLast(11);
    lista.addLast(8);
    assertEquals(11, lista.remove(0));
    StringBuilder atras = new StringBuilder();
    lista.forEachReverse(atras::append);
    assertEquals("8", atras.toString());
    lista.addFirst(23);
    assertEquals(8, lista.remove(1));
    StringBuilder adelante = new StringBuilder();
    lista.forEach(adelante::append);
    assertEquals("23", adelante.toString());
    assertEquals(23, lista.remove(0));
    assertTrue(lista.isEmpty());
    lista.addLast(17);
    lista.addFirst(10);
    atras.setLength(0);
    lista.forEachReverse(v -> atras.append(v).append(" "));
    assertEquals("17 10 ", atras.toString());
  }

  /** Comprueba secuencias exactas y duplicados ignorados. */
  @Test
  void treeTraversals() {
    BinarySearchTree t = new BinarySearchTree();
    assertTrue(t.isEmpty());
    assertFalse(t.remove(4));
    for (int k : new int[] {4, 2, 6, 1, 3, 5, 7}) assertTrue(t.insert(k));
    assertFalse(t.insert(4));
    assertEquals(7, t.size());
    assertTrue(t.contains(5));
    assertFalse(t.contains(8));
    StringBuilder a = new StringBuilder(), b = new StringBuilder(), c = new StringBuilder();
    t.inOrder(a::append);
    t.preOrder(b::append);
    t.postOrder(c::append);
    assertEquals("1234567", a.toString());
    assertEquals("4213657", b.toString());
    assertEquals("1325764", c.toString());
  }

  /** Elimina hoja, un hijo, raíz con dos hijos y vacía todo el árbol. */
  @Test
  void treeRemoveCases() {
    BinarySearchTree t = new BinarySearchTree();
    for (int k : new int[] {4, 2, 6, 1, 3, 5, 7, 8}) t.insert(k);
    assertTrue(t.remove(1));
    assertTrue(t.remove(7));
    assertTrue(t.contains(8));
    assertTrue(t.remove(4));
    assertFalse(t.contains(4));
    StringBuilder s = new StringBuilder();
    t.inOrder(s::append);
    assertEquals("23568", s.toString());
    for (int k : new int[] {2, 3, 5, 6, 8}) assertTrue(t.remove(k));
    assertEquals(0, t.size());
    assertFalse(t.remove(8));
    t.insert(1);
    t.insert(2);
    assertTrue(t.remove(1));
    assertTrue(t.contains(2));
  }

  /** Reproduce el árbol de la página 10 de Árboles y sus tres órdenes exactos. */
  @Test
  void classroomTreeTraversals() {
    BinarySearchTree arbol = new BinarySearchTree();
    for (int dato : new int[] {10, 5, 12, 3, 7, 11, 15}) {
      assertTrue(arbol.insertar(dato));
    }
    assertFalse(arbol.insertar(10));
    StringBuilder in = new StringBuilder(), pre = new StringBuilder(), pos = new StringBuilder();
    arbol.inorden(dato -> in.append(dato).append(" "));
    arbol.preorden(dato -> pre.append(dato).append(" "));
    arbol.posorden(dato -> pos.append(dato).append(" "));
    assertEquals("3 5 7 10 11 12 15 ", in.toString());
    assertEquals("10 5 3 7 12 11 15 ", pre.toString());
    assertEquals("3 7 5 11 15 12 10 ", pos.toString());
    assertTrue(arbol.buscar(11));
    assertTrue(arbol.eliminar(10));
    assertFalse(arbol.buscar(10));
    in.setLength(0);
    arbol.inorden(dato -> in.append(dato).append(" "));
    assertEquals("3 5 7 11 12 15 ", in.toString());
  }

  /** Conserva el caso de 2000 nodos y ejercita los tres recorridos recursivos en la JVM usada. */
  @Test
  void degenerateTree() {
    BinarySearchTree t = new BinarySearchTree();
    for (int i = 0; i < 2000; i++) t.insert(i);
    int[] count = {0};
    t.postOrder(k -> count[0]++);
    assertEquals(2000, count[0]);
    count[0] = 0;
    t.inOrder(k -> count[0]++);
    assertEquals(2000, count[0]);
    count[0] = 0;
    t.preOrder(k -> count[0]++);
    assertEquals(2000, count[0]);
    assertTrue(t.remove(1000));
    assertEquals(1999, t.size());
  }
}
