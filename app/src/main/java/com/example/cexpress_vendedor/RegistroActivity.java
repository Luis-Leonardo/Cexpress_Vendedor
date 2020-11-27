package com.example.cexpress_vendedor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    EditText editRegistroNombre, editRegistroCorreo, editRegistroPassword, editRegistroConfirmation;
    Button btnRegistrarse;
    TextInputLayout impRegistroNombre, impRegistroCorreo, impRegistroPassword, impRegistroConfirmation;
    boolean nombre = false, correo = false, password = false, confirmation = false;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        editRegistroNombre = findViewById(R.id.editRegistroNombre);
        editRegistroCorreo = findViewById(R.id.editRegistroCorreo);
        editRegistroPassword = findViewById(R.id.editRegistroPassword);
        editRegistroConfirmation = findViewById(R.id.editRegistroConfirmation);

        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        impRegistroNombre = findViewById(R.id.impRegistroNombre);
        impRegistroCorreo = findViewById(R.id.impRegistroCorreo);
        impRegistroPassword = findViewById(R.id.impRegistroPassword);
        impRegistroConfirmation = findViewById(R.id.impRegistroConfirmation);

        request = Volley.newRequestQueue(this);

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validación nombre
                if(editRegistroNombre.getText().toString().isEmpty()) {
                    impRegistroNombre.setError("Campo de nombre está vacío");
                    nombre = false;
                } else {
                    impRegistroNombre.setError(null);
                    nombre = true;
                }
                //Validación correo
                if (Patterns.EMAIL_ADDRESS.matcher(editRegistroCorreo.getText().toString()).matches() == false) {
                    impRegistroCorreo.setError("Ingrese un correo válido");
                    correo = false;
                } else {
                    impRegistroCorreo.setError(null);
                    correo = true;
                }
                if(editRegistroCorreo.getText().toString().isEmpty()) {
                    impRegistroNombre.setError("Campo de correo está vacío");
                    correo = false;
                } else {
                    impRegistroNombre.setError(null);
                    correo = true;
                }
                //Validación contraseña
                if (editRegistroPassword.getText().toString().isEmpty()) {
                    impRegistroPassword.setError("Campo de contraseña está vacío");
                    password = false;
                } else {
                    impRegistroPassword.setError(null);
                    password = true;
                }
                if (editRegistroConfirmation.getText().toString().isEmpty()) {
                    impRegistroConfirmation.setError("Campo de confirmación está vacío");
                    confirmation = false;
                } else {
                    if(!editRegistroConfirmation.getText().toString().equals(editRegistroPassword.getText().toString())) {
                        impRegistroConfirmation.setError("Las contraseñas ingresadas no coinciden");
                        confirmation = false;
                    } else {
                        impRegistroConfirmation.setError(null);
                        confirmation = true;
                    }
                }

                // para hacer la autenticación provicional
                if (nombre && correo && password && confirmation) {
                    String nombre = editRegistroNombre.getText().toString();
                    String correo = editRegistroCorreo.getText().toString();
                    String password = editRegistroPassword.getText().toString();

                    registrarVendedor(nombre, correo, password);
                }
            }
        });
    }

    void registrarVendedor(String nombre, String correo, String password) {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/registrarVendedor.php?nombre="+nombre+"&correo="+correo+"&password="+password;
        URL = URL.replace(" ", "%20").replace("@", "%40");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Intent i = new Intent(RegistroActivity.this, MainActivity.class);
        startActivity(i);
    }
}
