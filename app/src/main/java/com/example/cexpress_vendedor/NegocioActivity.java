package com.example.cexpress_vendedor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NegocioActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    ArrayList<Integer> idProductos;
    ArrayList<String> nombres;
    ArrayList<String> fotos;
    ArrayList<String> cantidades;
    ProductosListAdapter adapter;
    int idVendedor, idNegocio;

    TextView txtViewNombreNegocio;
    ImageButton imgBtnRegresar;
    Button btnNuevoProducto;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negocio);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle bundle = getIntent().getBundleExtra("datos");
        idNegocio = bundle.getInt("idNegocio");

        recuperarId();

        txtViewNombreNegocio = findViewById(R.id.txtViewNombreNegocio);
        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);
        btnNuevoProducto = findViewById(R.id.btnNuevoProducto);

        request = Volley.newRequestQueue(this);

        txtViewNombreNegocio.setText(bundle.getString("nombre"));

        imgBtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNuevoProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cargarDatos();
    }

    void recuperarId() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        idVendedor = sharedPreferences.getInt("id", 0);
    }

    void  cargarDatos() {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/getProductosNegocio.php?idPuesto="+idNegocio;

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
            JSONArray jsonArray = response.getJSONArray("Productos");
            nombres = new ArrayList<String>();
            cantidades = new ArrayList<String>();
            idProductos = new ArrayList<Integer>();
            fotos = new ArrayList<String>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                idProductos.add(jsonObject.getInt("idProducto"));
                nombres.add(jsonObject.getString("nombre"));
                fotos.add(jsonObject.getString("foto"));
                String cantidad, unidad;
                cantidad = jsonObject.getString("cantidad");
                unidad = jsonObject.getString("unidad");
                cantidad = cantidad + " " + unidad;
                cantidades.add(cantidad);
            }

            adapter = new ProductosListAdapter(this, idProductos, nombres, fotos, cantidades);
            ListView listProductos = findViewById(R.id.listProductos);
            listProductos.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
