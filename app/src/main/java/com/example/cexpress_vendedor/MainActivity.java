package com.example.cexpress_vendedor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    ImageButton imgBtnPerfil, imgBtnNegocios, imgBtnPedidos;
    Button btnSalir;
    BarChart chart;

    int idVendedor;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgBtnPerfil = findViewById(R.id.imgBtnPerfil);
        imgBtnNegocios = findViewById(R.id.imgBtnNegocios);
        imgBtnPedidos = findViewById(R.id.imgBtnPedidos);
        btnSalir = findViewById(R.id.btnSalir);
        chart = findViewById(R.id.chart);

        request = Volley.newRequestQueue(this);

        recuperarId();
        cargarDatos();

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

    void recuperarId() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        idVendedor = sharedPreferences.getInt("id", 0);
    }

    public void cargarDatos() {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/getGananciasVendedor.php?idVendedor="+idVendedor;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("Ganancias");
            ArrayList<BarEntry> ganancias = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                ganancias.add(new BarEntry(Float.valueOf(i), Float.valueOf(jsonObject.getString("ganancia"))));
            }

            BarDataSet barDataSet = new BarDataSet(ganancias, "Ganancias");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(16f);

            BarData barData = new BarData(barDataSet);

            chart.setFitBars(true);
            chart.setData(barData);
            chart.getDescription().setText("Gananacias Últimos 30 Días");
            chart.animateY(2000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
