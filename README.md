# MazeQuest: The Lost Labyrinth

Videojuego académico de laberintos para Estructuras de Datos, desarrollado con Java 21, Swing y Java2D. Destinos: Windows y Linux.

## Estado verificable

Versión jugable con tres niveles, interfaz Swing/Java2D y cinco estructuras propias. Repositorio privado con primer commit remoto `896c34b` conservado. Validación limpia del 16 de noviembre de 2026: **27 pruebas, cero fallos, errores y omisiones**, con Java 21.0.13 en Windows; Javadoc correcto.

[CI del código base 2f9d04b](https://github.com/Juan14625W/MazeQuest/actions/runs/43664777093) pasó en Ubuntu/Windows con Java 21/25. La revisión gráfica registrada se realizó en Windows. Evidencias y límites en [validación](docs/validacion.md).

## Alcance

Tres niveles, turnos, enemigos con patrullas circulares, tres vidas, deshacer completo, pistas BFS, historial bidireccional y panel técnico con datos reales.

Las cinco estructuras están implementadas con nodos propios: pila, cola, lista circular, lista doblemente enlazada y árbol binario de búsqueda. No se usan colecciones del lenguaje como sustitutos.

Adaptación académica del 16 de noviembre: nodos `Nodo`, referencias y auxiliares en español, operaciones de pila/cola/BST usadas por el juego y recorridos recursivos del árbol. Se conservaron genéricos sencillos y compatibilidad de API. [Matriz final y ejemplos para sustentar](docs/adaptacion-academica.md); [comparación inicial con las diapositivas](docs/reviews/07-matriz-academica-inicial.md). Los recorridos recursivos no prometen profundidad ilimitada; se probó el caso degenerado de 2.000 nodos.

## Compilar y ejecutar

Instalar JDK 21 y Maven 3.9; configurar `JAVA_HOME` y `PATH`. Comprobar `java -version` y `mvn -version`. La primera compilación necesita conexión a Maven Central. Desde la raíz:

```powershell
.\scripts\maven.cmd -B -ntp clean verify javadoc:javadoc
java -jar target/mazequest.jar
```

En Linux, desde una sesión con escritorio gráfico:

```bash
bash scripts/maven.sh -B -ntp clean verify javadoc:javadoc
java -jar target/mazequest.jar
```

También se puede usar `mvn -B -ntp clean verify javadoc:javadoc` directamente. El JAR se genera en `target/mazequest.jar`; la API, en `target/reports/apidocs/index.html`. No se versionan compilados ni herramientas descargadas. No se necesita Python para jugar.

Los scripts de Windows respetan un `JAVA_HOME` explícito y comprueban que contenga `java` y `javac`. Si falta, buscan un JDK 21 en `.tools/jdk/`; finalmente usan el `PATH`. Un `JAVA_HOME` inválido produce un error claro. No cambian la configuración permanente del equipo.

## Controles y reglas

| Acción | Control |
|---|---|
| Movimiento ortogonal | Flechas o WASD |
| Deshacer turno completo | Z |
| Pista mínima estática | H |
| Laboratorio de estructuras | T |
| Reiniciar con confirmación | R |
| Pausa / volver / cancelar | Esc |
| Navegar y activar botones | Tab y Enter, o ratón |

Un paso válido mueve también a los guardianes. Hay daño si el destino coincide con su celda anterior o nueva, incluido el intercambio. Máximo una vida perdida por turno; jugador y patrullas reaparecen en sus posiciones iniciales. Los muros no consumen turno. La salida da la victoria; cero vidas, la derrota. Z también restaura esos estados terminales.

La pista ignora enemigos móviles. El historial se consulta sin alterar la partida. El reloj mide la sesión, incluida la pausa, y no se rebobina al deshacer; el contador de pistas tampoco. No se guarda progreso al cerrar.

## VS Code

Abrir esta carpeta e instalar las extensiones recomendadas en `.vscode/extensions.json`. Seleccionar JDK 21 en «Java: Configure Java Runtime» si no se detecta. La configuración compartida no contiene rutas personales.

- `Ctrl+Shift+B`: compilación limpia y pruebas.
- Tareas «MazeQuest: pruebas» y «MazeQuest: empaquetar»: pruebas o JAR.
- `F5`: «MazeQuest · jugar / depurar», con clase principal `mazequest.Main`.

Si el IDE genera bytecode de otra versión, seleccionar el mismo JDK y repetir `clean verify`. El reconocimiento del proyecto y el inicio del depurador se observaron; falta confirmar su detención en breakpoint.

## Documentación de entrega

- [Manual de usuario PDF](output/pdf/manual-usuario.pdf) y [fuente editable](docs/manual-usuario.md).
- [Manual técnico PDF](output/pdf/manual-tecnico.pdf) y [fuente editable](docs/manual-tecnico.md).
- [Guion de ocho minutos y preguntas técnicas](docs/sustentacion.md).
- [Arquitectura](docs/arquitectura.md), [trazabilidad](docs/trazabilidad.md) y [auditorías reales](docs/reviews/).

Para regenerar los manuales, instalar `scripts/requirements-docs.txt` en un entorno Python y ejecutar `python scripts/build_manuals.py`. Inspeccionar los renderizados de `output/.render/` antes de entregar.

## Requisitos académicos

Contrastado el 15 de noviembre con las cuatro páginas de «Proyecto final – Estructuras de Datos.pdf», suministrado por el solicitante. La [trazabilidad](docs/trazabilidad.md) distingue requisitos de la profesora y compromisos internos. El manual técnico incorpora diagramas de clases, justificación de estructuras, complejidades y casos con resultado esperado y obtenido.

Pendientes de entrega: contribuciones reales identificables de ambos integrantes en Git, ensayo de ocho minutos y defensa individual de doce minutos. La integración de esta rama en `main` requiere autorización. La GUI de Linux y la detención en un breakpoint de VS Code siguen sin verificarse.

Entrega indicada: 20 de noviembre de 2026, 18:00, America/Bogota.

## Autoría

- Juan David Paz Rodriguez.

Equipo académico confirmado por el solicitante. La identidad de Git se conserva: indicar el equipo no demuestra contribuciones de ambos en el historial. Desarrollo asistido por Codex; las revisiones reales de OpenCode están identificadas en `docs/reviews/`.

## Acceso docente

El repositorio debe permanecer privado. Su propietario puede invitar a la docente desde Settings → Collaborators, utilizando su cuenta de GitHub. No se publicarán credenciales ni archivos temporales.
