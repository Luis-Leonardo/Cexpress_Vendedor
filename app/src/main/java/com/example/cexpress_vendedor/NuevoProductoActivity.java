package com.example.cexpress_vendedor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NuevoProductoActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    TextInputLayout impNombreProducto, impCantidadProducto, impPrecioProducto;
    EditText editNombreProducto, editCantidadProducto, editPrecioProducto;
    Spinner spinnerUnidadProducto;
    ImageButton imgBtnFotoProducto, imgBtnRegresar;
    Button btnRegistrarProducto;

    ArrayList<String> unidades;
    ArrayList<Integer> idUnidades;
    Boolean nombre = false, cantidad = false, precio = false;
    int idVendedor, idNegocio, idUnidad = 1;
    String encodedImage, nuevoNombre, nuevaCantidad, nuevoPrecio;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_producto);

        impNombreProducto = findViewById(R.id.impNombreProducto);
        impCantidadProducto = findViewById(R.id.impCantidadProducto);
        impPrecioProducto = findViewById(R.id.impPrecioProducto);
        editNombreProducto = findViewById(R.id.editNombreProducto);
        editCantidadProducto = findViewById(R.id.editCantidadProducto);
        editPrecioProducto = findViewById(R.id.editPrecioProducto);
        spinnerUnidadProducto = findViewById(R.id.spinnerUnidadProducto);
        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);
        imgBtnFotoProducto = findViewById(R.id.imgBtnFotoProducto);
        btnRegistrarProducto = findViewById(R.id.btnRegistrarProducto);

        request = Volley.newRequestQueue(this);
        unidades = new ArrayList<>();
        idUnidades = new ArrayList<>();

        Bundle bundle = getIntent().getBundleExtra("datos");
        idNegocio = bundle.getInt("idNegocio");

        recuperarId();
        cargarUnidades();

        imgBtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgBtnFotoProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpcionesFoto();
            }
        });

        spinnerUnidadProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idUnidad = idUnidades.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRegistrarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
    }

    private void mostrarOpcionesFoto() {
        final CharSequence[] opciones={"Tomar Foto", "Elegir foto de la galería", "Cancelar"};
        final AlertDialog.Builder opc= new AlertDialog.Builder(NuevoProductoActivity.this);
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
                        imgBtnFotoProducto.setImageBitmap(bitmap);

                        guardarImagen(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case 20:
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imgBtnFotoProducto.setImageBitmap(bitmap);
                    guardarImagen(bitmap);
                    break;
            }
        } else {

        }
    }

    void recuperarId() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        idVendedor = sharedPreferences.getInt("id", 0);
    }

    void cargarUnidades() {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/getUnidades.php?";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("Unidades");

            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                unidades.add(jsonObject.getString("nombre"));
                idUnidades.add(jsonObject.getInt("idUnidad"));

                ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, unidades);
                spinnerUnidadProducto.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void guardarImagen(Bitmap imagen) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    void validarDatos() {
        //Validaciones nombre
        if(editNombreProducto.getText().toString().isEmpty()) {
            impNombreProducto.setError("Campo de Nombre está vacío");
            nombre = false;
        } else {
            impNombreProducto.setError(null);
            nombre = true;
        }
        //Validaciones cantidad
        if(editCantidadProducto.getText().toString().isEmpty()) {
            impCantidadProducto.setError("Campo de Cantidad está vacío");
            cantidad = false;
        } else {
            if(Integer.valueOf(editCantidadProducto.getText().toString())==0) {
                impCantidadProducto.setError("La cantidad debe ser mayor a 0");
                cantidad = false;
            } else {
                impCantidadProducto.setError(null);
                cantidad = true;
            }
        }
        //Validaciones precio
        if(editPrecioProducto.getText().toString().isEmpty()) {
            impPrecioProducto.setError("Campo de Nombre está vacío");
            precio = false;
        } else {
            if(Float.valueOf(editPrecioProducto.getText().toString())==0) {
                impPrecioProducto.setError("El precio debe ser mayor a 0");
                precio = false;
            } else {
                impPrecioProducto.setError(null);
                precio = true;
            }
        }

        if(nombre && cantidad && precio) {
            //Llamar funcion registro
            nuevoNombre = editNombreProducto.getText().toString();
            nuevaCantidad = editCantidadProducto.getText().toString();
            nuevoPrecio = editPrecioProducto.getText().toString();

            registrarProducto(nuevoNombre, nuevaCantidad, nuevoPrecio);
        }
    }

    void registrarProducto(final String nombre, final String cantidad, final String precio) {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/registrarProducto.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(NuevoProductoActivity.this, "Nuevo Producto Registrado", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(NuevoNegocioActivity.this, NegociosActivity.class);
                //startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NuevoProductoActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("cantidad", cantidad);
                params.put("precio", precio);
                params.put("foto", encodedImage);
                params.put("idNegocio", String.valueOf(idNegocio));
                params.put("idUnidad", String.valueOf(idUnidad));

                return params;
            }
        };
        request.add(stringRequest);
    }
}
