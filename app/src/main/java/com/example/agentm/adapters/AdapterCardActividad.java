package com.example.agentm.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.agentm.R;
import com.example.agentm.model.Actividad;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.agentm.Patterns.Utils.intMonthToString;

/**
 * Created by carlo on 12/05/2017.
 */

public class AdapterCardActividad extends RecyclerView.Adapter<AdapterCardActividad.ViewHolder>{

    private List<Actividad> actividades;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView dia, hora, mes, year, titulo;
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
            dia = (TextView) v.findViewById(R.id.diaAct);
            hora = (TextView) v.findViewById(R.id.horaAct);
            mes = (TextView) v.findViewById(R.id.mesAct);
            year = (TextView) v.findViewById(R.id.yearAct);
            titulo = (TextView) v.findViewById(R.id.tituloAct);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterCardActividad(List<Actividad> avtivs) {
        actividades = avtivs;
    }

    @Override
    public int getItemCount() {
        return actividades.size();
    }
    // Create new views (invoked by the layout manager)
    @Override
    public AdapterCardActividad.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_actividad, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Actividad actividad = actividades.get(position);
        Date date = actividad.getFecha(); // your date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        DateFormat df = new SimpleDateFormat("HH:mm");
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        holder.dia.setText(String.valueOf(day));
        holder.year.setText(String.valueOf(year));
        holder.titulo.setText(actividad.getNombre());
        holder.mes.setText(intMonthToString(month));
        holder.hora.setText(df.format(date));
    }

}
