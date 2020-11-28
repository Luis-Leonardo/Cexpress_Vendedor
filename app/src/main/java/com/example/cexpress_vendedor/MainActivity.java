package com.example.cexpress_vendedor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    ImageButton imgBtnPerfil, imgBtnNegocios, imgBtnPedidos;
    Button btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgBtnPerfil = findViewById(R.id.imgBtnPerfil);
        imgBtnNegocios = findViewById(R.id.imgBtnNegocios);
        imgBtnPedidos = findViewById(R.id.imgBtnPedidos);
        btnSalir = findViewById(R.id.btnSalir);

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarSesion();
            }
        });

        imgBtnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PerfilActivity.class);
                startActivity(i);
            }
        });

        imgBtnNegocios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NegociosActivity.class);
                startActivity(i);
            }
        });

        imgBtnPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PedidosActivity.class);
                startActivity(i);
            }
        });
    }
    //con esta función se modifica el valor del id para cerrar la sesión
    void finalizarSesion() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        sharedPreferences.edit().putInt("id", Integer.valueOf(0)).apply();

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
