package com.example.agentm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.agentm.Patterns.Utils;
import com.example.agentm.R;
import com.example.agentm.model.Actividad;

import java.util.List;

/**
 * Created by carlo on 13/05/2017.
 */

public class AdapterListSocActs extends BaseAdapter {

    private LayoutInflater inflater;                       // Crea Layouts a partir del XML
    private TextView tvNombre, tvFecha;
    private List<Actividad> actividades;

    public AdapterListSocActs(Context contexto, List<Actividad> acts) {
        actividades = acts;
        inflater = LayoutInflater.from(contexto);
    }

    @Override
    public int getCount() {
        return actividades.size();
    }

    @Override
    public Object getItem(int i) {
        return actividades.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.list_socio_actividades, null);
        RelativeLayout relativeLayout;
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rlSocActs);
        tvNombre = (TextView) view.findViewById(R.id.nombreActividadSociAct);
        tvFecha = (TextView) view.findViewById(R.id.fechaActividadSociAct);

        if(i%2 == 1) {
            relativeLayout.setBackgroundColor(0x88EEEEEE);
        }
        tvNombre.setText(actividades.get(i).getNombre());
        tvFecha.setText(Utils.dateToString(actividades.get(i).getFecha()));
        return view;
    }
}
