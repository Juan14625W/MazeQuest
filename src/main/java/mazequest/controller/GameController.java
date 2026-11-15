package mazequest.controller;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import mazequest.algorithms.*;
import mazequest.engine.GameEngine;
import mazequest.model.*;

/** Coordina acciones y búsqueda en segundo plano; el motor permanece independiente de Swing. */
public final class GameController {
  private GameEngine game;
  private SearchResult search;
  private final Runnable changed;
  private long revision;
  private SwingWorker<SearchResult, Void> worker;
  private boolean searching;
  private String message = "";
  private int hints;

  /**
   * Recibe notificación para repintar en el EDT.
   *
   * @param changed callback no nulo de vista; las acciones públicas se invocan en el EDT
   */
  public GameController(Runnable changed) {
    this.changed = changed;
  }

  /**
   * Abre nivel limpio.
   *
   * @param level índice de nivel entre 0 (incluido) y Levels.count() (excluido)
   * @throws IllegalArgumentException si el nivel no existe
   */
  public void start(int level) {
    invalidate();
    game = new GameEngine(Levels.get(level));
    hints = 0;
    changed.run();
  }

  /**
   * Ejecuta un turno si es válido.
   *
   * @param dr fila
   * @param dc columna
   * @return si cambió
   */
  public boolean move(int dr, int dc) {
    if (game == null || !game.move(dr, dc)) return false;
    invalidate();
    changed.run();
    return true;
  }

  /**
   * Deshace y descarta una pista que ya no corresponde.
   *
   * @return si cambió
   */
  public boolean undo() {
    if (game == null || !game.undo()) return false;
    invalidate();
    changed.run();
    return true;
  }

  /** Reinicia lógica y ayudas de sesión. */
  public void restart() {
    if (game != null) {
      invalidate();
      game.restart();
      hints = 0;
      changed.run();
    }
  }

  /** Invalida resultados y cancela trabajos antiguos al cambiar el mundo. */
  private void invalidate() {
    revision++;
    search = null;
    message = "";
    searching = false;
    if (worker != null) worker.cancel(true);
  }

  /** Calcula BFS sin bloquear el EDT; solo publica resultados de la revisión vigente. */
  public void hint() {
    if (game == null || searching) return;
    final long request = revision;
    final Maze maze = game.maze();
    final Position start = game.player();
    searching = true;
    message = "Explorando…";
    changed.run();
    worker =
        new SwingWorker<>() {
          /**
           * Ejecuta el algoritmo puro fuera del EDT.
           *
           * @return resultado
           */
          @Override
          protected SearchResult doInBackground() {
            return BreadthFirstSearch.find(maze, start, maze.exit());
          }

          /** Publica solo si el nivel/turno siguen vigentes y trata errores sin cerrar el juego. */
          @Override
          protected void done() {
            if (request != revision) return;
            searching = false;
            try {
              search = get();
              hints++;
              message =
                  search.found()
                      ? search.distance() + " pasos hasta la salida"
                      : "No hay ruta estática";
            } catch (CancellationException e) {
              message = "Búsqueda cancelada";
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              message = "Búsqueda interrumpida";
            } catch (ExecutionException e) {
              message = "No se pudo calcular la pista";
            }
            changed.run();
          }
        };
    worker.execute();
  }

  /**
   * Modelo actual.
   *
   * @return partida o null antes de jugar
   */
  public GameEngine game() {
    return game;
  }

  /**
   * Resultado vigente.
   *
   * @return búsqueda o null
   */
  public SearchResult search() {
    return search;
  }

  /**
   * Estado asíncrono.
   *
   * @return si calcula
   */
  public boolean searching() {
    return searching;
  }

  /**
   * Feedback breve.
   *
   * @return mensaje
   */
  public String message() {
    return message;
  }

  /**
   * Ayudas de la sesión, no reversibles.
   *
   * @return consultas completadas
   */
  public int hints() {
    return hints;
  }
}
