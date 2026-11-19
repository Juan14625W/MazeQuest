# Auditoría integral de MazeQuest contra el enunciado original

Fecha: 15 de noviembre de 2026. Revisor integrador: Codex. Rama auditada: `codex/documentacion-entrega`, commit `71c9b3444cb6b8876aa7e2f1409838137d447157`.

## Dictamen

**Todavía no se cumplen todos los requisitos de entrega.** La implementación principal está respaldada por compilación limpia, pruebas y comprobaciones adicionales. Los faltantes confirmados se concentran en documentación, evidencia de contribuciones y preparación de entrega. No se detectó un defecto funcional crítico del motor en las verificaciones realizadas. Esto no es garantía de ausencia de errores ni una calificación académica.

Se auditó sin modificar código, reglas, manuales, identidades ni historial de Git. Se regeneró `target` para validar, se usaron herramientas temporales ignoradas por Git y se agregaron únicamente informes de revisión. No se hicieron commits, push ni merge durante esta auditoría.

## Fuente y alcance

- Documento: **Proyecto final – Estructuras de Datos.pdf**, proporcionado por el usuario desde Downloads. Cuatro páginas, leídas completas y renderizadas para comprobar tablas y rúbrica.
- SHA-256 del original: `78140ee7e7d79fa5fe9733a07d7adfe5199cd4c7d8da0e99eceaf0d8b9b76f22`.
- Referencia complementaria: `AGENTS.md`. Java 21, tres niveles, motor independiente de Swing y otros compromisos registrados exceden el mínimo del PDF, cuyo lenguaje es libre e interfaz gráfica opcional.
- Integrantes confirmados por el usuario: **Juan David Paz Rodriguez** y **Andrea Fernanda Sapuyez**. Dos integrantes satisface el tamaño de grupo del enunciado. No se ha cambiado ninguna autoría de Git.
- Inspección de las 19 unidades Java de producción, las cuatro clases JUnit, POM, scripts, configuración VS Code/CI, documentación Markdown, ambos PDF y estado remoto de repositorio/PR.
- Auditoría adicional acotada de OpenCode: [transcripción real](05-opencode-rubrica.md). Sesión `ses_e1f56ee04ffe6MBBj5ngQW4CK9`, agente plan, solo lectura, sin autoaprobación. El auditor recibió un resumen de requisitos ya extraídos del PDF por Codex; no se le atribuye la lectura original ni las pruebas ejecutadas por Codex.

## Hallazgos prioritarios

### A1. Falta el diagrama de clases obligatorio

**Prioridad alta de entrega.** El PDF lo pide en la página 2. El manual técnico contiene tabla de paquetes y explicación de dependencias, pero no diagrama de clases. Se comprobó tanto la fuente `docs/manual-tecnico.md:16` como las cinco páginas del PDF; su única imagen es una captura del laboratorio.

Corrección propuesta: incorporar al manual un diagrama con clases principales, asociaciones/composición y tipos de estructuras, consistente con el código. Una tabla de paquetes no sustituye ese entregable. Afecta la completitud del criterio de manuales (10 %), sin implicar una nota automática.

### A2. No se acreditan commits de ambos integrantes

**Prioridad alta de entrega; evidencia incompleta.** El PDF, páginas 1-2, exige grupo de dos o tres y commits de todos. El grupo de dos ya está confirmado. `git shortlog -sn HEAD` muestra ocho commits con una sola identidad: `Juan David`. No aparecen contribuciones identificadas de Andrea en los ancestros de la versión auditada.

Esto no permite concluir que Andrea no trabajó: sí impide demostrar mediante el historial el requisito de commits de todos. Revisar si existen contribuciones reales en otra rama/repositorio o realizar trabajo real identificado correctamente. **No reescribir ni fabricar autorías, fechas o contribuciones para aparentar cumplimiento.** Los nombres confirmados también faltan en `README.md:78`.

### A3. Pruebas del manual técnico: falta esperado frente a obtenido

**Prioridad media de entrega.** Página 3 del enunciado: explicar qué se probó y contrastar resultado esperado con obtenido. `docs/manual-tecnico.md:84` resume clases, número de pruebas y temas, y sí ofrece resultados concretos de los tres niveles; no documenta esa comparación para los casos principales de estructuras, BFS y motor.

