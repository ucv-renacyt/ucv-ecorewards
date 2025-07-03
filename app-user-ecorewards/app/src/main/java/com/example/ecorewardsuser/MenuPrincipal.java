package com.example.ecorewardsuser;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    private TextView txvPuntos;
    private DatabaseReference puntosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        txvPuntos = findViewById(R.id.txvPuntos);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            puntosRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("puntos");

            puntosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int puntosActuales = dataSnapshot.getValue(Integer.class);
                        txvPuntos.setText(puntosActuales + " EcoPoints");
                    } else {
                        txvPuntos.setText("0 EcoPoints");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    txvPuntos.setText("Error al obtener puntos");
                }
            });
        }

        ImageButton imvCanjear = findViewById(R.id.imvCanjear);
        imvCanjear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, MainTienda.class));
            }
        });

        Button btnRegresar2 = findViewById(R.id.btnRegresar2);
        btnRegresar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MenuPrincipal.this, Login.class));
                finish();
            }
        });
    }
}
