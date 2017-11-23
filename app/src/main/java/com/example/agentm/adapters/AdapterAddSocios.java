package com.example.agentm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.agentm.R;
import com.example.agentm.model.Socio;

import java.util.List;

/**
 * Created by carlo on 13/05/2017.
 */

public class AdapterAddSocios extends ArrayAdapter<Socio> implements
        View.OnClickListener {
    private LayoutInflater inflater;
    private List<Boolean> selected;


    public AdapterAddSocios(Context context, List<Socio> objects, List<Boolean> selectd) {
        super(context, 0, objects);
        selected = selectd;
        inflater = LayoutInflater.from(context);
    }

    public List<Boolean> getSelecteds() {
        return selected;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        // holder pattern
        Holder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_add_socios, viewGroup, false);
            holder.setRelativeLayout((RelativeLayout) convertView.findViewById(R.id.rowListAddSocios));
            holder.setTvNombre((TextView) convertView.findViewById(R.id.tvNombreAddSoc));
            holder.setTvDni((TextView) convertView.findViewById(R.id.tvDniAddSoc));
            holder.setSelect((CheckBox) convertView.findViewById(R.id.checkBoxAddSoc));
        }else {
            holder = (Holder) convertView.getTag();
        }

        final Socio socio = getItem(i);
        if(i%2 == 1) {
            holder.getRelativeLayout().setBackgroundColor(0x88EEEEEE);
        }
        if(!socio.getNombre().isEmpty() && !socio.getApellidos().isEmpty()) {
            holder.getTvNombre().setText(socio.getApellidos() + ", " + socio.getNombre());
        }else {
            holder.getTvNombre().setVisibility(View.GONE);
        }

        if(socio.getDni() != null) {
            holder.getTvDni().setText(socio.getDni());
        }else {
            holder.getTvDni().setVisibility(View.GONE);
        }

        holder.getSelect().setTag(i);
        holder.getSelect().setChecked(selected.get(i));
        holder.getSelect().setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        CheckBox checkBox = (CheckBox) v;
        int position = (Integer) v.getTag();
        selected.set(position, checkBox.isChecked());
    }

    static class Holder
    {
        private TextView tvNombre, tvDni;
        private CheckBox select;
        RelativeLayout relativeLayout;

        public TextView getTvNombre() {
            return tvNombre;
        }

        public void setTvNombre(TextView tvNombre) {
            this.tvNombre = tvNombre;
        }

        public TextView getTvDni() {
            return tvDni;
        }

        public void setTvDni(TextView tvDni) {
            this.tvDni = tvDni;
        }

        public CheckBox getSelect() {
            return select;
        }

        public void setSelect(CheckBox select) {
            this.select = select;
        }

        public RelativeLayout getRelativeLayout() {
            return relativeLayout;
        }

        public void setRelativeLayout(RelativeLayout relativeLayout) {
            this.relativeLayout = relativeLayout;
        }
    }
}
