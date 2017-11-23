package com.example.agentm;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.Patterns.CustomActividadIntent;
import com.example.agentm.Patterns.Utils;
import com.example.agentm.adapters.AdaptarListSocios;
import com.example.agentm.adapters.AdapterPagosActividad;
import com.example.agentm.model.Actividad;
import com.example.agentm.model.Socio;
import com.example.agentm.model.SocioActividad;
import com.example.agentm.model.VistaSocioActividad;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ActividadActivity extends AppCompatActivity {

    private static final int UPDATEACTIVIDAD_REQUEST = 122;
    public static final int RESULT_DELETED = 777;
    private static final int ADDSOCIOACTIVIDAD_REQUEST = 212;
    private TextView tvTitulo, tvDesc, tvLugar, tvUbica, tvPrecio, tvFecha, tvEuro, tvGratis;
    private Actividad actividad;
    private String sFecha;
    private List<Socio>  otrosSocios;
    private ListView listView;
    private NestedScrollView nsvViewAct;
    //private List<Socio> sociosParticipan;
    private List<VistaSocioActividad> vistaSocioActividades;

    MobileServiceClient mClient;
    private static MobileServiceTable<Socio> mSocioTable;
    private static MobileServiceTable<SocioActividad> mSocioActividadTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.activityActividad_toolbar);
        myToolbar.inflateMenu(R.menu.view_activity_actionbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.evento);



        tvTitulo = (TextView) findViewById(R.id.tituloViewAct);
        tvDesc = (TextView) findViewById(R.id.desViewAct);
        tvLugar = (TextView) findViewById(R.id.lugarViewAct);
        tvUbica = (TextView) findViewById(R.id.ubicaViewAct);
        tvPrecio = (TextView) findViewById(R.id.precioViewAct);
        tvFecha = (TextView) findViewById(R.id.fechaViewAct);
        tvEuro = (TextView) findViewById(R.id.euroViewAct);
        tvGratis = (TextView) findViewById(R.id.gratisViewAct);

        actividad = new Actividad();
        actividad.setId(getIntent().getExtras().getString("id"));
        actividad.setNombre(getIntent().getExtras().getString("titulo"));

        if(getIntent().getExtras().getString("descripcion") != null && !getIntent().getExtras().getString("descripcion").isEmpty()) {
            actividad.setDescripcion(getIntent().getExtras().getString("descripcion"));
        }
        if(getIntent().getExtras().getString("lugar") != null && !getIntent().getExtras().getString("lugar").isEmpty()) {
            actividad.setLugar(getIntent().getExtras().getString("lugar"));
        }
        if(getIntent().getExtras().getString("ubicacion") != null && !getIntent().getExtras().getString("ubicacion").isEmpty()) {
            actividad.setUbicacion(getIntent().getExtras().getString("ubicacion"));
        }
        actividad.setPrecio(Float.parseFloat(getIntent().getExtras().getString("precio")));
        try {
            actividad.setFecha(Utils.completeDateToDate(getIntent().getExtras().getString("fecha")));
            sFecha = getIntent().getExtras().getString("fecha");
        }catch (ParseException e) {
            show(getString(R.string.errorFecha));
        }

        mClient = ClientFactory.getInstance().getClient();
        mSocioTable = mClient.getTable(Socio.class);
        mSocioActividadTable = mClient.getTable(SocioActividad.class);
        //sociosParticipan = new ArrayList<>();
        otrosSocios = new ArrayList<>();
        vistaSocioActividades = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listaSociosAct);
        new GetSocios().execute();
        nsvViewAct = (NestedScrollView) findViewById(R.id.nsvViewAct);

        initViews();


    }

    private void initViews() {
        tvTitulo.setText(actividad.getNombre());

        if(actividad.getDescripcion() != null && !actividad.getDescripcion().isEmpty()){
            tvDesc.setText(actividad.getDescripcion());
        } else {
            tvDesc.setVisibility(View.GONE);
        }

        if(actividad.getLugar() != null && !actividad.getLugar().isEmpty()){
            tvLugar.setText(actividad.getLugar());
        } else {
            tvLugar.setVisibility(View.GONE);
        }

        if(actividad.getUbicacion() != null && !actividad.getUbicacion().isEmpty()){
            tvUbica.setText(actividad.getUbicacion());
        } else {
            tvUbica.setVisibility(View.GONE);
        }

        if(sFecha != null && !sFecha.isEmpty()){
            tvFecha.setText(sFecha);
        }
        if (actividad.getPrecio() > 0) {
            tvPrecio.setText(String.valueOf(actividad.getPrecio()));
            tvPrecio.setVisibility(View.VISIBLE);
            tvEuro.setVisibility(View.VISIBLE);
            tvGratis.setVisibility(View.GONE);
        }else {
            tvEuro.setVisibility(View.GONE);
            tvPrecio.setVisibility(View.GONE);
            tvGratis.setVisibility(View.VISIBLE);
        }


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_activity_actionbar, menu);
        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addSocioToolbar:
                startActivityAddSocioActividad();
                return true;
            case R.id.updateToolbar:
                startActivityUpdateActividad();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void startActivityAddSocioActividad() {
        Intent intent = new Intent(this, AddSociosActivity.class);
        CustomActividadIntent customActividadIntent = CustomActividadIntent.getInstance();
        customActividadIntent.setIdAct(actividad.getId());
        customActividadIntent.setPrecioAct(actividad.getPrecio());
        //customActividadIntent.setParticipan(sociosParticipan);
        customActividadIntent.setNoParticipan(otrosSocios);
        startActivityForResult(intent, ADDSOCIOACTIVIDAD_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == UPDATEACTIVIDAD_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                actividad = new Actividad();
                actividad.setId(data.getExtras().getString("id"));
                actividad.setNombre(data.getExtras().getString("titulo"));

                if(data.getExtras().getString("descripcion") != null && !data.getExtras().getString("descripcion").isEmpty()) {
                    actividad.setDescripcion(data.getExtras().getString("descripcion"));
                }
                if(data.getExtras().getString("lugar") != null && !data.getExtras().getString("lugar").isEmpty()) {
                    actividad.setLugar(data.getExtras().getString("lugar"));
                }
                if(data.getExtras().getString("ubicacion") != null && !data.getExtras().getString("ubicacion").isEmpty()) {
                    actividad.setUbicacion(data.getExtras().getString("ubicacion"));
                }
                actividad.setPrecio(Float.parseFloat(data.getExtras().getString("precio")));
                try {
                    actividad.setFecha(Utils.completeDateToDate(data.getExtras().getString("fecha")));
                    sFecha = data.getExtras().getString("fecha");
                }catch (ParseException e) {
                    show(getString(R.string.errorFecha));
                }
                initViews();
            }else if(resultCode == RESULT_DELETED) {
                setResult(RESULT_OK);
                finish();
            }
        }else if (requestCode == ADDSOCIOACTIVIDAD_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                addNewSociosParticipan();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addNewSociosParticipan() {
        CustomActividadIntent customActividadIntent = CustomActividadIntent.getInstance();
        otrosSocios = customActividadIntent.getNoParticipan();
        List<Socio> sociosPart = customActividadIntent.getParticipan();
        vistaSocioActividades.addAll(customActividadIntent.getVSocActParticipan());
        inflateListSociosParticipan();
    }


    private void startActivityUpdateActividad() {
        Intent intent = new Intent(this, UpdateActividadActivity.class);
        intent.putExtra("id", actividad.getId());
        intent.putExtra("titulo", actividad.getNombre());
        if(actividad.getDescripcion() != null && !actividad.getDescripcion().isEmpty()){
            intent.putExtra("descripcion", actividad.getDescripcion());
        }else {
            intent.putExtra("descripcion", "");
        }

        if(actividad.getLugar() != null && !actividad.getLugar().isEmpty()){
            intent.putExtra("lugar", actividad.getLugar());
        }else {
            intent.putExtra("lugar", "");
        }
        if(actividad.getUbicacion() != null && !actividad.getUbicacion().isEmpty()){
            intent.putExtra("ubicacion", actividad.getUbicacion());
        }else {
            intent.putExtra("ubicacion", "");
        }
        intent.putExtra("precio", String.valueOf(actividad.getPrecio()));

        String s = Utils.completeDateToString(actividad.getFecha());
        intent.putExtra("fecha", s);

        startActivityForResult(intent, UPDATEACTIVIDAD_REQUEST);
    }

    //Show toast
    private void show(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void inflateListSociosParticipan() {
        boolean gratis = true;
        if (actividad.getPrecio() > 0) gratis = false;
        AdapterPagosActividad adapterPagosActividad = new AdapterPagosActividad(this, vistaSocioActividades, gratis, this);
        listView.setAdapter(adapterPagosActividad);
        listView.setFocusable(false);
        nsvViewAct.fullScroll(ScrollView.FOCUS_UP);
    }

    public void refresh() {
        new GetSocios().execute();
    }
    private class GetSocios extends AsyncTask<Void, Void, List<Socio>> {

        @Override
        protected List<Socio> doInBackground(Void... content) {

            try {
                List<Socio> allSocios = mSocioTable.orderBy("apellidos", QueryOrder.Ascending)
                        .select("id", "nombre", "apellidos", "dni").execute().get();

                List<SocioActividad> socioActividads =mSocioActividadTable.where()
                        .field("actividad_id").eq(actividad.getId()).execute().get();
                //List<Socio> socioParticipa = new ArrayList<>();

                for (int i = 0; i < socioActividads.size(); ++i) {
                    boolean encontrado = false;
                    int iter = 0;
                    while (encontrado == false && iter < allSocios.size()) {
                        if(socioActividads.get(i).getSocio_id().equals(allSocios.get(iter).getId())){
                            //socioParticipa.add(allSocios.get(iter));
                            VistaSocioActividad vsa = new VistaSocioActividad();
                            vsa.setSocioActividad(socioActividads.get(i));
                            vsa.setSocio(allSocios.get(iter));
                            vistaSocioActividades.add(vsa);
                            allSocios.remove(iter);
                            encontrado=true;
                        }
                        ++iter;
                    }
                }

                //sociosParticipan = socioParticipa;
                otrosSocios = allSocios;

            } catch (InterruptedException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (ExecutionException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            }
            List<Socio> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<Socio> result) {
            inflateListSociosParticipan();
        }
    }
}
