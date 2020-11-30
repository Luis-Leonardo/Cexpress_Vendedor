package com.example.cexpress_vendedor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.TimePickerDialog;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditarNegocioActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener  {
    TextInputLayout impNombreNegocioEditar, impNumeroNegocioEditar, impHoraAperturaEditar, impHoraCierreEditar;
    EditText editNombreNegocioEditar, editNumeroNegocioEditar, editHoraAperturaEditar, editHoraCierreEditar;
    Spinner spinnerMercadoNegocioEditar, spinnerCategoriaNegocioEditar;
    ImageButton imgBtnFotoNegocioEditar, imgBtnRegresar;
    Button btnModificarNegocio;

    ArrayList<String> mercados, categorias;
    ArrayList<Integer> idMercados, idCategorias;
    Boolean nombre = false, horaApertura = false, horaCierre = false;
    int idPuesto, idVendedor, idMercado=1, idCategoria=1;
    String encodedImage, nuevoNombre, nuevoNumero, nuevaHoraApertura, nuevaHoraCierre;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_negocio);

        impNombreNegocioEditar = findViewById(R.id.impNombreNegocioEditar);
        impNumeroNegocioEditar = findViewById(R.id.impNumeroNegocioEditar);
        impHoraAperturaEditar = findViewById(R.id.impHoraAperturaEditar);
        impHoraCierreEditar = findViewById(R.id.impHoraCierreEditar);
        editNombreNegocioEditar = findViewById(R.id.editNombreNegocioEditar);
        editNumeroNegocioEditar = findViewById(R.id.editNumeroNegocioEditar);
        editHoraAperturaEditar = findViewById(R.id.editHoraAperturaEditar);
        editHoraCierreEditar = findViewById(R.id.editHoraCierreEditar);
        spinnerMercadoNegocioEditar = findViewById(R.id.spinnerMercadoNegocioEditar);
        spinnerCategoriaNegocioEditar = findViewById(R.id.spinnerCategoriaNegocioEditar);
        imgBtnFotoNegocioEditar = findViewById(R.id.imgBtnFotoNegocioEditar);
        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);
        btnModificarNegocio = findViewById(R.id.btnModificarNegocio);

        request = Volley.newRequestQueue(this);
        mercados = new ArrayList<>();
        idMercados = new ArrayList<>();
        categorias = new ArrayList<>();
        idCategorias = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle bundle = getIntent().getBundleExtra("datos");
        idPuesto = bundle.getInt("idNegocio");

        recuperarId();
        cargarMercados();
        cargarCategorias();
        obtenerPuesto();

        imgBtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgBtnFotoNegocioEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpcionesFoto();
            }
        });

        spinnerMercadoNegocioEditar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idMercado = idMercados.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCategoriaNegocioEditar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idCategoria = idCategorias.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editHoraAperturaEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(EditarNegocioActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        editHoraAperturaEditar.setText(hora + ":" + minutos + " " + periodo);
                    }
                }, hour, minute, false);

                timePickerDialog.setTitle("Hora de Apertura");
                timePickerDialog.setCanceledOnTouchOutside(true);
                timePickerDialog.show();
            }
        });

        editHoraCierreEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(EditarNegocioActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        editHoraCierreEditar.setText(hora + ":" + minutos + " " + periodo);
                    }
                }, hour, minute, false);

                timePickerDialog.setTitle("Hora de Cierre");
                timePickerDialog.setCanceledOnTouchOutside(true);
                timePickerDialog.show();
            }
        });

        btnModificarNegocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
    }

    private void mostrarOpcionesFoto() {
        final CharSequence[] opciones={"Tomar Foto", "Elegir foto de la galería", "Cancelar"};
        final AlertDialog.Builder opc= new AlertDialog.Builder(EditarNegocioActivity.this);
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
                        imgBtnFotoNegocioEditar.setImageBitmap(bitmap);

                        guardarImagen(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case 20:
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imgBtnFotoNegocioEditar.setImageBitmap(bitmap);
                    guardarImagen(bitmap);
                    break;
            }
        } else {

        }
    }

    void guardarImagen(Bitmap imagen) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    void obtenerPuesto() {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/getNegocio.php?idPuesto="+idPuesto;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("Negocio");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    //Nombre y numero
                    editNombreNegocioEditar.setText(jsonObject.getString("nombre"));
                    if(jsonObject.getString("numero")!="null") {
                        editNumeroNegocioEditar.setText(jsonObject.getString("numero"));
                    }
                    //Horas
                    String hora, minutos, periodo, horaAperturaBD, horaCierreBD;
                    hora = jsonObject.getString("horaApertura").substring(0, 2);
                    minutos = jsonObject.getString("horaApertura").substring(3, 5);
                    nuevaHoraApertura=hora+":"+minutos+":00";
                    if(Integer.valueOf(hora)>=12) {
                        periodo = "PM";
                        int horaAdecuada = Integer.valueOf(hora)-12;
                        if(horaAdecuada<10) {
                            hora = 0+String.valueOf(horaAdecuada);
                        } else {
                            hora = String.valueOf(horaAdecuada);
                        }
                    } else {
                        periodo = "AM";
                    }
                    horaAperturaBD=hora+":"+minutos+" "+periodo;
                    editHoraAperturaEditar.setText(horaAperturaBD);
                    hora = jsonObject.getString("horaCierre").substring(0, 2);
                    minutos = jsonObject.getString("horaCierre").substring(3, 5);
                    nuevaHoraCierre=hora+":"+minutos+":00";
                    if(Integer.valueOf(hora)>=12) {
                        periodo = "PM";
                        int horaAdecuada = Integer.valueOf(hora)-12;
                        if(horaAdecuada<10) {
                            hora = 0+String.valueOf(horaAdecuada);
                        } else {
                            hora = String.valueOf(horaAdecuada);
                        }
                    } else {
                        periodo = "AM";
                    }
                    horaCierreBD=hora+":"+minutos+" "+periodo;
                    editHoraCierreEditar.setText(horaCierreBD);
                    //Foto
                    if(jsonObject.getString("foto")!="null") {
                        String urlFoto = "https://appsmoviles2020.000webhostapp.com/imagenes/"+jsonObject.getString("foto");
                        java.net.URL url = new URL(urlFoto);
                        Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        imgBtnFotoNegocioEditar.setImageBitmap(bitmap);
                        guardarImagen(bitmap);
                    }
                    //Selects
                    spinnerMercadoNegocioEditar.setSelection(idMercados.indexOf(jsonObject.getInt("idMercado")));
                    spinnerCategoriaNegocioEditar.setSelection(idCategorias.indexOf(jsonObject.getInt("idCategoria")));
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
                Toast.makeText(EditarNegocioActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        request.add(jsonObjectRequest);
    }

    void recuperarId() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        idVendedor = sharedPreferences.getInt("id", 0);
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
                spinnerMercadoNegocioEditar.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

                        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(EditarNegocioActivity.this, android.R.layout.simple_spinner_item, categorias);
                        spinnerCategoriaNegocioEditar.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditarNegocioActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        request.add(jsonObjectRequest);
    }

    void validarDatos() {
        //Validaciones nombre
        if(editNombreNegocioEditar.getText().toString().isEmpty()) {
            impNombreNegocioEditar.setError("Campo de Nombre está vacío");
            nombre = false;
        } else {
            impNombreNegocioEditar.setError(null);
            nombre = true;
        }
        //Validaciones hora
        if(editHoraAperturaEditar.getText().toString().isEmpty()) {
            impHoraAperturaEditar.setError("Hora de Apertura no seleccionada");
            horaApertura = false;
        } else {
            impHoraAperturaEditar.setError(null);
            horaApertura = true;
        }
        if(editHoraCierreEditar.getText().toString().isEmpty()) {
            impHoraCierreEditar.setError("Hora de Cierre no seleccionada");
            horaCierre = false;
        } else {
            impHoraCierreEditar.setError(null);
            horaCierre = true;
        }

        if(nombre && horaApertura && horaCierre) {
            //Llamar funcion registro
            nuevoNombre = editNombreNegocioEditar.getText().toString();
            nuevoNumero = editNumeroNegocioEditar.getText().toString();

            actualizarNegocio(nuevoNombre, nuevoNumero, nuevaHoraApertura, nuevaHoraCierre);
        }
    }

    void actualizarNegocio(final String nombre, final String numero, final String horaApertura, final String horaCierre) {
        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/actualizarNegocio.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(EditarNegocioActivity.this, "Cambios Guardados", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(EditarNegocioActivity.this, NegociosActivity.class);
                //startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditarNegocioActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idPuesto", String.valueOf(idPuesto));
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