Corrección propuesta: tabla de casos verificables, entrada/acción, esperado, obtenido y método de prueba. Ejemplos con evidencia ya disponible: FIFO 1,2,3; BFS del mapa con obstáculo, seis pasos; daño de varios enemigos, una sola vida; deshacer derrota; cierre de circular; eliminación BST con dos hijos. No inventar casos ni métricas.

### A4. Error reproducido en el guion de demostración

**Prioridad media.** `docs/sustentacion.md:19` indica que la sexta entrada a la derecha produce el choque en El umbral. La secuencia ejecutada mediante las acciones de GamePanel sobre el EDT, sin ventana y con animaciones desactivadas, dio:

| Turno | Posición del jugador | Vidas |
|---|---|---|
| 1 | (1,2) | 3 |
| 2 | (1,3) | 3 |
| 3 | (1,4) | 3 |
| 4 | (1,5) | 3 |
| 5 | (1,1), reaparición por daño | 2 |
| 6 | (1,2) | 2 |
| Z después del sexto | (1,1), turno 5 | 2 |
| Otro Z | (1,5), turno 4 | 3 |

El choque es el **quinto** paso. La primera comprobación temporal falló al esperar recuperar una vida deshaciendo el sexto; se investigó y confirmó que el motor restaura correctamente, mientras el guion tiene el turno incorrecto. Corrección: cinco pasos a la derecha y luego Z. No se modificó el guion en esta auditoría.

### A5. Contratos Javadoc incompletos para excepciones

**Prioridad media respecto de AGENTS.md.** El análisis sintáctico de 228 declaraciones explícitas de clases y métodos de producción/pruebas encontró cero declaraciones sin bloque Javadoc. Sin embargo, tener bloque y pasar doclint no comprueba exhaustivamente excepciones no comprobadas en ejecución.

Ejemplos concretos que requieren completar `@throws` y límites:

- `GameEngine.java:23`: constructor llama `Objects.requireNonNull`, pero no documenta `NullPointerException`.
- `GameEngine.java:173`: `historyAt` propaga `IndexOutOfBoundsException` de la lista.
- `GameEngine.java:224`, `:234`, `:244`: accesos por índice de enemigo, sin contrato de índice/excepción completo.
- `Maze.java:146`: `route(index)` accede al arreglo sin documentar su límite/excepción.
- `Enemy.java:48`: `restore(index)` propaga la validación del cursor.
- `GameController.java:35`: `start(level)` propaga el rechazo de niveles fuera de 0..2.
- `CircularLinkedList.Cursor`: sus métodos públicos llaman `check()` y pueden lanzar `ConcurrentModificationException`; la excepción está descrita en el auxiliar privado, no en todos los contratos públicos correspondientes.

Son omisiones de documentación. No equivalen por sí solas a cierres ante entradas normales de la interfaz.

### A6. La rama principal todavía no contiene los entregables nuevos

