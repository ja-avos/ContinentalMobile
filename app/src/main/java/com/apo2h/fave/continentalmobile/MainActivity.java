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

public class MainActivity extends AppCompatActivity {

    ImageView[] cartas;

    ImageButton cartaBarajaI;

    ImageView cartaBarajaJ;

    EditText username;

    EditText serverAddress;

    Button loginBtn;

    AlertDialog.Builder alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alerta = new AlertDialog.Builder(this);
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

    public void comenzarJuego(String usuario, String host)
    {
        setContentView(R.layout.activity_main);

        cartas = new ImageView[7];
        cartaBarajaI = (ImageButton) findViewById(R.id.barajaIncialButton);
        cartaBarajaJ = (ImageView) findViewById(R.id.barajaJugadaImage);

        cartaBarajaJ.setOnLongClickListener(new ChoiceTouchListener());
        for(int i = 0; i < cartas.length; i++)
        {
            cartas[i] = (ImageView) findViewById(GetID.getResId("carta" + (i+1), R.id.class));
            cartas[i].setOnLongClickListener(new ChoiceTouchListener());
            if(i%2 != 0)
                cartas[i].setTag("rutaDePrueba");
            else
                cartas[i].setTag("noCarta");
        }
    }

    public void cambiarCarta(ImageView carta1, ImageView carta2)
    {
        String tag1 = (String) carta1.getTag();
        carta1.setTag(carta2.getTag());
        carta1.setImageResource(GetID.getResId((String)carta2.getTag(), R.drawable.class));
        carta2.setTag(tag1);
        carta2.setImageResource(GetID.getResId(tag1, R.drawable.class));
    }

    public final class ChoiceTouchListener implements View.OnLongClickListener
    {
        @Override
        public boolean onLongClick(View v) {
            if(((ImageView)v).getDrawable() != null)
            {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                return true;
            }

            return false;
        }
    }

    public final class IngresoListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            String usuario = username.getText().toString();
            String servidor = serverAddress.getText().toString();
            alerta.setMessage("El usuario es:    " + usuario + "\nEl servidor es:    " + servidor);
            alerta.show();
            comenzarJuego(usuario, servidor);
        }
    }
}
