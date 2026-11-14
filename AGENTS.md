# Instrucciones de trabajo — MazeQuest

## Prioridades

1. Respetar los requisitos del solicitante y contrastar el PDF original cuando esté disponible.
2. No implementar el juego hasta crear el repositorio privado MazeQuest y comprobar el primer commit remoto.
3. Java 21, Maven, JUnit 5, Swing y Java2D; preparar VS Code con configuración portable.
4. Conservar cambios existentes y evitar operaciones destructivas de Git.

## Diseño y reglas académicas

- Paquetes: structures, model, algorithms, engine, controller y view.
- Las estructuras y el motor no dependen de Swing; BFS no depende de la vista.
- Implementar pila, cola, lista circular, lista doble y BST mediante nodos y referencias propios.
- Prohibido reemplazarlas por Stack, Queue, ArrayDeque, LinkedList, ArrayList, HashSet, HashMap, TreeSet, TreeMap o equivalentes.
- BFS usa cola propia, BST de visitados y matriz de predecesores. Documentar el costo real del BST no balanceado.
- Deshacer restaura el turno completo. Las reglas de colisión, reaparición, finalización e historial deben quedar explícitas y probadas.
- No inventar capturas, métricas, resultados ni revisiones externas.

## Documentación y calidad

- Documentar en español todas las clases, constructores y métodos mediante Javadoc, con parámetros, retornos y excepciones pertinentes.
- Explicar invariantes, cambios de enlaces, casos límite y decisiones importantes dentro del código.
- Añadir pruebas verificables de estructuras, BFS y motor, sin abrir ventanas.
- Usar el EDT y Swing Timer; no bloquear la interfaz con cálculos costosos.
- Mantener trazabilidad requisito → implementación → prueba y evidencia de cada fase.
- Ejecutar las pruebas pertinentes tras cambios, y validación limpia antes de entregar.
- No declarar una fase completa si falta documentación, falla la compilación o fallan pruebas esenciales.

## Colaboración y versiones

- Codex integra. OpenCode realiza auditorías acotadas preferentemente de solo lectura, incluida la documentación del código.
- Comprobar la ayuda instalada de OpenCode; no conceder permisos ilimitados ni modificar simultáneamente los mismos archivos.
- Guardar informes reales en docs/reviews/ y registrar indisponibilidad si ocurre.
- Commits pequeños y descriptivos, sincronizados con GitHub. No incorporar compilados, herramientas descargadas ni secretos.
- No alterar identidad de Git ni atribuir cambios a otras personas.

## Entrega

README actualizado, manuales de usuario y técnico en PDF con capturas reales, JAR ejecutable, guion de ocho minutos y preguntas técnicas. Indicar plataformas realmente verificadas y pendientes exactos.
