package com.example.agentm.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.R;
import com.example.agentm.UpdateSocioActivity;
import com.example.agentm.model.CuotasSocio;
import com.example.agentm.model.Socio;
import com.example.agentm.model.VistaCuota;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;

/**
 * Created by carlo on 11/05/2017.
 */

public class AdapterListCuotas extends ArrayAdapter<VistaCuota> implements
        View.OnClickListener {
    private LayoutInflater inflater;
   /*private List<Socio> socios;
    private List<CuotasSocio> cuotasSocios;
    private List<VistaCuota> vistaCuotas;
    private TextView tvNombre, tvDni;
    private CheckBox pagado;
    RelativeLayout relativeLayout;*/


    public AdapterListCuotas(Context context, List<VistaCuota> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        // holder pattern
        Holder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_cuotas, viewGroup, false);
            holder.setRelativeLayout((RelativeLayout) convertView.findViewById(R.id.rowListCuotas));
            holder.setTvNombre((TextView) convertView.findViewById(R.id.tvNombreCuota));
            holder.setTvDni((TextView) convertView.findViewById(R.id.tvDniCuota));
            holder.setPagado((CheckBox) convertView.findViewById(R.id.checkBoxPago));
        }else {
            holder = (Holder) convertView.getTag();
        }

        final VistaCuota vistaCuota = getItem(i);
        if(i%2 == 1) {
            holder.getRelativeLayout().setBackgroundColor(0x88EEEEEE);
        }
        if(!vistaCuota.getNombre().isEmpty() && !vistaCuota.getApellidos().isEmpty()) {
            holder.getTvNombre().setText(vistaCuota.getApellidos() + ", " + vistaCuota.getNombre());
        }else {
            holder.getTvNombre().setVisibility(View.GONE);
        }

        if(vistaCuota.getDni() != null) {
            holder.getTvDni().setText(vistaCuota.getDni());
        }else {
            holder.getTvDni().setVisibility(View.GONE);
        }

        holder.getPagado().setTag(i);
        holder.getPagado().setChecked(vistaCuota.getPagado());
        holder.getPagado().setOnClickListener(this);
        return convertView;
    }

    private static View alertView;
    @Override
    public void onClick(View v) {
        alertView = v;
        createAlertPagar();
    }

    private String getTitle() {
        if(getItem((Integer) alertView.getTag()).getPagado()) {
            return "Deshacer pago";
        }
        else {
            return "Confirmar Pago";
        }
    }

    private String getMessage() {
        return "Socio: " + getItem((Integer) alertView.getTag()).getNombre() + " " +
                getItem((Integer) alertView.getTag()).getApellidos() +"\n" +
                "NIF: " + getItem((Integer) alertView.getTag()).getDni();
    }

    private void createAlertPagar() {
        AlertDialog dialog =
        new AlertDialog.Builder(getContext())
                .setTitle(getTitle())
                .setMessage(getMessage())
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CheckBox checkBox = (CheckBox) alertView;
                        int position = (Integer) alertView.getTag();
                        getItem(position).setPagado(checkBox.isChecked());

                        CuotasSocio cuotasSocio = new CuotasSocio();
                        cuotasSocio.setId(getItem(position).getId());
                        cuotasSocio.setPagado(getItem(position).getPagado());
                        cuotasSocio.setYear(getItem(position).getYear());
                        cuotasSocio.setId_socio(getItem(position).getId_socio());

                        new UpdateCuota().execute(cuotasSocio);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CheckBox checkBox = (CheckBox) alertView;
                        int position = (Integer) alertView.getTag();
                        checkBox.setChecked(getItem(position).getPagado());
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextSize(20);
    }

    private class UpdateCuota extends AsyncTask<CuotasSocio, Void, Void> {


        @Override
        protected Void doInBackground(CuotasSocio... cuotasSocios) {
            MobileServiceClient mClient = ClientFactory.getInstance().getClient();
            MobileServiceTable<CuotasSocio> mCuotaSocio = mClient.getTable(CuotasSocio.class);
            mCuotaSocio.update(cuotasSocios[0]);
            return null;
        }

        /*@Override
        protected Void onPostExecute(Void... v) {

        }*/
    }

    static class Holder
    {
        private TextView tvNombre, tvDni;
        private CheckBox pagado;
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

        public CheckBox getPagado() {
            return pagado;
        }

        public void setPagado(CheckBox pagado) {
            this.pagado = pagado;
        }

        public RelativeLayout getRelativeLayout() {
            return relativeLayout;
        }

        public void setRelativeLayout(RelativeLayout relativeLayout) {
            this.relativeLayout = relativeLayout;
        }
    }
}
