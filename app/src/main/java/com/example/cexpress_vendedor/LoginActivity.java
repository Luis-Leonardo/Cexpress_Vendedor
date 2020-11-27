package com.example.cexpress_vendedor;

import androidx.appcompat.app.AppCompatActivity;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText txtUsuario, txtPasswd;
    Button btnIngresar, btnRegistrar;
    TextInputLayout impUsuario, impPasswd;
    boolean user=false;
    boolean pass=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsuario = findViewById(R.id.Usuario);
        txtPasswd = findViewById(R.id.Password);
        btnIngresar = findViewById(R.id.Ingresar);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        impUsuario = findViewById(R.id.impUsuario);
        impPasswd = findViewById(R.id.impPassword);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(i);
            }
        });
        btnIngresar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Validaciones correo
                if (Patterns.EMAIL_ADDRESS.matcher(txtUsuario.getText().toString()).matches() == false) {
                    impUsuario.setError("Ingrese un correo válido");
                    user = false;
                } else {
                    impUsuario.setError(null);
                    user = true;
                }
                if (txtUsuario.getText().toString().isEmpty()) {
                    impUsuario.setError("Campo de usuario esta vacío");
                    user = false;
                } else {
                    impUsuario.setError(null);
                    user = true;
                }
                //Validaciones contraseña
                if (txtPasswd.getText().toString().isEmpty()) {
                    impPasswd.setError("Campo de contraseña está vacío");
                    user = false;
                } else {
                    impPasswd.setError(null);
                    pass = true;
                }

                // para hacer la autenticación provicional
                if (user && pass) {
                    String correo = txtUsuario.getText().toString();
                    String clave = txtPasswd.getText().toString();
                    login(correo, clave);
                }
            }
        });
    }

    void  login(final String correo, final String password) {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Error con el servidor. Inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("correo", correo);
                parametros.put("password", password);
                return  parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}