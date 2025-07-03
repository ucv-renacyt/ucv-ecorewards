package com.example.ecorewardsuser;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecCon extends AppCompatActivity {

    private EditText edtCorreo;
    private EditText edtNuevaContrasena;
    private EditText edtConfirmarContrasena;
    private Button btnActualizarContrasena;
    private Button btnBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reccon);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAuth = FirebaseAuth.getInstance();

        edtCorreo = findViewById(R.id.edtCorreo);
        edtNuevaContrasena = findViewById(R.id.edtNuevaContrasena);
        edtConfirmarContrasena = findViewById(R.id.edtConfirmarContrasena);
        btnActualizarContrasena = findViewById(R.id.btnActualizarContrasena);
        btnBack = findViewById(R.id.btnBack);

        btnActualizarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarContrasena();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecCon.this, Login.class));
                finish();
            }
        });
    }

    private void actualizarContrasena() {
        String correo = edtCorreo.getText().toString().trim();
        String nuevaContrasena = edtNuevaContrasena.getText().toString().trim();
        String confirmarContrasena = edtConfirmarContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(correo) || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese un correo electrónico válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nuevaContrasena)) {
            Toast.makeText(this, "Ingrese una nueva contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecCon.this, "Correo de recuperación enviado. Por favor revise su correo electrónico.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RecCon.this, "Error al enviar el correo de recuperación: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
