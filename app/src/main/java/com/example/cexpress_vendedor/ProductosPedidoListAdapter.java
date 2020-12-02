package com.example.cexpress_vendedor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ProductosPedidoListAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> nombres, fotos;
    ArrayList<Float> precios;
    ArrayList<Integer> cantidades;
    private static LayoutInflater inflater = null;

    public ProductosPedidoListAdapter(Context context, ArrayList<String> nombres, ArrayList<String> fotos, ArrayList<Float> precios, ArrayList<Integer> cantidades) {
        this.context = context;
        this.nombres = nombres;
        this.fotos = fotos;
        this.precios = precios;
        this.cantidades = cantidades;
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

    public class Holder {
        TextView txtViewNombreProductoItem, txtViewCantidad, txtViewPrecio, txtViewSubtotal;
        ImageView imgProductoPedido;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View row;

        row = inflater.inflate(R.layout.producto_pedido_iten, null);
        holder.txtViewNombreProductoItem = row.findViewById(R.id.txtViewNombreProductoPedido);
        holder.txtViewCantidad = row.findViewById(R.id.txtViewCantidad);
        holder.txtViewPrecio = row.findViewById(R.id.txtViewPrecio);
        holder.txtViewSubtotal = row.findViewById(R.id.txtViewSubtotal);
        holder.imgProductoPedido = row.findViewById(R.id.imgProductoPedido);

        holder.txtViewNombreProductoItem.setText(nombres.get(position));
        holder.txtViewCantidad.setText("Cantidad: " + cantidades.get(position));
        holder.txtViewPrecio.setText("Precio: $" + precios.get(position));
        float subtotal = cantidades.get(position)*precios.get(position);
        holder.txtViewSubtotal.setText("Subtotal: $" + subtotal);
        if(fotos.get(position)!="null") {
            String urlFoto = "https://appsmoviles2020.000webhostapp.com/imagenes/"+fotos.get(position);
            try {
                URL url = new URL(urlFoto);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                holder.imgProductoPedido.setImageBitmap(bitmap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return row;
    }
}
