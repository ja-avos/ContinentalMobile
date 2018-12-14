package mundo;

import com.apo2h.fave.continentalmobile.InterfazContinental;

public class ThreadEsperarJugada extends Thread {

    /**
     * Referencia al juego
     */
    private JugadorContinental jugador;

    /**
     * Referencia a la ventana principal de la aplicación
     */
    private InterfazContinental principal;

    // -----------------------------------------------------------------
    // Constructores
    // -----------------------------------------------------------------

    /**
     * Construye el nuevo hilo y lo deja listo para esperar la jugada.
     * @param pJuego Referencia al juego. juego != null.
     * @param pInterfaz Referencia a la ventana principal de la aplicación. interfaz != null.
     */
    public ThreadEsperarJugada( JugadorContinental pJuego, InterfazContinental pInterfaz )
    {
        super( );
        jugador = pJuego;
        principal = pInterfaz;
    }

    // -----------------------------------------------------------------
    // Métodos
    // -----------------------------------------------------------------

    /**
     * Inicia la ejecución del hilo que espera la jugada del oponente. <br>
     * Cuando se tiene información sobre la jugada del oponente entonces se actualiza la interfaz.<br>
     * Si el juego debe terminar entonces muestra quien fue el ganador y termina el encuentro y la conexión al servidor.
     */
    public void run( )
    {
        try
        {
            jugador.esperarJugada( );
            if( jugador.juegoTerminado( ) )
            {
                principal.mostrarInformacionGanador( jugador.darVictoriaValidaOponente( ) );
               principal.actualizarInterfaz( );
            }
            else
            {
               // principal.desactivarBotonJugar( );
               principal.actualizarInterfaz( );
            }
        }
        catch( ContinentalException e )
        {
            principal.mostrarError( e );
           // principal.desactivarBarajas( );
        }

    }

}
