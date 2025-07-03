package com.example.ecorewardsuser;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
//import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Canje extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canje);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        TextView textViewCanjeado = findViewById(R.id.textViewCanjeado);
        TextView txvMensaje = findViewById(R.id.txvMensaje);

        Intent intent = getIntent();
        String objetoSeleccionado = intent.getStringExtra("OBJETO_SELECCIONADO");
        String mensaje = intent.getStringExtra("MENSAJE");

        //textViewCanjeado.setText("Se ha canjeado '" + objetoSeleccionado +  "' correctamente");
        String mensajeFormateado = getString(R.string.canje_exitoso_mensaje, objetoSeleccionado);
        textViewCanjeado.setText(mensajeFormateado);
        txvMensaje.setText(mensaje);

        Button btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> {
            Intent intent1 = new Intent(Canje.this, MainTienda.class);
            startActivity(intent1);
            finish();
        });
    }
}
