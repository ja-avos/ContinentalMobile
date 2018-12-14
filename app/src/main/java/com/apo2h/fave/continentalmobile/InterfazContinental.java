package com.apo2h.fave.continentalmobile;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import mundo.Carta;
import mundo.ContinentalException;
import mundo.JugadorContinental;
import mundo.ThreadConectar;
import mundo.ThreadEnviarDatosVictoria;
import mundo.ThreadEnviarJugada;
import mundo.ThreadEsperarCarta;
import mundo.ThreadEsperarJugada;

public class InterfazContinental extends AppCompatActivity {

    public static final int PUERTO = 9999;


    Socket server;

    ImageButton[] cartas;

    ImageButton cartaBarajaI, cartaBarajaJ, cartaTemp;

    EditText username;

    EditText serverAddress;

    Button loginBtn;

    ImageButton infoButton, nextButton, seleccionado;

    AlertDialog.Builder alerta;

    /**
     * Dirección del servidor.
     */
    private String servidor;

    /**
     * Nombre del usuario.
     */
    private String usuario;

    /**
     * Puerto del servidor.
     */
    private int puerto;

    /**
     * Clase principal del mundo.
     */
    private JugadorContinental jugador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alerta = new AlertDialog.Builder(this);
        jugador = new JugadorContinental();
        setTitle("Continental mobile");
        ingresar();
    }

    @Override
    public void onBackPressed()
    {
        alerta.setMessage("Seguro que desea salir de la partida?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ingresar();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do Nothing
            }
        });

        alerta.show();

    }

    public void ingresar()
    {
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username);
        serverAddress = (EditText) findViewById(R.id.serverAddress);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new IngresoListener());
    }

    /**
     * Configura los datos de la conexión.
     * @param pServidor Nombre del servidor.
     * @param pUsuario Nombre del usuario.
     */
    public void configurarDatosConexion( String pServidor, String pUsuario )
    {
        servidor = pServidor;
        puerto = 9999;
        usuario = pUsuario;
        conectar( );

    }

    public void comenzarJuego(String usuario, final String host)
    {
        setContentView(R.layout.activity_main);

        cartas = new ImageButton[7];
        cartaBarajaI = (ImageButton) findViewById(R.id.barajaIncialButton);
        cartaBarajaJ = (ImageButton) findViewById(R.id.barajaJugadaButton);
        cartaTemp = (ImageButton) findViewById(R.id.cartaTemp);

        cartaBarajaI.setOnClickListener(new OnInicialListener());
        cartaBarajaI.setTag("cover");
        cartaBarajaJ.setOnClickListener(new OnCartasListener());
        cartaBarajaJ.setTag("nocarta");
        cartaTemp.setOnClickListener(new OnCartasListener());
        cartaTemp.setTag("nocarta");

        for(int i = 0; i < cartas.length; i++)
        {
            cartas[i] = (ImageButton) findViewById(GetID.getResId("carta" + (i+1), R.id.class));
            cartas[i].setOnClickListener(new OnCartasListener());
            cartas[i].setTag("nocarta");
        }

        cartas[0].setTag("carta_8_corazones_");
        cartas[0].setImageResource(R.drawable.carta_8_corazones_);
        cartas[2].setTag("carta_a_diamantes_");
        cartas[2].setImageResource(R.drawable.carta_a_diamantes_);
        cartas[4].setTag("carta_3_picas_");
        cartas[4].setImageResource(R.drawable.carta_3_picas_);

        infoButton = (ImageButton) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarInformacion();
            }
        });

        nextButton = (ImageButton) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                realizarJugada();

            }
        });

    }

    public class OnInicialListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            pedirCartaBarajaInicial();
        }
    }

    public class OnCartasListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            if(seleccionado == null)
            {
                seleccionado = (ImageButton) v;
                seleccionado.setForeground(getDrawable(R.drawable.ic_launcher_foreground));
            }
            else {
                cambiarCartas(seleccionado, (ImageButton) v);
                seleccionado.setForeground(null);
                seleccionado = null;
            }
        }
    }

    //Lanza un mensaje en caso de ganar o perder la partida
    public void victoria(boolean pVictoria){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Continental");
        if(pVictoria){
            builder.setMessage( "Felicitaciones, has ganado la partida.");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int id) {
                    switch (id){
                        case RESULT_OK:
                            dialogInterface.dismiss();
                            break;
                    }
                }
            });
        }
        else{
            builder.setMessage("Lo sentimos, no has ganado la partida.");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i){
                        case RESULT_OK:
                            dialogInterface.dismiss();
                            break;
                    }
                }
            });
        }
    }

    //Muestra la información de la partida
    public void mostrarInformacion()
    {
        alerta.setMessage("Esta es una prueba. \n\t En la versión final aparecerá con \n \t datos de jugadores.");
        alerta.show();
    }

    //Termina la jugada y envía la carta en la mesa al servidor
    public void realizarJugada()
    {
        if(((String)cartaTemp.getTag()).equals("nocarta"))
            mostrarMensaje("Debe realizar alguna jugada");
        else
        {
            ThreadEnviarJugada t = new ThreadEnviarJugada( jugador, this );
            t.start( );
        }
    }

    //Muestra quien gano la partida
    public void mostrarInformacionGanador(boolean pVictoriaValida){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Continental");

        if(pVictoriaValida){
            builder.setMessage("Lo sentimos, " + jugador.darNombreOponente() + " ha ganado la partida");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int id) {
                    switch (id){
                        case RESULT_OK:
                            dialogInterface.dismiss();
                            break;
                    }
                }
            });
        }
        else{
            builder.setMessage("Felicitaciones, has ganado el juego. " + jugador.darNombreOponente() + "cantó victoria invalida");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i){
                        case RESULT_OK:
                            dialogInterface.dismiss();
                            break;
                    }
                }
            });
        }
    }

    //Muestra un error en caso de que el thread falle
    public void mostrarError(ContinentalException pEvento){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Continental");
        builder.setMessage(pEvento.getMessage());
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case RESULT_OK:
                        dialogInterface.dismiss();
                        break;
                }
            }
        });
    }

    public void conectar(){
        String usuario = username.getText().toString();
        String servidor = serverAddress.getText().toString();
        try
        {
            ThreadConectar t = new ThreadConectar(jugador, this, usuario, servidor, PUERTO);
            t.start();
        }
        catch(Exception e) {
            mostrarMensaje(e.getMessage());
        }


    }

    public final class IngresoListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            String usuario = username.getText().toString();
            String servidor = serverAddress.getText().toString();
            alerta.setMessage("El usuario es:    " + usuario + "\nEl servidor es:    " + servidor);
            configurarDatosConexion(servidor, usuario);
            alerta.show();
            comenzarJuego(usuario, servidor);
        }
    }

    public void quitarCarta(ImageButton btn)
    {
        btn.setImageResource(R.drawable.nocarta);
        btn.setTag("nocarta");
    }

    public void cambiarCartas(ImageButton btn1, ImageButton btn2)
    {
        String tag1 = (String) btn1.getTag();
        btn1.setTag(btn2.getTag());
        btn1.setImageResource(GetID.getResId((String) btn2.getTag(), R.drawable.class));
        btn2.setTag(tag1);
        btn2.setImageResource(GetID.getResId(tag1, R.drawable.class));
    }

    public void mostrarMensaje(String mensaje)
    {
        alerta.setMessage(mensaje);
        alerta.show();
    }

    public void cambiarCarta(ImageButton carta, String nombre)
    {
        carta.setTag(nombre);
        carta.setImageResource(GetID.getResId(nombre, R.drawable.class));
    }

    public String traducirCarta(Carta carta)
    {
        String repre = carta.darRepresentacion().toLowerCase();
        return "carta_" + (repre.equals("as")?"a":repre) + "_" + carta.darPalo().toLowerCase() + "_";
    }

    public void actualizarInterfaz()
    {
        List<Carta> cartasM = jugador.darJuego().darJuego();
        for(int i = 0; i < 7; i++)
        {
            cambiarCarta(cartas[i], traducirCarta(cartasM.get(i)));
        }
        cambiarCarta(cartaBarajaJ, traducirCarta(jugador.darJuego().darCartaBarajaJugada()));
        cambiarCarta(cartaTemp, traducirCarta(jugador.darJuego().darCartaTemporal()));
    }

    /**
     * Espera la jugada del oponente. <br>
     * El proceso para esperar una jugada se hace en un hilo aparte usando la clase ThreadEsperarJugada.
     */
    public void esperarJugada( )
    {
        cartaBarajaJ.setClickable(false);
        cartaBarajaI.setClickable(false);
        nextButton.setClickable(false);
        ThreadEsperarJugada t = new ThreadEsperarJugada( jugador, this );
        t.start( );

    }

    /**
     * Pide una carta al servidor de la baraja inicial. <br>
     * El proceso para esperar una jugada se hace en un hilo aparte usando la clase ThreadEsperarCarta.
     */
    public void pedirCartaBarajaInicial( )
    {
        if( jugador.darEstado( ) == JugadorContinental.ESPERANDO_JUGADA )
        {
            mostrarMensaje("Espera a tu turno");
        }
        else
        {
            if( jugador.seleccionoCarta( ) )
            {
                mostrarMensaje("Ya seleccionaste una carta");
            }
            else
            {
                ThreadEsperarCarta t = new ThreadEsperarCarta( jugador, this, ThreadEsperarCarta.INICIAL );
                t.start( );
            }
        }
    }

    /**
     * Pide una carta al servidor de la baraja donde se ubican las cartas ya jugadas. <br>
     * El proceso para esperar una jugada se hace en un hilo aparte usando la clase ThreadEsperarCarta.
     */
    public void pedirCartaBarajaJugada( )
    {
        if( jugador.darEstado( ) == JugadorContinental.ESPERANDO_JUGADA )
        {
            mostrarMensaje("Espera tu turno");
        }
        else
        {
            if( jugador.seleccionoCarta( ) )
            {
                mostrarMensaje("Ya seleccionó una carta");
            }
            else
            {
                jugador.darJuego( ).modificarCartaBarajaJugada( null );
                ThreadEsperarCarta t = new ThreadEsperarCarta( jugador, this, ThreadEsperarCarta.JUGADA);
                t.start( );
            }
        }
    }

    /**
     * Valida si el jugador tiene un juego correcto. <br>
     * El proceso para validar la victoria se hace en un hilo aparte usando la clase ThreadEnviarDatosVictoria.
     */
    public void validarVictoria( )
    {
        ThreadEnviarDatosVictoria t = new ThreadEnviarDatosVictoria( jugador, this );
        t.start( );

    }
}
