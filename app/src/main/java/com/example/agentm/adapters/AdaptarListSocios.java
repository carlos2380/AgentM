package com.example.agentm.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.agentm.R;
import com.example.agentm.model.Socio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlo on 06/05/2017.
 */

public class AdaptarListSocios extends BaseAdapter {

    private LayoutInflater inflater;                       // Crea Layouts a partir del XML
    private TextView tvNombre, tvDni;
    private List<Socio> socios;

    public AdaptarListSocios(Context contexto, List<Socio> socioList) {
        socios = socioList;
        inflater = LayoutInflater.from(contexto);
    }


    @Override
    public int getCount() {
        return socios.size();
    }

    @Override
    public Object getItem(int i) {
        return socios.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        view = inflater.inflate(R.layout.list_socios, null);
        RelativeLayout relativeLayout;
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rowListSocios);
        tvNombre = (TextView) view.findViewById(R.id.nombreListSocio);
        tvDni = (TextView) view.findViewById(R.id.dniListSocio);

        if(i%2 == 1) {
            relativeLayout.setBackgroundColor(0x88EEEEEE);
        }
        if(!socios.get(i).getNombre().isEmpty() && !socios.get(i).getApellidos().isEmpty()) {
            tvNombre.setText(socios.get(i).getApellidos() + ", " + socios.get(i).getNombre());
        }else {
           tvNombre.setVisibility(View.GONE);
        }

        if(socios.get(i).getDni() != null) {
            tvDni.setText(socios.get(i).getDni());
        }else {
            tvDni.setVisibility(View.GONE);
        }

        return view;
    }
}
