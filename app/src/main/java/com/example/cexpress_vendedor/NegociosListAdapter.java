package com.example.cexpress_vendedor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public class Holder {
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

        holder.btnEditarItemNegocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditarNegocioActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("idNegocio", idNegocios.get(position).intValue());
                intent.putExtra("datos", bundle);
                context.startActivity(intent);
            }
        });

        holder.btnEliminarItemNegocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("¿Seguro que desea eliminar el negocio?").setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String URL = "https://appsmoviles2020.000webhostapp.com/vendedor/eliminarNegocio.php";

                        RequestQueue request = Volley.newRequestQueue(context);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println(response);
                                Toast.makeText(context, "Negocio Eliminado", Toast.LENGTH_SHORT).show();
                                idNegocios.remove(position);
                                nombres.remove(position);
                                mercados.remove(position);
                                fotos.remove(position);
                                notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("idNegocio", String.valueOf(idNegocios.get(position)));

                                return params;
                            }
                        };
                        request.add(stringRequest);
                    }
                });
                dialog.create();
                dialog.show();
            }
        });

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, NegocioActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("idNegocio", idNegocios.get(position).intValue());
                bundle.putString("nombre", nombres.get(position));
                i.putExtra("datos", bundle);
                context.startActivity(i);
            }
        });

        return row;
    }
}
