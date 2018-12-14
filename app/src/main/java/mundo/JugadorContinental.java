package mundo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class JugadorContinental {

    //Constante
    public static final String REALIZANDO_JUGADA = "Realizando jugada";
    public static final String ESPERANDO_JUGADA = "Esperando jugada";
    public static final String ESPERANDO_OPONENTE = "Esperando oponente";
    public static final String JUEGO_TERMINADO = "Juego terminado";
    public static final String CARTA = "CARTA";
    public static final String JUGADOR = "JUGADOR:";
    public static final String VICTORIA = "VICTORIA";
    public static final String BARAJA_INICIAL = "BARAJA:INICIAL";
    public static final String BARAJA_JUGADA = "BARAJA:JUGADA";

    //Atributos
    private String numDerrotasOponente;
    private  String numVictoriasOponente;
    private String nombre;
    private Socket canal;
    private PrintWriter outWriter;
    private BufferedReader inReader;
    private boolean finJuego;
    private String estado;
    private boolean seleccionoCarta;
    private String nombreOponente;
    private boolean victoriaValidaOponente;
    private Juego juego;



    //Constructor
    public JugadorContinental( ){
        finJuego = false;
        juego = new Juego();
        estado = ESPERANDO_OPONENTE;
        seleccionoCarta = false;
    }

    /**
     * Devuelve el estado del jugador.
     * @return Estado del jugador.
     */
    public String darEstado( )
    {
        return estado;
    }

    /**
     * Devuelve el juego del jugador. <b>
     * pre: </b> El juego se encuentra inicializado. juego != null. <br>
     * @return Juego del jugador.
     */
    public Juego darJuego( )
    {
        return juego;
    }

    /**
     * Devuelve la carta a descartar por el jugador. <b>
     * pre: </b> El juego se encuentra inicializado. juego != null. <br>
     * @return Carta a descartar.
     */
    public Carta darCartaTemporal( )
    {
        return juego.darCartaTemporal( );
    }

    /**
     * Nombre del oponente.
     * @return Nombre del oponente.
     */
    public String darNombreOponente( )
    {
        return nombreOponente;
    }

    /**
     * Devuelve el numero de derrotas del oponente.
     * @return Numero de derrotas del oponente.
     */
    public String darNumDerrotasOponente( )
    {
        return numDerrotasOponente;
    }

    /**
     * Devuelve el numero de victorias del oponente.
     * @return Numero de victorias del oponente.
     */
    public String darNumVictoriasOponente( )
    {
        return numVictoriasOponente;
    }

    /**
     * Devuelve si la victoria del oponente fue valida.
     * @return True si la victoria del oponente fue valida, false de lo contrario.
     */
    public boolean darVictoriaValidaOponente( )
    {
        return victoriaValidaOponente;
    }

    /**
     * Devuelve si el juego se ha terminado.
     * @return True si el juego ha terminado, false de lo contrario.
     */
    public boolean juegoTerminado( )
    {
        return finJuego;
    }

    /**
     * Indica si el jugador ha tomado una carta de alguna baraja.
     * @return True si el jugador a tomado una carta, false de lo contrario.
     */
    public boolean seleccionoCarta( )
    {
        return seleccionoCarta;
    }

    /**
     * Modifica la carta a descartar por el jugador.
     * @param pCarta Carta a descartar.
     */
    public void modificarCartaTemporal( Carta pCarta )
    {
        juego.modificarCartaTemporal( pCarta );
    }

    /**
     * Conectarse el cliente al servidor del juego.
     * @param pNombreJugador Nombre del jugador. nombreJugador != null.
     * @param pDirServ Dirección del servidor. dirServ != null.
     * @param pPuertoServ Puerto del servidor. puertoServ != null.
     * @throws ContinentalException Si se produce algún error al conectarse al servidor.
     */
    public void conectar( String pNombreJugador, String pDirServ, int pPuertoServ ) throws ContinentalException
    {
        nombre = pNombreJugador;

        try
        {
            // Conectar al servidor
            canal = new Socket( pDirServ, pPuertoServ );
            outWriter = new PrintWriter( canal.getOutputStream( ), true );
            inReader = new BufferedReader( new InputStreamReader( canal.getInputStream( ) ) );

            // iniciar el encuentro
            iniciarEncuentro( );

            // Recibir el juego
            recibirJuego( );
        }
        catch( UnknownHostException e )
        {
            e.printStackTrace();
            estado = JUEGO_TERMINADO;
            finJuego = true;
            verificarInvariante( );
            throw new ContinentalException( "No fue posible establecer una conexión al servidor. " );
        }
        catch( IOException e )
        {
            estado = JUEGO_TERMINADO;
            finJuego = true;
            verificarInvariante( );
            throw new ContinentalException( "No fue posible establecer una conexión al servidor. " );
        }

    }

    /**
     * Envía al servidor los mensajes necesarios para iniciar un encuentro y recibe la información del oponente.
     * <b> pre: </b> Se encuentra establecida una conexión con el servidor. <br>
     * @throws ContinentalException Se lanza esta excepción si hay un problema leyendo del canal.
     */
    private void iniciarEncuentro( ) throws ContinentalException
    {

        try
        {
            finJuego = false;

            // Enviar el nombre del jugador
            outWriter.println( JUGADOR + nombre );

            // Leer la información del jugador
            // INFO:<nombre>:<ganados>:<perdidos>
            String[] datosJugador;
            datosJugador = inReader.readLine( ).split( ":" );
            nombreOponente = datosJugador[ 1 ];
            numVictoriasOponente = datosJugador[ 2 ];
            numDerrotasOponente = datosJugador[ 3 ];

        }
        catch( IOException e )
        {
            e.printStackTrace();
            estado = JUEGO_TERMINADO;
            finJuego = true;
            verificarInvariante( );
            throw new ContinentalException( "Error al recibir los datos del oponente: " + e.getMessage( ) );
        }

    }

    /**
     * Recibe del servidor las 7 cartas del juego, luego la primera carta destapada de la baraja y el turno que le corresponde .
     * <b> pre: </b> Se encuentra establecida una conexión con el servidor. <br>
     * @throws ContinentalException Si ocurre un error al recibir los datos del servidor.
     */
    public void recibirJuego( ) throws ContinentalException
    {

        try
        {
            // Recibe las 7 cartas del juego
            for( int i = 0; i < 7; i++ )
            {
                String[] datosCarta = inReader.readLine( ).split( ":" );
                juego.agregarCarta( datosCarta[ 1 ], datosCarta[ 2 ], datosCarta[ 3 ] );
            }

            // Recibe la primera carta de la baraja
            String[] datosCarta = inReader.readLine( ).split( ":" );
            juego.modificarCartaBarajaJugada( datosCarta[ 1 ], datosCarta[ 2 ], datosCarta[ 3 ] );

            // Recibe a quién le corresponde el primer turno
            String[] datosPrimerTurno = inReader.readLine( ).split( ":" );
            if( datosPrimerTurno[ 1 ].equals( nombre ) )
                estado = REALIZANDO_JUGADA;
            else
                estado = ESPERANDO_JUGADA;
            verificarInvariante( );
        }
        catch( IOException e )
        {
            estado = JUEGO_TERMINADO;
            finJuego = true;
            verificarInvariante( );
            throw new ContinentalException( "Error al recibir las cartas: " + e.getMessage( ) );
        }
    }

    /**
     * Espera la jugada del oponente, puede ser VICTORIA o la carta que jugó este. <br>
     * <b> pre: </b> Se encuentra establecida una conexión con el servidor. <br>
     * @throws ContinentalException Si ocurre un error al recibir la jugada del oponente, si se pierde la conexión con el oponente.
     */
    public void esperarJugada( ) throws ContinentalException
    {
        String[] datos;
        try
        {
            String jugada = inReader.readLine( );
            if( jugada == null )
            {
                estado = JUEGO_TERMINADO;
                throw new ContinentalException( "Se ha perdido la conexión con el oponente." );
            }
            datos = jugada.split( ":" );

            if( datos[ 0 ].equals( "VICTORIA" ) )
            {
                estado = JUEGO_TERMINADO;
                esperarDatosVictoria( datos );
            }
            else if( datos[ 0 ].equals( CARTA ) )
            {
                juego.modificarCartaBarajaJugada( datos[ 1 ], datos[ 2 ], datos[ 3 ] );
                estado = REALIZANDO_JUGADA;
            }
            verificarInvariante( );
        }
        catch( IOException e )
        {
            estado = JUEGO_TERMINADO;
            finJuego = true;
            verificarInvariante( );
            throw new ContinentalException( "Error al recibir la carta jugada por el oponente: " + e.getMessage( ) );
        }
    }

    /**
     * Espera los datos de la victoria del oponente, si la victoria fue valida, recibe el juego con el que gano el oponente.
     * <b> pre: </b> Se encuentra establecida una conexión con el servidor. <br>
     * <b> post: </b> la variable finDeJuego es true y la variable victoriaValidaOponente indica si la victoria del oponente fue válida. <br>
     * @param pDatos Cadena que indica si la victoria fue valida o no.
     * @throws ContinentalException Si ocurre un error al recibir las datos de la victoria del oponente.
     */
    private void esperarDatosVictoria( String[] pDatos ) throws ContinentalException
    {
        try
        {
            if( !pDatos[ 1 ].equals( "FALSA" ) )
            {

                victoriaValidaOponente = true;
            }
            else
                victoriaValidaOponente = false;

            finJuego = true;
        }
        catch( Exception e )
        {
            estado = JUEGO_TERMINADO;
            finJuego = true;
            verificarInvariante( );
            throw new ContinentalException( "Error al recibir los datos de la victoria" );
        }
    }

    /**
     * Envía los datos de la victoria al servidor <b> pre: </b> Se encuentra establecida una conexión con el servidor. <br>
     * @return True si el juego es valido, false de lo contrario.
     * @throws ContinentalException Si ocurre un error al enviar la información de la victoria.
     */
    public boolean enviarDatosVictoria( ) throws ContinentalException
    {

        try
        {
            outWriter.println( VICTORIA );

            // Se recibe el juego del oponente, no se realiza alguna operación con las cartas, se deja para extensión del ejercicio.

            for( int i = 0; i < juego.darJuego( ).size( ); i++ )
            {
                Carta carta = ( Carta )juego.darJuego( ).get( i );
                outWriter.println( carta.toString( ) );
            }
            String[] datos = inReader.readLine( ).split( ":" );
            estado = JUEGO_TERMINADO;

            verificarInvariante( );

            if( datos[ 1 ].equals( "OK" ) )
                return true;
            else
                return false;

        }
        catch( IOException e )
        {
            estado = JUEGO_TERMINADO;
            finJuego = true;
            verificarInvariante( );
            throw new ContinentalException( "Error al enviar los datos de la Victoria" );
        }
    }

    /**
     * Envía la carta jugada al servidor y actualiza el estado a ESPERANDO_JUGADA. <b>
     * pre: </b> El estado del jugador se encuentra REALIZANDO_JUGADA. <br>
     * Se encuentra establecida una conexión con el servidor. <br>
     * <b> post: </b> estado == ESPERANDO_JUGADA. <br>
     * seleccionoCarta == false. <br>
     * Se modifica la carta temporal del juego a null. <br>
     * Se modifica la carta de la baraja jugada del juego con la carta jugada. <br>
     *
     */
    public void realizarJugada( )
    {
        if( juego.darCartaTemporal( ) != null )
        {
            Carta carta = juego.darCartaTemporal( );
            outWriter.println( carta.toString( ) );
            juego.modificarCartaBarajaJugada( carta );
            estado = ESPERANDO_JUGADA;
            seleccionoCarta = false;
            juego.modificarCartaTemporal( null );
            verificarInvariante( );
        }
    }

    /**
     * Pide al servidor una carta de la baraja inicial y la coloca en la carta temporal del juego. <br>
     * <b> pre: </b> Se encuentra establecida una conexión con el servidor. <br>
     * El estado del jugador se encuentra REALIZANDO_JUGADA. <br>
     * <b> post: </b> seleccionoCarta == true. <br>
     * @throws ContinentalException Si ocurre algún error al recibir la carta del servidor.
     */
    public void pedirCartaBarajaInicial( ) throws ContinentalException
    {
        try
        {
            seleccionoCarta = true;
            outWriter.println( BARAJA_INICIAL );

            String[] datosCarta = inReader.readLine( ).split( ":" );
            juego.modificarCartaTemporal( datosCarta[ 1 ], datosCarta[ 2 ], datosCarta[ 3 ] );
        }
        catch( Exception e )
        {
            estado = JUEGO_TERMINADO;
            finJuego = true;
            verificarInvariante( );
            throw new ContinentalException( "Error al pedir carta de la baraja inicial: " + e.getMessage( ) );
        }
    }

    /**
     * Pide al servidor una carta de la baraja jugada y la coloca en la carta temporal del juego.
     * <b> pre: </b> Se encuentra establecida una conexión con el servidor. <br>
     * El estado del jugador se encuentra REALIZANDO_JUGADA. <br>
     * <b> post: </b> seleccionoCarta == true. <br>
     * @throws ContinentalException Si ocurre algún error al recibir la carta del servidor.
     */
    public void pedirCartaBarajaJugada( ) throws ContinentalException
    {
        try
        {
            seleccionoCarta = true;
            outWriter.println( BARAJA_JUGADA );

            String[] datosCarta = inReader.readLine( ).split( ":" );
            juego.modificarCartaTemporal( datosCarta[ 1 ], datosCarta[ 2 ], datosCarta[ 3 ] );

        }
        catch( IOException e )
        {
            estado = JUEGO_TERMINADO;
            finJuego = true;
            verificarInvariante( );
            throw new ContinentalException( "Error al pedir carta de la baraja jugada: " + e.getMessage( ) );
        }
    }

    // -----------------------------------------------------------------
    // Invariante
    // -----------------------------------------------------------------

    /**
     * Verifica el invariante de la clase.<br>
     * <b>inv:</b> estado pertenece a {REALIZANDO_JUGADA, ESPERANDO_JUGADA, ESPERANDO_OPONENTE, JUEGO_TERMINADO}.<br>
     */
    private void verificarInvariante( )
    {
        assert ( estado.equals( REALIZANDO_JUGADA ) || estado.equals( ESPERANDO_JUGADA ) || estado.equals( ESPERANDO_OPONENTE ) || estado.equals( JUEGO_TERMINADO ) ) : "El estado no es válido";

    }

    // -----------------------------------------------------------------
    // Puntos de Extensión
    // -----------------------------------------------------------------

    /**
     * Método para la extensión 1.
     * @return respuesta1.
     */
    public String metodo1( )
    {
        return "Respuesta 1.";
    }

    /**
     * Método para la extensión2.
     * @return respuesta2.
     */
    public String metodo2( )
    {
        return "Respuesta 2.";
    }



}
