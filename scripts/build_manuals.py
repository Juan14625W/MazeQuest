"""Genera los dos manuales desde Markdown y renderiza cada página para revisión visual."""

from pathlib import Path
import re
from xml.sax.saxutils import escape

import pymupdf
from class_diagrams import core_diagram, structures_diagram
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.pdfgen.canvas import Canvas
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, PageBreak,
)

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "output" / "pdf"
RENDER = ROOT / "output" / ".render"
INK = colors.HexColor("#142b3c")
TEAL = colors.HexColor("#14796e")
STYLES = getSampleStyleSheet()
STYLES.add(ParagraphStyle("BodyES", fontName="Helvetica", fontSize=10,
                         leading=14.5, textColor=INK, spaceAfter=9))
STYLES.add(ParagraphStyle("TitleES", fontName="Helvetica-Bold", fontSize=30,
                         leading=35, textColor=INK, spaceAfter=14))
STYLES.add(ParagraphStyle("HeadingES", fontName="Helvetica-Bold", fontSize=17,
                         leading=21, textColor=TEAL, spaceBefore=8,
                         spaceAfter=12, keepWithNext=True))
STYLES.add(ParagraphStyle("CellES", parent=STYLES["BodyES"], fontSize=9,
                         leading=12, spaceAfter=0))
STYLES.add(ParagraphStyle("CaptionES", parent=STYLES["BodyES"], fontSize=8,
                         leading=11, textColor=colors.HexColor("#5b6b77"),
                         spaceBefore=5, spaceAfter=12))
STYLES.add(ParagraphStyle("CodeES", fontName="Courier", fontSize=8.7,
                         leading=13, backColor=colors.HexColor("#eef4f5"),
                         borderPadding=9, spaceBefore=5, spaceAfter=12))


def inline(text):
    """Escapa texto de origen y aplica solo énfasis sencillo permitido."""
    return re.sub(r"\*\*(.*?)\*\*", r"<b>\1</b>", escape(text))


def footer(canvas, doc):
    """Añade cabecera y pie sin invadir el marco de contenido."""
    canvas.saveState()
    width, height = A4
    canvas.setStrokeColor(TEAL)
    canvas.setLineWidth(1)
    canvas.line(48, height - 37, width - 48, height - 37)
    canvas.setFillColor(TEAL)
    canvas.setFont("Helvetica-Bold", 8)
    canvas.drawString(48, height - 28, "MAZEQUEST  /  THE LOST LABYRINTH")
    canvas.setFillColor(INK)
    canvas.setFont("Helvetica", 8)
    canvas.drawString(48, 27, "Versión 1.0.0  |  Documentación en español  |  16 nov 2026")
    canvas.drawRightString(width - 48, 27, str(doc.page))
    canvas.restoreState()


class ManualCanvas(Canvas):
    """Dibuja la identificación al cerrar cada página, después de su contenido."""

    def showPage(self):
        """Añade encabezado y pie en todas las páginas, incluidas las de desbordamiento."""
        self.page = self.getPageNumber()
        footer(self, self)
        super().showPage()


def markdown_flowables(source):
    """Convierte el subconjunto usado por los manuales; rechaza imágenes fuera del proyecto."""
    lines = source.read_text(encoding="utf-8").splitlines()
    story = []
    i = 0
    available = A4[0] - 96
    while i < len(lines):
        line = lines[i].strip()
        if not line:
            i += 1
            continue
        if line == "<!-- pagebreak -->":
            story.append(PageBreak())
        elif line == "<!-- diagram: core -->":
            story.append(core_diagram())
            story.append(Spacer(1, 12))
        elif line == "<!-- diagram: structures -->":
            story.append(structures_diagram())
        elif line.startswith("# "):
            story.append(Paragraph(inline(line[2:]), STYLES["TitleES"]))
        elif line.startswith("## "):
            story.append(Paragraph(inline(line[3:]), STYLES["HeadingES"]))
        elif line.startswith("```"):
            code = []
            i += 1
            while i < len(lines) and not lines[i].startswith("```"):
                code.append(escape(lines[i]))
                i += 1
            story.append(Paragraph("<br/>".join(code), STYLES["CodeES"]))
        elif line.startswith("!["):
            match = re.fullmatch(r"!\[(.*?)\]\((.*?)\)", line)
            if not match:
                raise ValueError(f"Imagen inválida: {line}")
            path = (source.parent / match[2]).resolve()
            if not path.is_relative_to(ROOT):
                raise ValueError("La imagen debe pertenecer al proyecto")
            pic = Image(str(path))
            max_height = 250 if source.stem == "manual-usuario" else 300
            ratio = min(available / pic.imageWidth, max_height / pic.imageHeight)
            pic.drawWidth = pic.imageWidth * ratio
            pic.drawHeight = pic.imageHeight * ratio
            pic.hAlign = "CENTER"
            story.extend([pic, Paragraph(inline(match[1]), STYLES["CaptionES"])])
        elif line.startswith("|"):
            rows = []
            while i < len(lines) and lines[i].strip().startswith("|"):
                cells = [c.strip() for c in lines[i].strip().strip("|").split("|")]
                if not all(re.fullmatch(r":?-+:?", c) for c in cells):
                    rows.append(cells)
                i += 1
            count = len(rows[0])
            proportions = [0.30, 0.70] if count == 2 else [0.23, 0.47, 0.30]
            if rows[0][0] == "Clase":
                proportions = [0.32, 0.13, 0.55]
            data = [[Paragraph(inline(c), STYLES["CellES"]) for c in row] for row in rows]
            table = Table(data, colWidths=[available * p for p in proportions],
                          repeatRows=1, hAlign="LEFT")
            table.setStyle(TableStyle([
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#dbeeea")),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1),
                 [colors.HexColor("#f3f6f7"), colors.white]),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LEFTPADDING", (0, 0), (-1, -1), 8),
                ("RIGHTPADDING", (0, 0), (-1, -1), 8),
                ("TOPPADDING", (0, 0), (-1, -1), 7),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 7),
                ("LINEBELOW", (0, 0), (-1, 0), 0.5, TEAL),
            ]))
            story.extend([table, Spacer(1, 12)])
            continue
        else:
            story.append(Paragraph(inline(line), STYLES["BodyES"]))
        i += 1
    return story


def build(name):
    """Escribe un PDF, comprueba su texto y renderiza todas sus páginas a PNG."""
    source = ROOT / "docs" / f"{name}.md"
    target = OUT / f"{name}.pdf"
    doc = SimpleDocTemplate(str(target), pagesize=A4, rightMargin=48,
                            leftMargin=48, topMargin=55, bottomMargin=49,
                            title=source.read_text(encoding="utf-8").splitlines()[0][2:],
                            author="Juan David Paz Rodriguez; Andrea Fernanda Sapuyez",
                            subject="Documentación del proyecto académico")
    doc.build(markdown_flowables(source), canvasmaker=ManualCanvas)
    with pymupdf.open(target) as pdf:
        for number, page in enumerate(pdf, 1):
            if not page.get_text().strip():
                raise ValueError(f"Página sin texto: {name}, {number}")
            page.get_pixmap(matrix=pymupdf.Matrix(1.35, 1.35)).save(
                RENDER / f"{name}-{number:02}.png")
        print(f"{target.name}: {len(pdf)} páginas; renderizado completo")


def main():
    """Genera los dos entregables; la inspección visual posterior es obligatoria."""
    OUT.mkdir(parents=True, exist_ok=True)
    RENDER.mkdir(parents=True, exist_ok=True)
    for name in ("manual-usuario", "manual-tecnico"):
        build(name)


if __name__ == "__main__":
    main()
