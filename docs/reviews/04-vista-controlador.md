# Auditoría de vista y controlador

Fecha: 15 de noviembre de 2026. OpenCode, agente `plan`, sesión `ses_e1ff34b10ffe5OJ9NxivboHSGf`. Se consultó la ayuda instalada antes de ejecutar. Sin `--auto`, sin permisos ilimitados. Alcance: lectura de GameController.java y GamePanel.java; no se solicitaron cambios, comandos, red ni delegación.

Resumen fiel de la respuesta real, elaborado por Codex; no es una transcripción íntegra. OpenCode declaró: «No se ejecutaron pruebas ni comandos». La salida observada contiene lecturas de ambos archivos y un informe final.

## Hallazgos informados

1. **Bajo, Javadoc de Screen.** OpenCode afirmó que el enum interno carecía de Javadoc. Codex lo descarta: inmediatamente antes de la declaración ya existe `/** Pantallas que comparten el mismo renderer. */`. La propia lectura del auditor lo incluía. No se elimina ni modifica documentación correcta por ese informe.
2. **Bajo, coherencia de foco ratón/teclado.** OpenCode observó que Tab cambia `focus`, Enter activa `buttons[focus]`, y pulsar con ratón no actualiza ese índice. Codex acepta el caso de activación: `mouseReleased` ahora asigna `focus = i` antes de ejecutar el botón pulsado. Una acción que cambia de pantalla sigue restableciendo el foco mediante `change`. Hover y foco de teclado conservan funciones distintas; mover el puntero sin pulsar no equivale a enfocar.

## Sin hallazgos concretos del auditor

- Cálculo de BFS en doInBackground y publicación en done sobre el EDT.
- Manejo de cancelación, interrupción y errores de SwingWorker.
- Invalidación por revision para no publicar pistas de otros turnos o niveles.
- Uso de Swing Timer y eventos del EDT.
- Javadoc general de clase, constructor y métodos en ambos archivos, salvo el falso positivo anterior.

## Alcance de la evidencia

Es una auditoría estática acotada, no una validación visual ni una medición de cobertura. La compilación y las pruebas posteriores las ejecuta Codex y se registran por separado en docs/validacion.md. No se atribuyen a OpenCode resultados que no produjo.
