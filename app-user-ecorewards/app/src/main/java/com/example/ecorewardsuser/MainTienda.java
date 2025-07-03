package com.example.ecorewardsuser;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainTienda extends AppCompatActivity {
    Button regresar1;
    String objetoSeleccionado = "";
    int puntosNecesarios = 0;
    DatabaseReference usuarioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tienda);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        regresar1 = findViewById(R.id.btnRegresar1);
        regresar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MenuPrincipal.class));
            }
        });

        Button btnCanjearAgua = findViewById(R.id.btnCanjearAgua);
        Button btnCanjearGalle = findViewById(R.id.btnCanjearGalle);
        Button btnCanjearPen = findViewById(R.id.btnCanjearPen);
        Button btnCanjearLa = findViewById(R.id.btnCanjearLa);

        btnCanjearAgua.setOnClickListener(canjearListener);
        btnCanjearGalle.setOnClickListener(canjearListener);
        btnCanjearPen.setOnClickListener(canjearListener);
        btnCanjearLa.setOnClickListener(canjearListener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            usuarioRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("puntos");
        }
    }

    private View.OnClickListener canjearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnCanjearAgua) {
                objetoSeleccionado = "Agua";
                puntosNecesarios = 200;
            } else if (v.getId() == R.id.btnCanjearGalle) {
                objetoSeleccionado = "Galleta";
                puntosNecesarios = 150;
            } else if (v.getId() == R.id.btnCanjearPen) {
                objetoSeleccionado = "Lapicero";
                puntosNecesarios = 140;
            } else if (v.getId() == R.id.btnCanjearLa) {
                objetoSeleccionado = "Lápiz";
                puntosNecesarios = 140;
            }

            if (usuarioRef != null) {
                usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int puntosActuales = dataSnapshot.getValue(Integer.class);
                            if (puntosActuales >= puntosNecesarios) {
                                restarPuntos(puntosActuales, puntosNecesarios);
                            } else {
                                Toast.makeText(MainTienda.this, "Aún te faltan EcoPuntos", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainTienda.this, "No se encontraron puntos para el usuario", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainTienda.this, "Error al obtener puntos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    private void restarPuntos(int puntosActuales, int puntosNecesarios) {
        int puntosRestantes = puntosActuales - puntosNecesarios;
        if (usuarioRef != null) {
            usuarioRef.setValue(puntosRestantes).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(MainTienda.this, Canje.class);
                        intent.putExtra("OBJETO_SELECCIONADO", objetoSeleccionado);
                        intent.putExtra("MENSAJE", "Producto canjeado correctamente");
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainTienda.this, "Error al restar puntos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }}