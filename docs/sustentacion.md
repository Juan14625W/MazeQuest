# Sustentación de ocho minutos

Guion para ensayar; no es una exposición ya realizada. Preparar JAR, proyecto en VS Code e informes de prueba. Evitar descargas durante la presentación.

Equipo: Juan David Paz Rodriguez y Andrea Fernanda Sapuyez. Distribuir la demostración entre ambos, pero ensayar también intercambiando papeles: cada integrante debe poder explicar cualquier parte del código.

| Tiempo | Demostración y explicación |
|---|---|
| 0:00-0:45 | Presentar objetivo, tres niveles y tres vidas. Abrir El umbral. |
| 0:45-1:40 | Moverse e intentar un muro: no consume turno. Cada paso válido avanza patrullas. |
| 1:40-2:35 | Provocar daño y usar Z. Mostrar restauración de vidas, turno, jugador y guardianes. |
| 2:35-3:35 | Abrir T: pila de instantáneas y circular. Explicar top, next y cursor. |
| 3:35-4:25 | Historial en ambas direcciones; next/prev. Distinguir consulta de deshacer. |
| 4:25-5:35 | Ejecutar BFS: ruta, cola y BST. FIFO, marcado al encolar y predecesores. La pista ignora enemigos. |
| 5:35-6:25 | Recorridos BST y costo O(h), peor O(n). BFS puede ser cuadrático. |
| 6:25-7:20 | Paquetes, pruebas y jugabilidad de los tres niveles. EDT, Timer y SwingWorker. |
| 7:20-8:00 | Selección de niveles, manuales, plataformas verificadas y límites declarados. |

## Ensayo de daño y deshacer

Desde El umbral recién iniciado, avanzar **cinco veces a la derecha**, dejando terminar cada animación. Los cuatro primeros pasos conservan tres vidas; el quinto causa daño, deja dos vidas y devuelve al jugador a la entrada. Pulsar Z inmediatamente: vuelven tres vidas, turno cuatro y las posiciones anteriores del jugador y del guardián. La prueba `GameEngineTest.presentationCollisionOccursOnFifthStep` reproduce y verifica esa secuencia. El ensayo humano completo sigue pendiente.

## Preguntas técnicas

1. **¿Por qué pila?** El último turno válido es el primero que se restaura: LIFO. Se guarda antes de mutar.
2. **¿Qué se restaura?** Jugador, vidas, turno, estado y fases enemigas; se retira el último registro. Reloj y pistas son datos de sesión.
3. **¿Deshacer es O(1)?** pop sí; restaurar no necesariamente. seek recorre cada ruta enemiga.
4. **¿Por qué cola en BFS?** FIFO procesa distancias por capas y encuentra el mínimo número de pasos.
5. **¿Cuándo se marca visitado?** Al encolar; evita que varios vecinos agreguen la misma celda.
6. **¿BFS es O(V+E)?** No aquí: el BST cuesta O(h), y capturar máximos de cola agrega trabajo. El peor tiempo es cuadrático en el área del tablero.
7. **¿Para qué predecesores?** Reconstruir la ruta sin almacenar un camino completo por cada entrada de cola.
8. **¿La pista evita guardianes?** No. El verificador de niveles sí considera fase enemiga, exclusivamente en pruebas.
9. **¿Cómo evitar recorrer infinitamente la circular?** Visitar exactamente size nodos. El cursor avanza un enlace por turno.
10. **¿Cómo eliminar de la doble?** Reconectar vecinos en ambas direcciones y reparar extremos.
11. **¿Eliminar del BST con dos hijos?** Sustituir por sucesor inorden y retirar el nodo que aportó la clave.
12. **¿Varios choques?** hit es booleano: se descuenta una sola vida y se reinician patrullas.
13. **¿Intercambiar posiciones causa daño?** Sí: el destino estaba ocupado antes del avance enemigo. Existe prueba específica.
14. **¿Qué impide una pista obsoleta?** revision cambia con el mundo y done descarta resultados antiguos.
15. **¿Por qué SwingWorker y Timer?** Cálculo fuera del EDT, publicación en él y repintados sin bloquear. El reloj no mueve enemigos.
16. **¿Qué demuestra jugabilidad?** Secuencias con fase reproducidas contra GameEngine ganan los tres niveles sin perder vidas.
17. **¿Colecciones estándar?** No sustituyen las estructuras. Los arreglos son auxiliares para mapas, predecesores e instantáneas.
18. **¿Qué falta verificar?** Contribuciones reales de ambos en Git, ensayo de la defensa, GUI de Linux y breakpoint. El PDF académico ya se contrastó y el equipo está confirmado. CI sin ventanas no reemplaza revisión gráfica.

## Defensa individual y cambio en vivo (doce minutos)

La profesora reserva doce minutos para preguntas y exige que cualquiera explique estructuras, reglas y código, además de realizar una modificación pequeña. Practicar ambos los 18 puntos anteriores, sin limitar a cada persona a «su parte».

Ensayo sugerido: cuatro minutos para dibujar enlaces y eliminar un nodo del BST; cuatro para seguir un turno completo y su deshacer; cuatro para una modificación pequeña con compilación y prueba. Por ejemplo, cambiar una etiqueta de la ayuda y verificarla, o añadir un caso límite de una estructura. Este reparto es una propuesta de práctica, no una agenda impuesta por la profesora.

Antes de presentar: confirmar acceso privado de la docente, abrir ambos PDF y el JAR, comprobar el proyector y ensayar con cronómetro. Andrea debe hacer y registrar su contribución real con su propia identidad; no se simulan commits ni se reescribe la autoría existente.

## Entrega

Distribuir target/mazequest.jar y ambos manuales de output/pdf. Conservar código y evidencia en el repositorio privado. El equipo y la rúbrica original ya están incorporados; verificar los pendientes de trazabilidad.md antes de entregar.
