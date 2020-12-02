package com.example.cexpress_vendedor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;

public class PedidoActivity extends AppCompatActivity {
    TextView txtViewIdPedidoDetalle, txtViewFechaPedidoDetalle, txtViewNegocioPedidoDetalle, txtViewEstadoPedidoDetalle, txtViewCompradorPedidoDetalle;
    ImageButton imgBtnRegresar;
    int idPedido;
    String fecha, negocio, estado, comprador;

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
        imgBtnRegresar = findViewById(R.id.imgBtnRegresar);

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
    }
}
