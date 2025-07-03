package com.example.ecorewardsuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registrarse extends AppCompatActivity {

    private EditText nombres;
    private EditText apellidos;
    private EditText correo;
    private EditText contrasena;
    private EditText repetirContrasena;
    private ImageView visible1;
    private ImageView visible2;
    private Button registrarse;
    private TextView iniciarSesion;
    private boolean mostrarContrasena1 = false;
    private boolean mostrarContrasena2 = false;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        nombres = findViewById(R.id.txdNombres);
        apellidos = findViewById(R.id.txdApellidos);
        correo = findViewById(R.id.txdCorreo);
        contrasena = findViewById(R.id.txdContrasena);
        repetirContrasena = findViewById(R.id.txdConfirmarContrasena);
        visible1 = findViewById(R.id.imvVer);
        visible2 = findViewById(R.id.imvVer1);
        registrarse = findViewById(R.id.btnRegistrarse);
        iniciarSesion = findViewById(R.id.txvLinkLogin);

        contrasena.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
        repetirContrasena.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });

        visible1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoContrasena(mostrarContrasena1, contrasena, visible1);
            }
        });

        visible2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoContrasena(mostrarContrasena2, repetirContrasena, visible2);
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarCuenta();
            }
        });

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarVentanaInicioSesion();
            }
        });
    }

    private void estadoContrasena(boolean estado, EditText campoContrasena, ImageView imagenVisible) {
        if (estado) {
            campoContrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imagenVisible.setImageResource(R.drawable.novisible);
        } else {
            campoContrasena.setTransformationMethod(null);
            imagenVisible.setImageResource(R.drawable.visible);
        }
        if (campoContrasena == contrasena) {
            mostrarContrasena1 = !estado;
        } else if (campoContrasena == repetirContrasena) {
            mostrarContrasena2 = !estado;
        }
    }

    private void registrarCuenta() {
        final String nom = nombres.getText().toString().trim();
        final String ape = apellidos.getText().toString().trim();
        final String cor = correo.getText().toString().trim();
        final String con = contrasena.getText().toString();
        String rcon = repetirContrasena.getText().toString();

        if (nom.isEmpty() || ape.isEmpty() || cor.isEmpty() || con.isEmpty() || rcon.isEmpty()) {
            Toast.makeText(this, "Llene todos los campos.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!con.equals(rcon)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!con.matches("\\d{6}")) {
            Toast.makeText(this, "La contraseña debe contener exactamente 6 números.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(cor)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (!isNewUser) {
                            Toast.makeText(Registrarse.this, "El correo ya está registrado", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mAuth.createUserWithEmailAndPassword(cor, con)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                String userId = user.getUid();
                                                Map<String, Object> userData = new HashMap<>();
                                                userData.put("nombre", nom);
                                                userData.put("apellidos", ape);
                                                userData.put("correo", cor);
                                                userData.put("puntos", 0);

                                                databaseReference.child(userId).setValue(userData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(Registrarse.this, "Usuario registrado correctamente",
                                                                            Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(Registrarse.this, Inicio.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(Registrarse.this, "Error al guardar los datos: " + task.getException().getMessage(),
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            Toast.makeText(Registrarse.this, "Error al registrar: " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
    }

    private void cargarVentanaInicioSesion() {
        Intent intentInicioSesion = new Intent(this, Login.class);
        startActivity(intentInicioSesion);
    }
}
