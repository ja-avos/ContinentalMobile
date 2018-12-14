package mundo;

import com.apo2h.fave.continentalmobile.InterfazContinental;

public class ThreadEnviarJugada extends Thread {

    /**
     * Referencia al juego.
     */
    private JugadorContinental jugador;

    /**
     * Referencia a la ventana principal de la aplicación.
     */
    private InterfazContinental principal;

    // -----------------------------------------------------------------
    // Constructores
    // -----------------------------------------------------------------

    /**
     * Construye el nuevo hilo y lo deja listo para enviar la jugada.
     * @param pJuego Referencia al juego. juego != null.
     * @param pInterfaz Referencia a la ventana principal de la aplicación. interfaz != null.
     */
    public ThreadEnviarJugada( JugadorContinental pJuego, InterfazContinental pInterfaz )
    {
        super( );

        jugador = pJuego;
        principal = pInterfaz;
    }

    // -----------------------------------------------------------------
    // Métodos
    // -----------------------------------------------------------------

    /**
     * Inicia la ejecución del hilo que realiza el envío del mensaje y espera la respuesta. <br>
     * Cuando se tiene información sobre el resultado de la jugada entonces se actualiza la interfaz.<br>
     * Si el juego debe terminar entonces muestra quien fue el ganador y termina el encuentro y la conexión al servidor.
     */
    public void run( )
    {

        jugador.realizarJugada( );
        principal.actualizarInterfaz( );
        principal.esperarJugada( );

    }

}
