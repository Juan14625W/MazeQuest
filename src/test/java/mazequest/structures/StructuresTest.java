package mazequest.structures;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de contratos, límites y enlaces, sin colecciones preparadas. */
class StructuresTest {
    /** Constructor usado por JUnit. */
    StructuresTest() { }
    /** Comprueba LIFO, tamaño, peek, null y reutilización tras vaciar. */
    @Test void stackContract() {
        CustomStack<Integer> s = new CustomStack<>();
        assertTrue(s.isEmpty()); assertThrows(NoSuchElementException.class, s::pop);
        assertThrows(NoSuchElementException.class, s::peek);
        s.push(1); s.push(2); s.push(3);
        assertEquals(3, s.size()); assertEquals(3, s.peek());
        StringBuilder b = new StringBuilder(); s.forEach(b::append); assertEquals("321", b.toString());
        assertEquals(3, s.pop()); assertEquals(2, s.pop()); assertEquals(1, s.pop());
        s.push(null); assertNull(s.pop()); assertEquals(0, s.size());
    }
    /** Comprueba FIFO y que final se limpia al retirar el único nodo. */
    @Test void queueContract() {
        CustomQueue<Integer> q = new CustomQueue<>();
        assertTrue(q.isEmpty()); assertThrows(NoSuchElementException.class, q::dequeue);
        assertThrows(NoSuchElementException.class, q::peek);
        q.enqueue(1); q.enqueue(2); q.enqueue(3);
        assertEquals(3, q.size()); assertEquals(1, q.peek());
        StringBuilder b = new StringBuilder(); q.forEach(b::append); assertEquals("123", b.toString());
        assertEquals(1, q.dequeue()); assertEquals(2, q.dequeue()); assertEquals(3, q.dequeue());
        q.enqueue(null); assertNull(q.dequeue()); assertTrue(q.isEmpty());
    }
    /** Verifica vacío, singleton circular, todas las inserciones y búsquedas. */
    @Test void circularInsertAndCycle() {
        CircularLinkedList<Integer> l = new CircularLinkedList<>();
        assertThrows(NoSuchElementException.class, l::cursor);
        assertEquals(-1, l.indexOf(7));
        l.addLast(2); var one = l.cursor();
        for (int i = 0; i < 5; i++) { assertEquals(2, one.value()); one.advance(); }
        l.addFirst(0); l.insert(1, 1); l.insert(3, 3);
        assertThrows(ConcurrentModificationException.class, one::value);
        assertEquals(4, l.size()); assertEquals(2, l.indexOf(2));
        StringBuilder b = new StringBuilder(); l.forEach(b::append); assertEquals("0123", b.toString());
        var cursor = l.cursor();
        for (int i = 0; i < 16; i++) { assertEquals(i % 4, cursor.value()); cursor.advance(); }
        cursor.seek(3); cursor.advance(); assertEquals(0, cursor.index());
        assertThrows(IndexOutOfBoundsException.class, () -> l.insert(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> l.insert(5, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> cursor.seek(4));
    }
    /** Elimina primero, medio, último y singleton, comprobando el cierre del ciclo. */
    @Test void circularRemove() {
        CircularLinkedList<Integer> l = new CircularLinkedList<>();
        for (int i = 0; i < 5; i++) l.addLast(i);
        assertEquals(0, l.remove(0)); assertEquals(2, l.remove(1));
        assertEquals(4, l.remove(2));
        var c = l.cursor();
        for (int i = 0; i < 8; i++) { assertEquals(i % 2 == 0 ? 1 : 3, c.value()); c.advance(); }
        assertEquals(3, l.remove(1)); assertEquals(1, l.remove(0)); assertTrue(l.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(0));
        l.addFirst(null); assertEquals(0, l.indexOf(null)); assertNull(l.remove(0));
    }
    /** Usa ambos recorridos para detectar enlaces prev o next rotos. */
    @Test void doublyLinks() {
        DoublyLinkedList<Integer> l = new DoublyLinkedList<>();
        assertTrue(l.isEmpty()); assertThrows(IndexOutOfBoundsException.class, () -> l.get(0));
        l.addLast(2); l.addFirst(0); l.insert(1, 1); l.insert(3, 3); l.addLast(4);
        assertEquals(5, l.size()); assertEquals(3, l.indexOf(3)); assertEquals(-1, l.indexOf(9));
        StringBuilder f = new StringBuilder(), r = new StringBuilder();
        l.forEach(f::append); l.forEachReverse(r::append);
        assertEquals("01234", f.toString()); assertEquals("43210", r.toString());
        assertEquals(0, l.remove(0)); assertEquals(2, l.remove(1)); assertEquals(4, l.remove(2));
        f.setLength(0); r.setLength(0); l.forEach(f::append); l.forEachReverse(r::append);
        assertEquals("13", f.toString()); assertEquals("31", r.toString());
        assertEquals(3, l.get(1)); l.remove(1); l.remove(0); assertTrue(l.isEmpty());
        l.addFirst(null); assertEquals(0, l.indexOf(null)); assertNull(l.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> l.insert(1, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(-1));
    }
    /** Comprueba secuencias exactas y duplicados ignorados. */
    @Test void treeTraversals() {
        BinarySearchTree t = new BinarySearchTree();
        assertTrue(t.isEmpty()); assertFalse(t.remove(4));
        for (int k : new int[]{4,2,6,1,3,5,7}) assertTrue(t.insert(k));
        assertFalse(t.insert(4)); assertEquals(7, t.size()); assertTrue(t.contains(5)); assertFalse(t.contains(8));
        StringBuilder a = new StringBuilder(), b = new StringBuilder(), c = new StringBuilder();
        t.inOrder(a::append); t.preOrder(b::append); t.postOrder(c::append);
        assertEquals("1234567", a.toString()); assertEquals("4213657", b.toString()); assertEquals("1325764", c.toString());
    }
    /** Elimina hoja, un hijo, raíz con dos hijos y vacía todo el árbol. */
    @Test void treeRemoveCases() {
        BinarySearchTree t = new BinarySearchTree();
        for (int k : new int[]{4,2,6,1,3,5,7,8}) t.insert(k);
        assertTrue(t.remove(1)); assertTrue(t.remove(7)); assertTrue(t.contains(8));
        assertTrue(t.remove(4)); assertFalse(t.contains(4));
        StringBuilder s = new StringBuilder(); t.inOrder(s::append); assertEquals("23568", s.toString());
        for (int k : new int[]{2,3,5,6,8}) assertTrue(t.remove(k));
        assertEquals(0,t.size()); assertFalse(t.remove(8));
        t.insert(1); t.insert(2); assertTrue(t.remove(1)); assertTrue(t.contains(2));
    }
    /** Un árbol degenerado sigue siendo válido y sus recorridos no son recursivos. */
    @Test void degenerateTree() {
        BinarySearchTree t = new BinarySearchTree();
        for (int i = 0; i < 2000; i++) t.insert(i);
        int[] count = {0}; t.postOrder(k -> count[0]++);
        assertEquals(2000, count[0]); assertTrue(t.remove(1000)); assertEquals(1999,t.size());
    }
}