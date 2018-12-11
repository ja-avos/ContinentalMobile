package mundo;

import com.apo2h.fave.continentalmobile.InterfazContinental;

public class ThreadEnviarDatosVictoria implements Runnable {

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
     * Construye un nuevo hilo para validar una victoria.
     * @param pJuego Referencia al juego. juego != null.
     */
    public ThreadEnviarDatosVictoria( JugadorContinental pJuego)
    {
        super( );
        jugador = pJuego;
    }

    // -----------------------------------------------------------------
    // Métodos
    // -----------------------------------------------------------------

    /**
     * Inicia la ejecución del hilo que envía los datos del juego al servidor para validar la victoria.<br>
     */
    public void run( )
    {
        try
        {
            if( jugador.enviarDatosVictoria( ) )
            {
                principal.victoria( true );
            }
            else
            {
                principal.victoria( false );
            }
            //principal.actualizarInterfaz( );
        }
        catch( ContinentalException e )
        {
            e.printStackTrace();
            principal.mostrarError( e );
        }
    }

}
