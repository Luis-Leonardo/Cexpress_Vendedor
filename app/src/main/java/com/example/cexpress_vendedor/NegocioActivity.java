package com.example.cexpress_vendedor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class NegocioActivity extends AppCompatActivity {
    TextView txtViewNegocio;
    ImageButton imgBtnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negocio);

        Bundle bundle = getIntent().getBundleExtra("datos");
        int idNegocio = bundle.getInt("idNegocio");

        txtViewNegocio = findViewById(R.id.txtViewNegocio);
        txtViewNegocio.setText("Negocio #" + idNegocio);

        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);

        imgBtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
