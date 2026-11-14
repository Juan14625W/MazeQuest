package mazequest.engine;
/** Estados terminales explícitos para controlar entradas. */
public enum GameStatus {
    /** Permite turnos. */ PLAYING,
    /** El jugador alcanzó la salida. */ WON,
    /** Se agotaron las vidas. */ LOST
}