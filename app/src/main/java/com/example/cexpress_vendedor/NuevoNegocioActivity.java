package com.example.cexpress_vendedor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NuevoNegocioActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener  {
    TextInputLayout impNombreNegocio, impNumeroNegocio, impHoraApertura, impHoraCierre;
    EditText editNombreNegocio, editNumeroNegocio;
    EditText editHoraApertura, editHoraCierre;
    Spinner spinnerMercadoNegocio, spinnerCategoriaNegocio;
    ImageButton imgBtnFotoNegocio, imgBtnRegresar;
    Button btnRegistrarNegocio;

    ArrayList<String> mercados, categorias;
    ArrayList<Integer> idMercados, idCategorias;
    Boolean nombre = false, horaApertura = false, horaCierre = false;
    int idVendedor, idMercado=1, idCategoria=1;
    String encodedImage, nuevoNombre, nuevoNumero, nuevaHoraApertura, nuevaHoraCierre;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_negocio);

        impNombreNegocio = findViewById(R.id.impNombreNegocio);
        impNumeroNegocio = findViewById(R.id.impNumeroNegocio);
        impHoraApertura = findViewById(R.id.impHoraApertura);
        impHoraCierre = findViewById(R.id.impHoraCierre);
        editNombreNegocio = findViewById(R.id.editNombreNegocio);
        editNumeroNegocio = findViewById(R.id.editNumeroNegocio);
        editHoraApertura = findViewById(R.id.editHoraApertura);
        editHoraCierre = findViewById(R.id.editHoraCierre);
        spinnerMercadoNegocio = findViewById(R.id.spinnerMercadoNegocio);
        spinnerCategoriaNegocio = findViewById(R.id.spinnerCategoriaNegocio);
        imgBtnFotoNegocio = findViewById(R.id.imgBtnFotoNegocio);
        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);
        btnRegistrarNegocio = findViewById(R.id.btnRegistrarNegocio);

        request = Volley.newRequestQueue(this);
        mercados = new ArrayList<>();
        idMercados = new ArrayList<>();
        categorias = new ArrayList<>();
        idCategorias = new ArrayList<>();

        recuperarId();
        cargarMercados();
        cargarCategorias();

        imgBtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgBtnFotoNegocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpcionesFoto();
            }
        });

        spinnerMercadoNegocio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idMercado = idMercados.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCategoriaNegocio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idCategoria = idCategorias.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editHoraApertura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(NuevoNegocioActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hora, minutos, periodo;

                        if(hourOfDay < 10) {
                            hora = String.valueOf(0)+hourOfDay;
                        } else {
                            hora = String.valueOf(hourOfDay);
                        }
                        if(minute < 10) {
                            minutos = String.valueOf(0)+minute;
                        } else {
                            minutos = String.valueOf(minute);
                        }
                        nuevaHoraApertura=hora+":"+minutos+":00";
                        if (hourOfDay>=12&&hourOfDay<24) {
                            periodo = "PM";
                            if(hourOfDay>12) {
                                hora = String.valueOf(hourOfDay-12);
                            }
                        } else {
                            periodo = "AM";
                        }
                        editHoraApertura.setText(hora + ":" + minutos + " " + periodo);
                    }
                }, hour, minute, false);

                timePickerDialog.setTitle("Hora de Apertura");
                timePickerDialog.setCanceledOnTouchOutside(true);
                timePickerDialog.show();
            }
        });

        editHoraCierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(NuevoNegocioActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hora, minutos, periodo;

                        if(hourOfDay < 10) {
                            hora = String.valueOf(0)+hourOfDay;
                        } else {
                            hora = String.valueOf(hourOfDay);
                        }
                        if(minute < 10) {
                            minutos = String.valueOf(0)+minute;
                        } else {
                            minutos = String.valueOf(minute);
                        }
                        nuevaHoraCierre=hora+":"+minutos+":00";
                        if (hourOfDay>=12&&hourOfDay<24) {
                            periodo = "PM";
                            if(hourOfDay>12) {
                                hora = String.valueOf(hourOfDay-12);
                            }
                        } else {
                            periodo = "AM";
                        }
                        editHoraCierre.setText(hora + ":" + minutos + " " + periodo);
                    }
                }, hour, minute, false);

                timePickerDialog.setTitle("Hora de Cierre");
                timePickerDialog.setCanceledOnTouchOutside(true);
                timePickerDialog.show();
            }
        });
        
        btnRegistrarNegocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
    }

    private void mostrarOpcionesFoto() {
        final CharSequence[] opciones={"Tomar Foto", "Elegir foto de la galería", "Cancelar"};
        final AlertDialog.Builder opc= new AlertDialog.Builder(NuevoNegocioActivity.this);
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
                        imgBtnFotoNegocio.setImageBitmap(bitmap);

                        guardarImagen(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case 20:
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imgBtnFotoNegocio.setImageBitmap(bitmap);
                    guardarImagen(bitmap);
                    break;
            }
        } else {

        }
    }

    void cargarCategorias() {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/getCategorias.php?";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("Categorias");

                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        categorias.add(jsonObject.getString("nombre"));
                        idCategorias.add(jsonObject.getInt("idCategoria"));

                        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(NuevoNegocioActivity.this, android.R.layout.simple_spinner_item, categorias);
                        spinnerCategoriaNegocio.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NuevoNegocioActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        request.add(jsonObjectRequest);
    }

    void cargarMercados() {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/getMercados.php?";

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
            JSONArray jsonArray = response.getJSONArray("Mercados");

            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                mercados.add(jsonObject.getString("nombre"));
                idMercados.add(jsonObject.getInt("idMercado"));

                ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mercados);
                spinnerMercadoNegocio.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void validarDatos() {
        //Validaciones nombre
        if(editNombreNegocio.getText().toString().isEmpty()) {
            impNombreNegocio.setError("Campo de Nombre está vacío");
            nombre = false;
        } else {
            impNombreNegocio.setError(null);
            nombre = true;
        }
        //Validaciones hora
        if(editHoraApertura.getText().toString().isEmpty()) {
            impHoraApertura.setError("Hora de Apertura no seleccionada");
            horaApertura = false;
        } else {
            impHoraApertura.setError(null);
            horaApertura = true;
        }
        if(editHoraCierre.getText().toString().isEmpty()) {
            impHoraCierre.setError("Hora de Cierre no seleccionada");
            horaCierre = false;
        } else {
            impHoraCierre.setError(null);
            horaCierre = true;
        }

        if(nombre && horaApertura && horaCierre) {
            //Llamar funcion registro
            nuevoNombre = editNombreNegocio.getText().toString();
            nuevoNumero = editNumeroNegocio.getText().toString();

            registrarNegocio(nuevoNombre, nuevoNumero, nuevaHoraApertura, nuevaHoraCierre);
        }
    }

    void recuperarId() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        idVendedor = sharedPreferences.getInt("id", 0);
    }

    void guardarImagen(Bitmap imagen) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    void registrarNegocio(final String nombre, final String numero, final String horaApertura, final String horaCierre) {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/registrarNegocio.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(NuevoNegocioActivity.this, "Nuevo Negocio Registrado", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(NuevoNegocioActivity.this, NegociosActivity.class);
                //startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NuevoNegocioActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idVendedor", String.valueOf(idVendedor));
                params.put("nombre", nombre);
                params.put("numero", numero);
                params.put("horaApertura", horaApertura);
                params.put("horaCierre", horaCierre);
                params.put("foto", encodedImage);
                params.put("idMercado", String.valueOf(idMercado));
                params.put("idCategoria", String.valueOf(idCategoria));

                return params;
            }
        };
        request.add(stringRequest);
    }
}
