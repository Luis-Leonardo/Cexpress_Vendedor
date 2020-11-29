package com.example.cexpress_vendedor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NegociosListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Integer> idNegocios;
    ArrayList<String> nombres;
    ArrayList<String> mercados;
    ArrayList<String> fotos;
    private static LayoutInflater inflater = null;

    public NegociosListAdapter(Context context, ArrayList<Integer> idNegocios, ArrayList<String> nombres, ArrayList<String> fotos, ArrayList<String> mercados) {
        this.context = context;
        this.idNegocios = idNegocios;
        this.nombres = nombres;
        this. mercados = mercados;
        this.fotos = fotos;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return nombres.size();
    }

    @Override
    public Object getItem(int position) {
        return nombres.get(position);
    }

    @Override
    public long getItemId(int position) {
        return nombres.indexOf(getItem(position));
    }

    public  class Holder {
        TextView txtViewItemNombreNegocio;
        TextView txtViewItemMercadoNegocio;
        Button btnEditarItemNegocio;
        Button btnEliminarItemNegocio;
        ImageView imgItemNegocio;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View row;

        row = inflater.inflate(R.layout.negocio_item, null);
        holder.txtViewItemNombreNegocio = row.findViewById(R.id.txtViewItemNombreNegocio);
        holder.txtViewItemMercadoNegocio = row.findViewById(R.id.txtViewItemMercadoNegocio);
        holder.btnEditarItemNegocio = row.findViewById(R.id.btnEditarItemNegocio);
        holder.btnEliminarItemNegocio = row.findViewById(R.id.btnEliminarItemNegocio);
        holder.imgItemNegocio = row.findViewById(R.id.imgItemNegocio);

        holder.txtViewItemNombreNegocio.setText(nombres.get(position));
        holder.txtViewItemMercadoNegocio.setText(mercados.get(position));
        if(fotos.get(position)!="null") {
            String urlFoto = "https://appsmoviles2020.000webhostapp.com/imagenes/"+fotos.get(position);
            try {
                URL url = new URL(urlFoto);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                holder.imgItemNegocio.setImageBitmap(bitmap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        holder.btnEliminarItemNegocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idNegocios.remove(position);
                nombres.remove(position);
                mercados.remove(position);

                notifyDataSetChanged();
            }
        });

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, NegocioActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("idNegocio", idNegocios.get(position).intValue());
                i.putExtra("datos", bundle);
                context.startActivity(i);
            }
        });

        return row;
    }
}
