package com.example.cexpress_vendedor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class NuevoNegocioActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener  {
    TextInputLayout impNombreNegocio, impNumeroNegocio;
    EditText editNombreNegocio, editNumeroNegocio;
    EditText editHoraApertura, editHoraCierre;
    Spinner spinnerMercadoNegocio;
    ImageButton imgBtnFotoNegocio, imgBtnRegresar;
    Button btnRegistrarNegocio;

    ArrayList<String> mercados;
    ArrayList<Integer> idMercados;
    Boolean nombre = false, horaApertura = false, horaCierre = false;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_negocio);

        impNombreNegocio = findViewById(R.id.impNombreNegocio);
        impNumeroNegocio = findViewById(R.id.impNumeroNegocio);

        editNombreNegocio = findViewById(R.id.editNombreNegocio);
        editNumeroNegocio = findViewById(R.id.editNumeroNegocio);

        editHoraApertura = findViewById(R.id.editHoraApertura);
        editHoraCierre = findViewById(R.id.editHoraCierre);

        spinnerMercadoNegocio = findViewById(R.id.spinnerMercadoNegocio);
        imgBtnFotoNegocio = findViewById(R.id.imgBtnFotoNegocio);
        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);
        btnRegistrarNegocio = findViewById(R.id.btnRegistrarNegocio);

        request = Volley.newRequestQueue(this);
        mercados = new ArrayList<>();
        idMercados = new ArrayList<>();

        cargarMercados();

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
                        if (hourOfDay>=12&&hourOfDay<24) {
                            periodo = "PM";
                            hora = String.valueOf(hourOfDay-12);
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
                        if (hourOfDay>=12&&hourOfDay<24) {
                            periodo = "PM";
                            hora = String.valueOf(hourOfDay-12);
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
                    startActivityForResult(intent.createChooser(intent, "Selecciona una opción"), 20);
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

        switch (requestCode) {
            case 10:
                Uri miPath=data.getData();
                imgBtnFotoNegocio.setImageURI(miPath);
                break;
            case 20:

                break;
        }
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
            horaApertura = false;
        } else {
            horaApertura = true;
        }
        if(editHoraCierre.getText().toString().isEmpty()) {
            horaApertura = false;
        } else {
            horaApertura = true;
        }

        if(nombre && horaApertura && horaCierre) {
            //Llamar funcion registro
        }
    }
}
