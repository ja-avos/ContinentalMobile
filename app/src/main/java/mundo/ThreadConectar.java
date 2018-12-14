package mundo;

import com.apo2h.fave.continentalmobile.InterfazContinental;

public class ThreadConectar extends Thread {



    /**
     * Referencia al juego.
     */
    private JugadorContinental jugador;

    /**
     * Referencia a la ventana principal de la aplicación.
     */


    /**
     * Nombre que utilizará el jugador.
     */
    private String nombre;

    /**
     * Dirección para localizar al servidor.
     */
    private String servidor;

    /**
     * Referencia a la ventana principal de la aplicación.
     */
    private InterfazContinental principal;


    /**
     * Puerto a través del cual se realizará la conexión con el servidor.
     */
    private int puerto;

    // -----------------------------------------------------------------
    // Constructores
    // -----------------------------------------------------------------

    /**
     * Construye el nuevo hilo para conectarse al servidor.
     * @param pJuego Referencia al juego. juego != null.
     * @param pNombreJugador Nombre que utilizará el jugador. nombreJugador != null.
     * @param pDireccionServidor Dirección para localizar al servidor. direccionServidor != null.
     * @param pPuertoServidor Puerto a través del cual se realizará la conexión con el servidor. puertoServidor != null.
     */
    public ThreadConectar( JugadorContinental pJuego, InterfazContinental principal,  String pNombreJugador, String pDireccionServidor, int pPuertoServidor )
    {
        jugador = pJuego;
        this.principal = principal;
        nombre = pNombreJugador;
        servidor = pDireccionServidor;
        puerto = pPuertoServidor;
    }

    // -----------------------------------------------------------------
    // Métodos
    // -----------------------------------------------------------------

    /**
     * Inicia la ejecución del hilo que realiza la conexión con el servidor.<br>
     */
    public void run( )
    {

        try
        {
            jugador.conectar( nombre, servidor, puerto );
            principal.actualizarInterfaz( );
          //  principal.actualizarDatosOponente( );
           // principal.activarBarajas( );
            if( jugador.darEstado( ).equals( JugadorContinental.ESPERANDO_JUGADA ) )
                principal.esperarJugada( );
           // else
               // principal.desactivarBotonJugar( );
        }
        catch( ContinentalException e )
        {
            e.printStackTrace();
            principal.mostrarError( e );
        }
    }
}
