package com.example.ecorewardsuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText correo;
    private EditText edtContraseña;
    private StringBuilder passwordBuilder = new StringBuilder();
    private CheckBox recordarme;
    private Button iniciarSesion;
    private TextView registrarse, olvidasteContraseña;
    private FirebaseAuth mAuth;

    private static final String PREFS_NAME = "EcoRewardsPrefs";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER_ME = "remember_me";

    private ImageView verContraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();

        correo = findViewById(R.id.edtCodigo);
        edtContraseña = findViewById(R.id.edtContraseña);
        recordarme = findViewById(R.id.chkRecordarme);
        iniciarSesion = findViewById(R.id.btnIniciarSesion2);
        registrarse = findViewById(R.id.txvCrearCuenta);
        olvidasteContraseña = findViewById(R.id.txvOlvidastePassword);
        verContraseña = findViewById(R.id.ver);
        edtContraseña.setFocusable(false);
        edtContraseña.setFocusableInTouchMode(false);
        edtContraseña.setClickable(false);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedEmail = preferences.getString(PREF_EMAIL, "");
        String savedPassword = preferences.getString(PREF_PASSWORD, "");
        boolean rememberMe = preferences.getBoolean(PREF_REMEMBER_ME, false);

        correo.setText(savedEmail);
        recordarme.setChecked(rememberMe);

        configurarBotonesNumeros();

        verContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibilidadContraseña();
            }
        });

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarVentanaRegistrarse();
            }
        });

        olvidasteContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarVentanaRecuperarContrasena();
            }
        });

        verContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibilidadContraseña();
            }
        });
    }

    private void configurarBotonesNumeros() {
        Button b0 = findViewById(R.id.b0);
        Button b1 = findViewById(R.id.b1);
        Button b2 = findViewById(R.id.b2);
        Button b3 = findViewById(R.id.b3);
        Button b4 = findViewById(R.id.b4);
        Button b5 = findViewById(R.id.b5);
        Button b6 = findViewById(R.id.b6);
        Button b7 = findViewById(R.id.b7);
        Button b8 = findViewById(R.id.b8);
        Button b9 = findViewById(R.id.b9);
        Button bBorrar = findViewById(R.id.bBorrar);

        b0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNumero("0");
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNumero("1");
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNumero("2");
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNumero("3");
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNumero("4");
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNumero("5");
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNumero("6");
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNumero("7");
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNumero("8");
            }
        });
        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNumero("9");
            }
        });
        bBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarUltimoCaracter();
            }
        });
    }

    private void agregarNumero(String numero) {
        if (passwordBuilder.length() < 6) {
            passwordBuilder.append(numero);
            edtContraseña.setText(passwordBuilder.toString());
        }
    }

    private void borrarUltimoCaracter() {
        if (passwordBuilder.length() > 0) {
            passwordBuilder.deleteCharAt(passwordBuilder.length() - 1);
            edtContraseña.setText(passwordBuilder.toString());
        }
    }

    private void toggleVisibilidadContraseña() {
        boolean contraseñaVisible = edtContraseña.getTransformationMethod() == null;
        if (contraseñaVisible) {
            edtContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
            verContraseña.setImageResource(R.drawable.visible);
        } else {
            edtContraseña.setTransformationMethod(null);
            verContraseña.setImageResource(R.drawable.novisible);
        }
    }
    private void iniciarSesion() {
        String email = correo.getText().toString().trim();
        String password = passwordBuilder.toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Ingrese su contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() != 6) {
            Toast.makeText(getApplicationContext(), "La contraseña debe ser de exactamente 6 números", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (recordarme.isChecked()) {
                                guardarPreferencias(email, password, true);
                            } else {
                                guardarPreferencias("", "", false);
                            }
                            Intent intent = new Intent(Login.this, MenuPrincipal.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Inicio de sesión fallido, verifique su correo y su contraseña", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardarPreferencias(String email, String password, boolean rememberMe) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_EMAIL, email);
        editor.putString(PREF_PASSWORD, password);
        editor.putBoolean(PREF_REMEMBER_ME, rememberMe);
        editor.apply();
    }

    private void cargarVentanaRegistrarse() {
        Intent intent = new Intent(Login.this, Registrarse.class);
        startActivity(intent);
    }

    private void cargarVentanaRecuperarContrasena() {
        Intent intent = new Intent(Login.this, RecCon.class);
        startActivity(intent);
    }

}
