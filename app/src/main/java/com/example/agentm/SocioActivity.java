package com.example.agentm;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.adapters.AdapterListSocActs;
import com.example.agentm.model.Actividad;
import com.example.agentm.model.CuotasSocio;
import com.example.agentm.model.Socio;
import com.example.agentm.model.SocioActividad;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.agentm.UpdateSocioActivity.RESULT_DELETED;


public class SocioActivity extends AppCompatActivity {

    private static final int UPDATESOCIO_REQUEST = 4;
    private TextView tvNomApell, tvDni, tvTel, tvEmail, tvIsUpc, tvImpagos;
    private Socio socio;
    private ListView listView;
    private AdapterListSocActs adapterListSocActs;

    MobileServiceClient mClient;
    private static MobileServiceTable<CuotasSocio> mCuotasTable;
    private static MobileServiceTable<SocioActividad> mSocioActTable;
    private static MobileServiceTable<Actividad> mActividadTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socio);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.activitySocio_toolbar);
        myToolbar.inflateMenu(R.menu.view_actionbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.Socio);

        tvNomApell = (TextView) findViewById(R.id.nombrViewSocio);
        tvDni = (TextView) findViewById(R.id.dniSocio);
        tvTel = (TextView) findViewById(R.id.telSocio);
        tvEmail = (TextView) findViewById(R.id.emailSocio);
        tvIsUpc = (TextView) findViewById(R.id.esUPCSocio);
        tvImpagos = (TextView) findViewById(R.id.impagosSocio);
        mClient = ClientFactory.getInstance().getClient();
        mCuotasTable = mClient.getTable(CuotasSocio.class);
        mSocioActTable = mClient.getTable(SocioActividad.class);
        mActividadTable = mClient.getTable(Actividad.class);
        listView = (ListView)  findViewById(R.id.listaSocActs);

        new GetImpagos().execute();

        socio = new Socio();
        socio.setId(getIntent().getExtras().getString("id"));
        socio.setNombre(getIntent().getExtras().getString("nombre"));
        socio.setApellidos(getIntent().getExtras().getString("apellidos"));
        socio.setDni(getIntent().getExtras().getString("dni"));
        socio.setTelefono(Integer.parseInt(getIntent().getExtras().getString("telefono")));
        socio.setEmail(getIntent().getExtras().getString("email"));
        if(getIntent().getExtras().getString("isupc").equals("true"))socio.setEsUPC(true);
        else socio.setEsUPC(false);
        new GetSocActivs().execute();
        initTextViews();

    }

    private void initTextViews() {
        tvNomApell.setText(socio.getNombre()+ " " + socio.getApellidos());
        tvDni.setText(socio.getDni());
        tvTel.setText(String.valueOf(socio.getTelefono()));
        tvEmail.setText(socio.getEmail());
        if(!socio.isEsUPC()) tvIsUpc.setVisibility(View.GONE);
        else tvIsUpc.setVisibility(View.VISIBLE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_actionbar, menu);
        return true;
    }

    private void startActivityUpdateSocio() {
        Intent intent = new Intent(this, UpdateSocioActivity.class);
        intent.putExtra("id", socio.getId());
        intent.putExtra("nombre", socio.getNombre());
        intent.putExtra("apellidos", socio.getApellidos());
        intent.putExtra("dni", socio.getDni());
        intent.putExtra("telefono", String.valueOf(socio.getTelefono()));
        intent.putExtra("email", socio.getEmail());
        if(socio.isEsUPC()) intent.putExtra("isupc", "true");
        else intent.putExtra("isupc", "false");
        startActivityForResult(intent, UPDATESOCIO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == UPDATESOCIO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                socio = new Socio();
                socio.setId(data.getExtras().getString("id"));
                socio.setNombre(data.getExtras().getString("nombre"));
                socio.setApellidos(data.getExtras().getString("apellidos"));
                socio.setDni(data.getExtras().getString("dni"));
                socio.setTelefono(Integer.parseInt(data.getExtras().getString("telefono")));
                socio.setEmail(data.getExtras().getString("email"));
                if(data.getExtras().getString("isupc").equals("true"))socio.setEsUPC(true);
                else socio.setEsUPC(false);
                initTextViews();
            }else if(resultCode == RESULT_DELETED) {
                setResult(RESULT_OK);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateToolbar:
                startActivityUpdateSocio();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Show toast
    private void show(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void setImpagos(List<CuotasSocio> cuotasSocios) {
        if (cuotasSocios.size() > 0) {
            tvImpagos.setText(getString(R.string.impago) + " " + String.valueOf(cuotasSocios.get(0).getYear()));
            for (int i = 1; i < cuotasSocios.size(); ++i) {
                tvImpagos.setText(tvImpagos.getText().toString() + "\n" + getString(R.string.impago) + " " + String.valueOf(cuotasSocios.get(i).getYear()));
            }
            tvImpagos.setVisibility(View.VISIBLE);
        }
    }
    private class GetImpagos extends AsyncTask<Void, Void, List<CuotasSocio>> {

        @Override
        protected List<CuotasSocio> doInBackground(Void... content) {

            try {
                return mCuotasTable.where()
                        .field("id_socio").eq(socio.getId()).and().
                        field("pagado").eq(false).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            List<CuotasSocio> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<CuotasSocio> result) {
            setImpagos(result);
        }
    }

    private class GetSocActivs extends AsyncTask<Void, Void, List<Actividad>> {

        @Override
        protected List<Actividad> doInBackground(Void... content) {

            try {
                List<SocioActividad> socioActividadList = mSocioActTable.where().field("socio_id").eq(socio.getId()).and().field("pagado").eq(true).execute().get();
                List<Actividad> actividades = new ArrayList<>();
                for (int i = 0; i < socioActividadList.size(); ++i) {
                    actividades.addAll(mActividadTable.where().field("id").eq(socioActividadList.get(i).getActividad_id()).select("nombre","fecha").execute().get());
                }
                return actividades;
            } catch (InterruptedException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            List<Actividad> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<Actividad> result) {
            inflateListSocActs(result);
        }
    }

    private void inflateListSocActs(List<Actividad> registros) {
        adapterListSocActs = new AdapterListSocActs(getApplicationContext(), registros);
        listView.setAdapter(adapterListSocActs);
    }
}
