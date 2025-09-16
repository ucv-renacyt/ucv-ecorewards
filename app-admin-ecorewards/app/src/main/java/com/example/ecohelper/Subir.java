package com.example.ecohelper;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Subir extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 123;
    private static final String CHANNEL_ID = "upload_channel";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    private Uri imageUri;
    private Spinner spinnerEtiquetas;
    private Button btnSubirImagen;
    private ImageView imgPreview;
    private Button btnRegresar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private void limpiarCampos() {
        imgPreview.setImageDrawable(null);
        imageUri = null;
        // if (spinnerEtiquetas.getAdapter() != null && spinnerEtiquetas.getAdapter().getCount() > 0) {
        //     spinnerEtiquetas.setSelection(0);
        // }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        spinnerEtiquetas = findViewById(R.id.spinnerEtiquetas);
        btnSubirImagen = findViewById(R.id.btnSubirImagen);
        imgPreview = findViewById(R.id.imgPreview);
        btnRegresar = findViewById(R.id.btnRegresar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.etiquetas_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEtiquetas.setAdapter(adapter);

        createNotificationChannel();

        btnSubirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirImagen();
            }
        });

        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(Subir.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
        });

        imgPreview.setOnClickListener(this::buscarImagen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    public void buscarImagen(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void subirImagen() {
        if (imageUri != null) {
            final String etiqueta = spinnerEtiquetas.getSelectedItem().toString();
            String fileName = System.currentTimeMillis() + "_" + etiqueta.replaceAll("\\s+", "_") + ".jpg";
            StorageReference fileReference = mStorage.child("uploads").child(etiqueta).child(fileName);

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // LA IMAGEN SE SUBIÓ A STORAGE (SEGÚN putFile)
                        // MOSTRAR NOTIFICACIÓN AQUÍ
                        Toast.makeText(Subir.this, "Archivo enviado a Storage.", Toast.LENGTH_SHORT).show(); // Toast para indicar solo la subida a Storage
                        mostrarNotificacion(); // Muestra "Imagen subida correctamente"
                        limpiarCampos(); // Limpia campos inmediatamente

                        // Intenta obtener la URL de descarga y guardar en la base de datos (esto ahora ocurre en segundo plano respecto a la notificación)
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            if (mAuth.getCurrentUser() == null) {
                                // Log o manejo silencioso, ya que la notificación de "éxito" ya se mostró
                                System.err.println("Error: Usuario no autenticado al intentar guardar en DB.");
                                return;
                            }
                            String userId = mAuth.getCurrentUser().getUid();

                            Map<String, Object> imageMap = new HashMap<>();
                            imageMap.put("imageUrl", downloadUrl);
                            imageMap.put("etiqueta", etiqueta);

                            mDatabase.child("imagenes").child(userId).push().setValue(imageMap)
                                    .addOnSuccessListener(aVoid -> {
                                        // Datos guardados en la base de datos (manejo silencioso o log)
                                        System.out.println("Datos de imagen guardados en DB exitosamente.");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error específico al guardar en la base de datos (manejo silencioso o log)
                                        System.err.println("Error al guardar datos de la imagen en DB: " + e.getMessage());
                                        // Podrías considerar mostrar un Toast aquí si quieres notificar este error específico,
                                        // pero sería después de la notificación de "éxito"
                                        // Toast.makeText(Subir.this, "Error al guardar metadatos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }).addOnFailureListener(e -> {
                            // Error específico al obtener la URL de descarga (manejo silencioso o log)
                            System.err.println("Error al obtener URL de descarga: " + e.getMessage());
                            // Toast.makeText(Subir.this, "Error al obtener URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Error específico al subir el archivo a Storage
                        Toast.makeText(Subir.this, "Error al subir la imagen a Storage: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(this, "Selecciona una imagen para subir", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // **RECUERDA CAMBIAR ESTO**
                .setContentTitle("Imagen Subida")
                .setContentText("Tu imagen fue registrada correctamente.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificación concedido.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de notificación denegado. Las notificaciones no se mostrarán.", Toast.LENGTH_LONG).show();
            }
        }
    }
}