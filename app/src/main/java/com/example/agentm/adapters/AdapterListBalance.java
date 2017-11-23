package com.example.agentm.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.agentm.R;
import com.example.agentm.model.Registro;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by carlo on 09/05/2017.
 */

public class AdapterListBalance extends BaseAdapter {

    private LayoutInflater inflater;                       // Crea Layouts a partir del XML
    private TextView tvTitulo, tvFecha, tvPrecio, tvEruo;
    private List<Registro> registros;

    public AdapterListBalance(Context contexto, List<Registro> registrosList) {
        registros = registrosList;
        inflater = LayoutInflater.from(contexto);
    }
    @Override
    public int getCount() {
        return registros.size();
    }

    @Override
    public Object getItem(int i) {
        return registros.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.list_registros, null);
        RelativeLayout relativeLayout;
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rowListRegistros);

        tvTitulo = (TextView) view.findViewById(R.id.tvTituloRegistro);
        tvFecha = (TextView) view.findViewById(R.id.tvFechaRegistro);
        tvPrecio = (TextView) view.findViewById(R.id.tvPrecioRegistro);
        tvEruo = (TextView) view.findViewById(R.id.tvEuro);
        if(i%2 == 1) {
            relativeLayout.setBackgroundColor(0x88EEEEEE);
        }
        if(!registros.get(i).getTitulo().isEmpty()) {
            tvTitulo.setText(registros.get(i).getTitulo());
        }else {
            tvTitulo.setVisibility(View.GONE);
        }

        if(registros.get(i).getFecha() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Set your date format
            String fechaString = sdf.format(registros.get(i).getFecha());
            tvFecha.setText(fechaString);
        }else {
            tvFecha.setVisibility(View.GONE);
        }

        if(registros.get(i).getPrecio() < 0) {
            tvPrecio.setTextColor(Color.rgb(92,6,6));
            tvEruo.setTextColor(Color.rgb(92,6,6));
        }else {
            tvPrecio.setTextColor(Color.rgb(0,105,11));
            tvEruo.setTextColor(Color.rgb(0,105,11));
        }
        tvPrecio.setText(String.valueOf(registros.get(i).getPrecio()));

        return view;
    }
}
