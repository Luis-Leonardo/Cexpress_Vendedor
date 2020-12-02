package com.example.cexpress_vendedor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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

public class PedidoActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    TextView txtViewIdPedidoDetalle, txtViewFechaPedidoDetalle, txtViewNegocioPedidoDetalle, txtViewEstadoPedidoDetalle,
            txtViewCompradorPedidoDetalle, txtViewOrden;
    ImageButton imgBtnRegresar;

    int idPedido;
    String fecha, negocio, estado, comprador;
    ArrayList<String> nombres, fotos;
    ArrayList<Float> precios;
    ArrayList<Integer> cantidades;
    ProductosPedidoListAdapter adapter;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        Bundle bundle = getIntent().getBundleExtra("datos");
        idPedido = bundle.getInt("idPedido");
        fecha = bundle.getString("fecha");
        negocio = bundle.getString("negocio");
        estado = bundle.getString("estado");
        comprador = bundle.getString("comprador");

        txtViewIdPedidoDetalle = findViewById(R.id.txtViewIdPedidoDetalle);
        txtViewFechaPedidoDetalle = findViewById(R.id.txtViewFechaPedidoDetalle);
        txtViewNegocioPedidoDetalle = findViewById(R.id.txtViewNegocioPedidoDetalle);
        txtViewEstadoPedidoDetalle = findViewById(R.id.txtViewEstadoPedidoDetalle);
        txtViewCompradorPedidoDetalle = findViewById(R.id.txtViewCompradorPedidoDetalle);
        txtViewOrden = findViewById(R.id.txtViewOrden);
        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        txtViewIdPedidoDetalle.setText("No. " + idPedido);
        txtViewFechaPedidoDetalle.setText(fecha);
        txtViewNegocioPedidoDetalle.setText("Negocio: " + negocio);
        if(estado.equals("En preparación")) {
            txtViewEstadoPedidoDetalle.setTextColor(0xFFFF9800);
            txtViewEstadoPedidoDetalle.setText("Preparación");
        } else if (estado.equals("En camino")) {
            txtViewEstadoPedidoDetalle.setTextColor(0xE9ECAE18);
            txtViewEstadoPedidoDetalle.setText(estado);
        } else {
            txtViewEstadoPedidoDetalle.setTextColor(0xFFC66900);
            txtViewEstadoPedidoDetalle.setText(estado);
        }
        txtViewCompradorPedidoDetalle.setText("Comprador: " + comprador);

        imgBtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        request = Volley.newRequestQueue(this);

        cargarDatos();
    }

    void cargarDatos() {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/getProductosPedido.php?idPedido="+idPedido;

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
            JSONArray jsonArray = response.getJSONArray("ProductosPedido");
            nombres = new ArrayList<String>();
            fotos = new ArrayList<String>();
            cantidades = new ArrayList<Integer>();
            precios = new ArrayList<Float>();
            double total = 0.0;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                cantidades.add(jsonObject.getInt("cantidad"));
                nombres.add(jsonObject.getString("nombre"));
                fotos.add(jsonObject.getString("foto"));
                precios.add(Float.valueOf(jsonObject.getString("precio")));
                total += (cantidades.get(i)*precios.get(i));
            }

            txtViewOrden.setText("Artículos Pedidos ($"+total+")");
            adapter = new ProductosPedidoListAdapter(this, nombres, fotos, precios, cantidades);
            ListView listProductosPedido = findViewById(R.id.listProductosPedido);
            listProductosPedido.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
