package com.example.cexpress_vendedor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PedidosActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{
    ArrayList<Integer> idPedidos;
    ArrayList<String> fechas, negocios, estados, compradores;
    PedidosListAdapter adapter;
    int idVendedor;

    ImageButton imgBtnRegresar;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        recuperarId();

        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);

        request = Volley.newRequestQueue(this);

        imgBtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cargarDatos();
    }

    void  cargarDatos() {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/getPedidosVendedor.php?idVendedor="+idVendedor;

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
            JSONArray jsonArray = response.getJSONArray("Pedidos");
            fechas = new ArrayList<String>();
            negocios = new ArrayList<String>();
            idPedidos = new ArrayList<Integer>();
            estados = new ArrayList<String>();
            compradores = new ArrayList<String>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                idPedidos.add(jsonObject.getInt("idPedido"));
                fechas.add(jsonObject.getString("fecha"));
                negocios.add(jsonObject.getString("negocio"));
                estados.add(jsonObject.getString("estado"));
                compradores.add(jsonObject.getString("comprador"));
            }

            adapter = new PedidosListAdapter(this, idPedidos, fechas, negocios, compradores, estados);
            ListView listPedidos = findViewById(R.id.listPedidos);
            listPedidos.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void recuperarId() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        idVendedor = sharedPreferences.getInt("id", 0);
    }
}
