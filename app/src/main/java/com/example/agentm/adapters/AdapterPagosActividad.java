package com.example.agentm.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentm.ActividadActivity;
import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.R;
import com.example.agentm.model.SocioActividad;
import com.example.agentm.model.VistaCuota;
import com.example.agentm.model.VistaSocioActividad;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;


/**
 * Created by carlo on 13/05/2017.
 */

public class AdapterPagosActividad extends ArrayAdapter<VistaSocioActividad>  implements
        View.OnClickListener, View.OnLongClickListener{

    private AdapterPagosActividad apaThis = this;
    private LayoutInflater inflater;
    boolean gratis;
    ActividadActivity aa;
    Context padreContext;
    List<VistaSocioActividad> list;
    public AdapterPagosActividad(Context context, List<VistaSocioActividad> objects, boolean gratis, ActividadActivity act) {
        super(context, 0, objects);
        aa = act;
        padreContext = context;
        list = objects;
        this.gratis = gratis;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        // holder pattern
        Holder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            holder = new Holder();
            if(gratis) {
                convertView = inflater.inflate(R.layout.list_gratis_actividad, viewGroup, false);
            }else {
                convertView = inflater.inflate(R.layout.list_pago_actividad, viewGroup, false);
            }
            holder.setRelativeLayout((RelativeLayout) convertView.findViewById(R.id.rowListCuotas));
            holder.setTvNombre((TextView) convertView.findViewById(R.id.tvNombreCuota));
            holder.setTvDni((TextView) convertView.findViewById(R.id.tvDniCuota));
            holder.setPagado((CheckBox) convertView.findViewById(R.id.checkBoxPago));
        }else {
            holder = (Holder) convertView.getTag();
        }

        final VistaSocioActividad vistaSocioActividad = getItem(i);
        if(i%2 == 1) {
            holder.getRelativeLayout().setBackgroundColor(0x88EEEEEE);
        }
        if(!vistaSocioActividad.getNombre().isEmpty() && !vistaSocioActividad.getApellidos().isEmpty()) {
            holder.getTvNombre().setText(vistaSocioActividad.getApellidos() + ", " + vistaSocioActividad.getNombre());
        }else {
            holder.getTvNombre().setVisibility(View.GONE);
        }

        if(vistaSocioActividad.getDni() != null) {
            holder.getTvDni().setText(vistaSocioActividad.getDni());
        }else {
            holder.getTvDni().setVisibility(View.GONE);
        }

        holder.getPagado().setTag(i);
        holder.getRelativeLayout().setTag(i);
        holder.getPagado().setChecked(vistaSocioActividad.isPagado());
        holder.getPagado().setOnClickListener(this);
        holder.getRelativeLayout().setOnLongClickListener(this);
        return convertView;
    }


    private static View alertView;
    @Override
    public void onClick(View v) {
        alertView = v;
        createAlertPagar();
    }

    private boolean created = false;
    @Override
    public boolean onLongClick(View v){
        if(!getItem((Integer) v.getTag()).isPagado() || gratis) {
            alertView = v;
            v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.azulSelect));
            return createAlertEliminar();
        }
        return false;
    }
    private String getTitle() {
        if(getItem((Integer) alertView.getTag()).isPagado()) {
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

                        SocioActividad socioActividad = new SocioActividad();
                        socioActividad.setId(getItem(position).getId());
                        socioActividad.setPagado(getItem(position).isPagado());
                        socioActividad.setSocio_id(getItem(position).getSocio_id());
                        socioActividad.setActividad_id(getItem(position).getActividad_id());

                        new UpdateSocioActividad().execute(socioActividad);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CheckBox checkBox = (CheckBox) alertView;
                        int position = (Integer) alertView.getTag();
                        checkBox.setChecked(getItem(position).isPagado());

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setTextSize(20);
    }

    private boolean createAlertEliminar() {
        AlertDialog dialog =
                new AlertDialog.Builder(getContext())
                        .setTitle(alertView.getContext().getString(R.string.EliminarSocioEvento))
                        .setMessage(getMessage())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                int position = (Integer) alertView.getTag();
                                SocioActividad socioActividad = new SocioActividad();
                                socioActividad.setId(getItem(position).getId());
                                socioActividad.setPagado(getItem(position).isPagado());
                                socioActividad.setSocio_id(getItem(position).getSocio_id());
                                socioActividad.setActividad_id(getItem(position).getActividad_id());

                                new DeleteSocioActividad().execute(socioActividad);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int position = (Integer) alertView.getTag();
                                if(position%2 == 1) {
                                    alertView.setBackgroundColor(0x88EEEEEE);
                                }else {
                                    alertView.setBackgroundColor(0xFFF);
                                }
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(20);
        return false;
    }

    private class UpdateSocioActividad extends AsyncTask<SocioActividad, Void, Void> {


        @Override
        protected Void doInBackground(SocioActividad... cuotasSocios) {
            MobileServiceClient mClient = ClientFactory.getInstance().getClient();
            MobileServiceTable<SocioActividad> mSocioAct = mClient.getTable(SocioActividad.class);
            mSocioAct.update(cuotasSocios[0]);
            return null;
        }

        /*@Override
        protected Void onPostExecute(Void... v) {

        }*/
    }

    private void refresh() {
        this.notifyDataSetChanged();
    }
    private class DeleteSocioActividad extends AsyncTask<SocioActividad, Void, Void> {


        @Override
        protected Void doInBackground(SocioActividad... cuotasSocios) {
            MobileServiceClient mClient = ClientFactory.getInstance().getClient();
            MobileServiceTable<SocioActividad> mSocioAct = mClient.getTable(SocioActividad.class);
            mSocioAct.delete(cuotasSocios[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            apaThis.clear();
            aa.refresh();
        }
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