**Prioridad de integración.** El repositorio remoto es privado. El [PR #1](https://github.com/Juan14625W/MazeQuest/pull/1) sigue abierto y en borrador, con cabeza `71c9b34` y base `main` en `2f9d04b`. Los manuales y el README actualizado están subidos en la rama de trabajo, pero no integrados en main.

Si se entrega solamente el enlace a la rama predeterminada en este estado, se verá el README inicial y no los manuales nuevos. Preparar la revisión completa y acordar qué rama/commit se entrega. **Fusionar a main es una acción crítica que debe consultarse antes de ejecutarla**, conforme a la indicación del usuario. No se hizo durante la auditoría.

### A7. Documentación ahora desactualizada frente al PDF recibido

**Prioridad media/baja.** README, trazabilidad y manual técnico todavía indican que el PDF original no está disponible; eso era cierto en su redacción, pero ya no describe el estado actual. También persiste la frase futura «se implementarán» en `README.md:15` para estructuras existentes.

Actualizar fuentes, trazabilidad y pendientes después de incorporar las correcciones. La fecha sí coincide: 20 de noviembre de 2026, hasta las 18:00. Añadir al ensayo la fase de aproximadamente 12 minutos de preguntas y un cambio pequeño en vivo: el enunciado exige que cualquiera pueda explicar cualquier parte; la sustentación individual pesa 20 % y no se puede validar con tests.

## Mejoras recomendables, distintas de incumplimientos confirmados del PDF

- **Selección de JDK:** `scripts/maven.cmd:5` y `scripts/dev.cmd:5` recorren todos los subdirectorios de `.tools/jdk` y sobrescriben JAVA_HOME con el último. Si coexisten versiones, no garantizan elegir 21 ni respetar un JAVA_HOME ya configurado. El POM sí fija release 21, y el build auditado pasó usando JDK 21 explícitamente. Preferir JAVA_HOME válido o una selección explícita de JDK 21. No es un fallo observado al jugar.
- **Complejidad y justificación:** la tabla por estructura existe; no se considera ausente. Para reforzarla, hacer explícitos insertar/eliminar/buscar/recorrer de cada lista y por qué el BST de visitados satisface el uso alternativo permitido en la página 1 del enunciado. El peor costo del BST/BFS está correctamente reconocido.
- **Pruebas permanentes de controlador/vista:** la suite de 21 cubre estructuras, algoritmo y motor. Las comprobaciones de interacción de esta auditoría son temporales, sin ventanas; no cubren visualmente toda la aplicación ni aseguran todos los intercalados de SwingWorker.
- **Sesiones extensas:** `GamePanel.technical` vuelve a serializar pila/historial y recorrer todas las líneas en cada repintado. El trabajo crece con turnos sin límite aunque se muestren pocas filas. Es un riesgo de rendimiento deducido del código, no un bloqueo medido ni un fallo en los mapas probados. Conviene cachear presentación por revisión o dibujar solo filas visibles si se amplía la duración de las sesiones.
- **Captura BST antigua:** está identificada honestamente como anterior al arreglo del salto de claves. Sustituirla por una captura real vigente mejoraría la entrega, sin retocar números ni simular estados.
- **Verificación de plataforma/IDE:** falta inspección gráfica Linux y confirmación de breakpoint. El PDF no obliga a Linux ni a VS Code; son compromisos adicionales del proyecto. No confundirlos con condiciones académicas mínimas.

## Matriz de requisitos del enunciado

| Requisito | Dictamen | Evidencia o pendiente |
|---|---|---|
| Grupo de 2 o 3 | Confirmado por el usuario | Juan David Paz Rodriguez y Andrea Fernanda Sapuyez |
| Laberinto funcional | Respaldado en alcance probado | Tres mapas, reglas explícitas, victoria y derrota; 21 JUnit exitosos |
| Pila propia y cinco operaciones | Cumple en revisión/pruebas | CustomStack: push/pop/peek/isEmpty/size; instantáneas reales |
| Cola propia y cinco operaciones | Cumple en revisión/pruebas | CustomQueue: enqueue/dequeue/peek/isEmpty/size; BFS real |
| Circular: insertar inicio/final/posición, eliminar, buscar, recorrer | Cumple en revisión/pruebas | CircularLinkedList y cursores de Enemy |
| Doble: operaciones mínimas y ambos recorridos | Cumple en revisión/pruebas | DoublyLinkedList; historial de MoveRecord |
| Árbol: insertar/buscar/eliminar y tres recorridos | Cumple en revisión/pruebas | BinarySearchTree; visitados reales y laboratorio |
| Sin colecciones estándar sustitutas | Cumple en código inspeccionado | Nodos propios; arreglos auxiliares no reemplazan las cinco estructuras |
| Uso real de las cinco | Cumple en código inspeccionado | Pila undo, cola BFS, circular patrullas, doble historial, BST visitados |
| Menú y entradas inválidas | Respaldado en alcance probado | Menú, filtros de movimiento, sin entradas textuales libres; controles básicos comprobados sin ventanas |
| Ganar/perder y reglas | Cumple en casos probados | WON/LOST, colisiones previas/posteriores, undo, reinicio y daño único |
| Interfaz | Presente | Swing/Java2D; valoración estética final corresponde a la docente |
| Código comentado | Mayormente completo | 228 declaraciones con Javadoc; completar excepciones pertinentes |
| README: juego, integrantes, lenguaje y ejecución | Parcial | Faltan nombres confirmados; instrucciones de ejecución presentes |
| Repositorio y commits de todos | Parcial | Privado y sincronizado; solo una identidad en HEAD |
| Manual de usuario PDF | Contenido mínimo presente | Instalación, controles, reglas, capturas, victoria/derrota; cuatro páginas revisadas |
| Manual técnico PDF | Incompleto | Falta diagrama y comparación esperado/obtenido; tabla de estructuras y organización presentes |
| Sustentación | Preparación parcial; evaluación no verificable | Guion y 18 preguntas; error en secuencia, falta ensayo individual y cambio en vivo |
| Fecha límite | Confirmada | 20/11/2026, 18:00, según PDF |

## Verificaciones ejecutadas

1. `mvn -B -ntp clean verify javadoc:javadoc`, JDK 21.0.13 y Maven 3.9.11, Windows, 15/11/2026 14:18 America/Bogota. **BUILD SUCCESS: 21 pruebas; 0 fallos, 0 errores, 0 omitidas.** JAR regenerado y Javadoc correcto. Informes en `target/surefire-reports/`.
2. Confirmación remota de [CI 43737300229](https://github.com/Juan14625W/MazeQuest/actions/runs/43737300229), asociado al commit auditado: terminado con éxito; matriz Ubuntu/Windows y Java 21/25 ya revisada. No implica validación gráfica.
3. Diagnóstico temporal `.tools/audit/ReviewChecks.java`: 10.000 operaciones de BST y 5.000 de listas con semilla fija 20261115; comparación contra referencias auxiliares, tamaños, orden, enlaces adelante/atrás y cierre circular. Pasó. No es parte de las 21 pruebas JUnit ni una prueba exhaustiva del espacio de estados.
4. Acciones del GamePanel en EDT y pintura en BufferedImage, sin abrir ventanas: menú, ayuda, configuración, selección, movimiento, pausa, laboratorio, cancelación y confirmación de reinicio, daño y undo. Pasó después de corregir la expectativa del guion en el diagnóstico, sin cambiar el juego. Los controles se invocaron programáticamente; no se presenta como ensayo humano ni inspección de ventana nativa.
5. Análisis sintáctico mediante API Javac/DocTrees sobre src: 228 declaraciones explícitas, cero sin bloque Javadoc. No certifica contenido semántico de todos los comentarios ni excepciones no comprobadas.
6. Extracción y revisión visual del PDF original (4 páginas), manual técnico (5) y manual de usuario (4). No se regeneraron los entregables.
7. Revisión de archivos versionados: no se encontraron JAR, class, ZIP, ejecutables descargados, claves PEM/KEY o archivos .env versionados. Esto no sustituye un escaneo completo de secretos incrustados en cualquier texto.

Limitaciones del entorno: las bibliotecas PDF y clases generadas no eran legibles desde el aislamiento; las comprobaciones se repitieron con autorización fuera de él. Esos fallos de acceso no se clasificaron como defectos del proyecto.

## Orden recomendado para cerrar la entrega

1. Agregar los nombres confirmados al README y resolver evidencia real de contribuciones de ambos integrantes.
2. Incorporar diagrama al manual técnico y tabla de pruebas con esperado/obtenido, obtenidos de la suite real; completar contratos Javadoc.
3. Corregir el quinto paso del guion, actualizar referencia al PDF y reforzar justificación/tablas de estructuras.
4. Renderizar/revisar otra vez los manuales y ejecutar validación pertinente tras las correcciones; ensayar los ocho minutos, preguntas y cambio pequeño.
5. Revisar el PR final y **pedir autorización antes de fusionar a main**, cambiar permisos del repositorio o publicar una entrega. No alterar reglas del juego o autoría sin una decisión explícita.

No se recomienda reescribir el motor ni reemplazar estructuras: no hay evidencia que justifique una intervención de ese alcance.
