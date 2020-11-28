package com.example.cexpress_vendedor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    EditText txtUsuario, txtPasswd;
    Button btnIngresar, btnRegistrar;
    TextInputLayout impUsuario, impPasswd;
    boolean user=false;
    boolean pass=false;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //aqui se llama a la funcion para verificar antes que el resto
        verificarSesion();

        txtUsuario = findViewById(R.id.Usuario);
        txtPasswd = findViewById(R.id.Password);
        btnIngresar = findViewById(R.id.Ingresar);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        impUsuario = findViewById(R.id.impUsuario);
        impPasswd = findViewById(R.id.impPassword);

        request = Volley.newRequestQueue(this);

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
                    pass = false;
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
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/login.php?correo="+correo+"&password="+password;
        URL = URL.replaceAll("@", "%40");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("Vendedor");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if(jsonObject.getString("idVendedor").equals("0")) {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
            } else {
                generarSesion(jsonObject.getString("idVendedor"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //con esta función se guarda el id del usuario en caso de que el login sea correcto
    void generarSesion(String idVendedor) {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        sharedPreferences.edit().putInt("id", Integer.valueOf(idVendedor)).apply();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    //con esta funcion se verifica si hay una sesion iniciada
    void verificarSesion() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);

        if(id!=0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}