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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditarProductoActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    TextInputLayout impNombreProductoEditar, impCantidadProductoEditar, impPrecioProductoEditar;
    EditText editNombreProductoEditar, editCantidadProductoEditar, editPrecioProductoEditar;
    Spinner spinnerUnidadProductoEditar;
    ImageButton imgBtnFotoProductoEditar, imgBtnRegresar;
    Button btnModificarProducto;
    Switch switchDisponibilidadProducto;

    ArrayList<String> unidades;
    ArrayList<Integer> idUnidades;
    Boolean nombre = false, cantidad = false, precio = false;
    int idVendedor, idProducto, idUnidad = 1, disponibilidad;
    String encodedImage, nuevoNombre, nuevaCantidad, nuevoPrecio;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);

        impNombreProductoEditar = findViewById(R.id.impNombreProductoEditar);
        impCantidadProductoEditar = findViewById(R.id.impCantidadProductoEditar);
        impPrecioProductoEditar = findViewById(R.id.impPrecioProductoEditar);
        editNombreProductoEditar = findViewById(R.id.editNombreProductoEditar);
        editCantidadProductoEditar = findViewById(R.id.editCantidadProductoEditar);
        editPrecioProductoEditar = findViewById(R.id.editPrecioProductoEditar);
        spinnerUnidadProductoEditar = findViewById(R.id.spinnerUnidadProductoEditar);
        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);
        imgBtnFotoProductoEditar = findViewById(R.id.imgBtnFotoProductoEditar);
        btnModificarProducto = findViewById(R.id.btnModificarProducto);
        switchDisponibilidadProducto = findViewById(R.id.switchDisponibilidadProducto);

        request = Volley.newRequestQueue(this);
        unidades = new ArrayList<>();
        idUnidades = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle bundle = getIntent().getBundleExtra("datos");
        idProducto = bundle.getInt("idProducto");

        recuperarId();
        cargarUnidades();
        obtenerProducto();

        imgBtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgBtnFotoProductoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpcionesFoto();
            }
        });

        spinnerUnidadProductoEditar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idUnidad = idUnidades.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnModificarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });

        switchDisponibilidadProducto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    disponibilidad = 1;
                } else {
                    disponibilidad = 0;
                }
            }
        });
    }

    private void mostrarOpcionesFoto() {
        final CharSequence[] opciones={"Tomar Foto", "Elegir foto de la galería", "Cancelar"};
        final AlertDialog.Builder opc= new AlertDialog.Builder(EditarProductoActivity.this);
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
                        imgBtnFotoProductoEditar.setImageBitmap(bitmap);

                        guardarImagen(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case 20:
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imgBtnFotoProductoEditar.setImageBitmap(bitmap);
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

    void obtenerProducto() {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/getProducto.php?idProducto="+idProducto;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("Producto");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    //Nombre, Cantidad y Precio
                    editNombreProductoEditar.setText(jsonObject.getString("nombre"));
                    editCantidadProductoEditar.setText(jsonObject.getString("cantidad"));
                    editPrecioProductoEditar.setText(jsonObject.getString("precio"));
                    //Foto
                    if(jsonObject.getString("foto")!="null") {
                        String urlFoto = "https://appsmoviles2020.000webhostapp.com/imagenes/"+jsonObject.getString("foto");
                        java.net.URL url = new URL(urlFoto);
                        Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        imgBtnFotoProductoEditar.setImageBitmap(bitmap);
                        guardarImagen(bitmap);
                    }
                    //Selects
                    spinnerUnidadProductoEditar.setSelection(idUnidades.indexOf(jsonObject.getInt("idUnidad")));
                    //Switch
                    if(jsonObject.getInt("disponibilidad")==1){
                        switchDisponibilidadProducto.setChecked(true);
                        disponibilidad = 1;
                    } else {
                        switchDisponibilidadProducto.setChecked(false);
                        disponibilidad = 2;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditarProductoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
                spinnerUnidadProductoEditar.setAdapter(adapter);
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
        if(editNombreProductoEditar.getText().toString().isEmpty()) {
            impNombreProductoEditar.setError("Campo de Nombre está vacío");
            nombre = false;
        } else {
            impNombreProductoEditar.setError(null);
            nombre = true;
        }
        //Validaciones cantidad
        if(editCantidadProductoEditar.getText().toString().isEmpty()) {
            impCantidadProductoEditar.setError("Campo de Cantidad está vacío");
            cantidad = false;
        } else {
            if(Integer.valueOf(editCantidadProductoEditar.getText().toString())==0) {
                impCantidadProductoEditar.setError("La cantidad debe ser mayor a 0");
                cantidad = false;
            } else {
                impCantidadProductoEditar.setError(null);
                cantidad = true;
            }
        }
        //Validaciones precio
        if(editPrecioProductoEditar.getText().toString().isEmpty()) {
            impPrecioProductoEditar.setError("Campo de Nombre está vacío");
            precio = false;
        } else {
            if(Float.valueOf(editPrecioProductoEditar.getText().toString())==0) {
                impPrecioProductoEditar.setError("El precio debe ser mayor a 0");
                precio = false;
            } else {
                impPrecioProductoEditar.setError(null);
                precio = true;
            }
        }

        if(nombre && cantidad && precio) {
            //Llamar funcion registro
            nuevoNombre = editNombreProductoEditar.getText().toString();
            nuevaCantidad = editCantidadProductoEditar.getText().toString();
            nuevoPrecio = editPrecioProductoEditar.getText().toString();

            actualizarProducto(nuevoNombre, nuevaCantidad, nuevoPrecio);
        }
    }

    void actualizarProducto(final String nombre, final String cantidad, final String precio) {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/actualizarProducto.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(EditarProductoActivity.this, "Datos Guardados", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(NuevoNegocioActivity.this, NegociosActivity.class);
                //startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditarProductoActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("cantidad", cantidad);
                params.put("precio", precio);
                params.put("foto", encodedImage);
                params.put("idProducto", String.valueOf(idProducto));
                params.put("idUnidad", String.valueOf(idUnidad));
                params.put("disponibilidad", String.valueOf(disponibilidad));

                return params;
            }
        };
        request.add(stringRequest);
    }
}
