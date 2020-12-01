package com.example.cexpress_vendedor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PedidosListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Integer> idPedidos;
    ArrayList<String> fechas, negocios, compradores, estados;
    private static LayoutInflater inflater = null;

    public PedidosListAdapter(Context context, ArrayList<Integer> idPedidos, ArrayList<String> fechas, ArrayList<String> negocios, ArrayList<String> compradores, ArrayList<String> estados) {
        this.context = context;
        this.idPedidos = idPedidos;
        this.fechas = fechas;
        this.negocios = negocios;
        this.compradores = compradores;
        this.estados = estados;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return idPedidos.size();
    }

    @Override
    public Object getItem(int position) {
        return idPedidos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return idPedidos.indexOf(getItem(position));
    }

    public class  Holder {
        TextView txtViewIdPedido, txtViewFechaPedido, txtViewNegocioPedido, txtViewEstadoPedido, txtViewCompradorPedido;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View row;

        row = inflater.inflate(R.layout.pedido_item, null);
        holder.txtViewIdPedido = row.findViewById(R.id.txtViewIdPedido);
        holder.txtViewFechaPedido = row.findViewById(R.id.txtViewFechaPedido);
        holder.txtViewNegocioPedido = row.findViewById(R.id.txtViewNegocioPedido);
        holder.txtViewEstadoPedido = row.findViewById(R.id.txtViewEstadoPedido);
        holder.txtViewCompradorPedido = row.findViewById(R.id.txtViewCompradorPedido);

        holder.txtViewIdPedido.setText("No. " + idPedidos.get(position));
        String mes, dia, year;
        dia = fechas.get(position).substring(8, 10);
        mes = fechas.get(position).substring(5, 7);
        year = fechas.get(position).substring(0, 4);
        holder.txtViewFechaPedido.setText(dia+"/"+mes+"/"+year);
        holder.txtViewNegocioPedido.setText("Negocio: " + negocios.get(position));
        if(estados.get(position).equals("En preparación")) {
            holder.txtViewEstadoPedido.setTextColor(0xFFFF9800);
            holder.txtViewEstadoPedido.setText("Preparación");
        } else if (estados.get(position).equals("En camino")) {
            holder.txtViewEstadoPedido.setTextColor(0xE9ECAE18);
            holder.txtViewEstadoPedido.setText(estados.get(position));
        } else {
            holder.txtViewEstadoPedido.setTextColor(0xFFC66900);
            holder.txtViewEstadoPedido.setText(estados.get(position));
        }
        holder.txtViewCompradorPedido.setText("Comprador: " + compradores.get(position));

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(context, NegocioActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("idNegocio", idNegocios.get(position).intValue());
                bundle.putString("nombre", nombres.get(position));
                i.putExtra("datos", bundle);
                context.startActivity(i);*/
            }
        });

        return row;
    }
}
