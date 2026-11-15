"""Diagramas vectoriales de clases reales para el manual técnico."""

from math import atan2, cos, sin
from reportlab.graphics.shapes import Drawing, Rect, Line, String, Polygon
from reportlab.lib import colors

INK = colors.HexColor("#142b3c")
TEAL = colors.HexColor("#14796e")


def box(drawing, x, y, width, height, title, attributes, methods):
    """Dibuja los compartimentos de nombre, atributos y operaciones seleccionadas."""
    drawing.add(Rect(x, y, width, height, fillColor=colors.white, strokeColor=TEAL))
    drawing.add(Rect(x, y + height - 23, width, 23,
                     fillColor=colors.HexColor("#dbeeea"), strokeColor=TEAL))
    drawing.add(String(x + 8, y + height - 15, title,
                       fontName="Helvetica-Bold", fontSize=9, fillColor=INK))
    baseline = y + height - 37
    for text in attributes:
        drawing.add(String(x + 8, baseline, text, fontSize=8, fillColor=INK))
        baseline -= 12
    drawing.add(Line(x, baseline + 5, x + width, baseline + 5, strokeColor=TEAL))
    baseline -= 8
    for text in methods:
        drawing.add(String(x + 8, baseline, text, fontSize=8, fillColor=INK))
        baseline -= 12


def arrow(drawing, x1, y1, x2, y2, dashed=False, composition=False):
    """Traza dependencia discontinua, asociación dirigida o composición con rombo."""
    line = Line(x1, y1, x2, y2, strokeColor=INK, strokeWidth=1)
    if dashed:
        line.strokeDashArray = [3, 3]
    drawing.add(line)
    angle = atan2(y2 - y1, x2 - x1)
    for offset in (-0.5, 0.5):
        drawing.add(Line(x2, y2, x2 - 7 * cos(angle + offset),
                         y2 - 7 * sin(angle + offset), strokeColor=INK))
    if composition:
        dx, dy = cos(angle), sin(angle)
        drawing.add(Polygon([x1, y1, x1 + 5*dx - 3*dy, y1 + 5*dy + 3*dx,
                             x1 + 10*dx, y1 + 10*dy,
                             x1 + 5*dx + 3*dy, y1 + 5*dy - 3*dx],
                            fillColor=INK, strokeColor=INK))


def core_diagram():
    """Muestra el flujo de dependencias sin introducir acoplamientos con Swing en el motor."""
    d = Drawing(499, 510)
    box(d, 145, 445, 210, 62, "Main", [], ["+ main(args)"])
    box(d, 145, 335, 210, 85, "GamePanel : JPanel", ["- controller: GameController"],
        ["# paintComponent(g)", "- technical(g)"])
    box(d, 145, 205, 210, 104, "GameController",
        ["- game: GameEngine", "- search: SearchResult", "- revision: long"],
        ["+ start(level), move(dr, dc)", "+ undo(), restart(), hint()"])
    box(d, 0, 70, 225, 105, "GameEngine",
        ["- maze: Maze; enemies: Enemy[]", "- undo: CustomStack<GameSnapshot>",
         "- history: DoublyLinkedList<MoveRecord>"], ["+ move(dr, dc), undo(), restart()"])
    box(d, 274, 70, 225, 105, "BreadthFirstSearch", [],
        ["+ find(maze, start, goal): SearchResult", "Cola y BST locales; matriz de predecesores"])
    box(d, 0, 0, 225, 50, "Maze", [], ["+ walkable(p), start(), exit(), route(i)"])
    box(d, 274, 0, 225, 50, "SearchResult", [],
        ["+ path(), explored(), frontier(), treeText(k)"])
    arrow(d, 250, 445, 250, 420, dashed=True)
    arrow(d, 250, 335, 250, 309, composition=True)
    arrow(d, 195, 205, 112, 175, composition=True)
    arrow(d, 305, 205, 386, 175, dashed=True)
    arrow(d, 112, 70, 112, 50)
    arrow(d, 386, 70, 386, 50, dashed=True)
    return d


def structures_diagram():
    """Relaciona cada estructura con su nodo privado y los enlaces realmente implementados."""
    d = Drawing(499, 560)
    rows = [
        ("CustomStack<T>", ["- top: Node<T>; size: int"],
         ["+ push, pop, peek, size, isEmpty"], ["value: T", "next: Node<T>"],
         "GameEngine.undo guarda GameSnapshot (jugador, vidas, turno, estado y fases)."),
        ("CustomQueue<T>", ["- first, last: Node<T>; size: int"],
         ["+ enqueue, dequeue, peek, size, isEmpty"], ["value: T", "next: Node<T>"],
         "BreadthFirstSearch usa una cola de Position para expandir por capas."),
        ("CircularLinkedList<T>", ["- tail: Node<T>; size, version: int"],
         ["+ addFirst, addLast, insert, remove", "+ indexOf, forEach, cursor"],
         ["value: T", "next: Node<T> (cierra el ciclo)"],
         "Enemy conserva ruta y Cursor; Cursor.current apunta a un nodo de esa lista."),
        ("DoublyLinkedList<T>", ["- first, last: Node<T>; size: int"],
         ["+ addFirst, addLast, insert, remove", "+ indexOf, forEach, forEachReverse"],
         ["value: T", "prev, next: Node<T>"],
         "GameEngine.history guarda MoveRecord (origen, intento, final y daño)."),
        ("BinarySearchTree", ["- root: Node; size: int"],
         ["+ insert, contains, remove", "+ inOrder, preOrder, postOrder"],
         ["key: int", "left, right: Node"],
         "BreadthFirstSearch usa las claves de celdas para registrar visitados."),
    ]
    for i, (title, attrs, methods, links, use) in enumerate(rows):
        y = 455 - i * 112
        box(d, 0, y, 235, 88, title, attrs, methods)
        box(d, 299, y, 200, 88, "Node privado de " + title.split("<")[0], links, [])
        arrow(d, 235, y + 44, 299, y + 44, composition=True)
        d.add(String(258, y + 53, "0..*", fontSize=8, fillColor=INK))
        d.add(String(0, y - 13, use, fontSize=8, fillColor=INK))
    return d
