package com.example.cexpress_vendedor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    ImageButton imgBtnRegresar, imgBtnFotoPerfil;
    TextInputLayout impNombrePerfil, impCorreoPerfil, impPasswordPerfil;
    EditText editNombrePerfil, editCorreoPerfil, editPasswordPerfil;
    Button btnModificarPerfil, btnGuardarPerfil, btnCancelarPerfil;
    String nombreAnterior, correoAnterior, passwordAnterior, fotoAnterior, nuevoNombre, nuevoCorreo, nuevaPassword, nuevaFoto;
    int idVendedor;
    Boolean nombre = false, correo = false, password = false;
    //Para las imagenes
    String encodedImage;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);
        imgBtnFotoPerfil = findViewById(R.id.imgBtnFotoPerfil);

        impNombrePerfil = findViewById(R.id.impNombrePerfil);
        impCorreoPerfil = findViewById(R.id.impCorreoPerfil);
        impPasswordPerfil = findViewById(R.id.impPasswordPerfil);

        editNombrePerfil = findViewById(R.id.editNombrePerfil);
        editCorreoPerfil = findViewById(R.id.editCorreoPerfil);
        editPasswordPerfil = findViewById(R.id.editPasswordPerfil);

        btnModificarPerfil = findViewById(R.id.btnModificarPerfil);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        btnCancelarPerfil = findViewById(R.id.btnCancelarPerfil);

        request = Volley.newRequestQueue(this);

        cambiarEstadoEditText(editNombrePerfil, false);
        cambiarEstadoEditText(editCorreoPerfil, false);
        cambiarEstadoEditText(editPasswordPerfil, false);
        imgBtnFotoPerfil.setEnabled(false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        recuperarId();
        recuperarPerfil(idVendedor);

        imgBtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgBtnFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpcionesFoto();
            }
        });

        btnModificarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoEditText(editNombrePerfil, true);
                cambiarEstadoEditText(editCorreoPerfil, true);
                cambiarEstadoEditText(editPasswordPerfil, true);
                imgBtnFotoPerfil.setEnabled(true);

                nombreAnterior = editNombrePerfil.getText().toString();
                correoAnterior = editCorreoPerfil.getText().toString();
                passwordAnterior = editPasswordPerfil.getText().toString();

                btnModificarPerfil.setVisibility(View.GONE);
                btnGuardarPerfil.setVisibility(View.VISIBLE);
                btnCancelarPerfil.setVisibility(View.VISIBLE);
            }
        });

        btnGuardarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoEditText(editNombrePerfil, false);
                cambiarEstadoEditText(editCorreoPerfil, false);
                cambiarEstadoEditText(editPasswordPerfil, false);
                imgBtnFotoPerfil.setEnabled(false);

                validarCambios();
            }
        });

        btnCancelarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNombrePerfil.setText(nombreAnterior);
                editCorreoPerfil.setText(correoAnterior);
                editPasswordPerfil.setText(passwordAnterior);
                String urlFoto = "https://appsmoviles2020.000webhostapp.com/imagenes/"+fotoAnterior;
                URL url = null;
                try {
                    url = new URL(urlFoto);
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    imgBtnFotoPerfil.setImageBitmap(bitmap);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                cambiarEstadoEditText(editNombrePerfil, false);
                cambiarEstadoEditText(editCorreoPerfil, false);
                cambiarEstadoEditText(editPasswordPerfil, false);
                imgBtnFotoPerfil.setEnabled(false);

                btnModificarPerfil.setVisibility(View.VISIBLE);
                btnGuardarPerfil.setVisibility(View.GONE);
                btnCancelarPerfil.setVisibility(View.GONE);
            }
        });
    }

    void cambiarEstadoEditText(EditText editText, boolean estado) {
        if(estado) {
            editText.setFocusableInTouchMode(estado);
            editText.setCursorVisible(estado);
        } else {
            editText.setFocusable(estado);
            editText.setCursorVisible(estado);
        }
    }

    void recuperarPerfil(int idVendedor) {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/getVendedor.php?idVendedor="+idVendedor;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("Vendedor");
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            fotoAnterior = jsonObject.getString("foto");
            String urlFoto = "https://appsmoviles2020.000webhostapp.com/imagenes/"+jsonObject.getString("foto");
            URL url = new URL(urlFoto);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imgBtnFotoPerfil.setImageBitmap(bitmap);
            guardarImagen(bitmap);
            editNombrePerfil.setText(jsonObject.getString("nombre"));
            editCorreoPerfil.setText(jsonObject.getString("correo"));
            editPasswordPerfil.setText(jsonObject.getString("password"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void mostrarOpcionesFoto() {
        final CharSequence[] opciones={"Tomar Foto", "Elegir foto de la galería", "Cancelar"};
        final AlertDialog.Builder opc= new AlertDialog.Builder(PerfilActivity.this);
        opc.setTitle("Elige una opción");
        opc.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(opciones[i].equals("Tomar Foto")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 20);
                } else if(opciones[i].equals("Elegir foto de la galería")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(intent.createChooser(intent, "Selecciona una opción"), 10);
                } else {
                    dialog.dismiss();
                }
            }
        });
        opc.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK) {
            switch (requestCode) {
                case 10:
                    Uri miPath=data.getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(miPath);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgBtnFotoPerfil.setImageBitmap(bitmap);

                        guardarImagen(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case 20:
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imgBtnFotoPerfil.setImageBitmap(bitmap);
                    guardarImagen(bitmap);
                    break;
            }
        } else {

        }
    }

    void validarCambios() {
        //Validación nombre
        if(editNombrePerfil.getText().toString().isEmpty()) {
            impNombrePerfil.setError("Campo de nombre está vacío");
            nombre = false;
        } else {
            impNombrePerfil.setError(null);
            nombre = true;
        }
        //Validación correo
        if (Patterns.EMAIL_ADDRESS.matcher(editCorreoPerfil.getText().toString()).matches() == false) {
            impCorreoPerfil.setError("Ingrese un correo válido");
            correo = false;
        } else {
            impCorreoPerfil.setError(null);
            correo = true;
        }
        if(editCorreoPerfil.getText().toString().isEmpty()) {
            impCorreoPerfil.setError("Campo de correo está vacío");
            correo = false;
        } else {
            impCorreoPerfil.setError(null);
            correo = true;
        }
        //Validación contraseña
        if (editPasswordPerfil.getText().toString().isEmpty()) {
            impPasswordPerfil.setError("Campo de contraseña está vacío");
            password = false;
        } else {
            impPasswordPerfil.setError(null);
            password = true;
        }

        if(nombre && correo && password) {
            nuevoNombre = editNombrePerfil.getText().toString();
            nuevoCorreo = editCorreoPerfil.getText().toString();
            nuevaPassword = editPasswordPerfil.getText().toString();

            btnModificarPerfil.setVisibility(View.VISIBLE);
            btnGuardarPerfil.setVisibility(View.GONE);
            btnCancelarPerfil.setVisibility(View.GONE);

            guardarCambios(idVendedor, nuevoNombre, nuevoCorreo, nuevaPassword);
        }
    }

    void guardarCambios(final int idVendedor, final String nombre, final String correo, final String password) {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/actualizarVendedor.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                Toast.makeText(PerfilActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PerfilActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idVendedor", String.valueOf(idVendedor));
                params.put("nombre", nombre);
                params.put("correo", correo);
                params.put("password", password);
                params.put("foto", encodedImage);

                return params;
            }
        };
        request.add(stringRequest);
    }

    void recuperarId() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        idVendedor = sharedPreferences.getInt("id", 0);
    }

    void guardarImagen(Bitmap imagen) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
