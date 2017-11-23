package com.example.agentm;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.Patterns.CustomActividadIntent;
import com.example.agentm.adapters.AdapterAddSocios;
import com.example.agentm.model.Socio;
import com.example.agentm.model.SocioActividad;
import com.example.agentm.model.VistaSocioActividad;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.agentm.R.string.actividad;

public class AddSociosActivity extends AppCompatActivity {

    private ListView listView;
    private AdapterAddSocios adapterAddSocios;
    private MobileServiceClient mClient;
    private MobileServiceTable<SocioActividad> mSocioActTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_socios);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.addSocios_toolbar);
        myToolbar.inflateMenu(R.menu.add_socio_actionbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.addSocios);

        listView = (ListView) findViewById(R.id.listaAddSocios);
        mClient = ClientFactory.getInstance().getClient();
        mSocioActTable = mClient.getTable(SocioActividad.class);

        CustomActividadIntent customIntent = CustomActividadIntent.getInstance();

        List<Socio> participan = customIntent.getNoParticipan();
        List<Boolean> selecteds = new ArrayList<>();
        for (int i = 0; i < participan.size(); ++i) {
            selecteds.add(false);
        }

        adapterAddSocios = new AdapterAddSocios(getApplicationContext(), customIntent.getNoParticipan(), selecteds);
        listView.setAdapter(adapterAddSocios);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_socio_actionbar, menu);
        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirmarToolbar:
                new UpdateSocioAct().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class UpdateSocioAct extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            CustomActividadIntent customIntent = CustomActividadIntent.getInstance();
            String idAct = customIntent.getIdAct();
            List<Socio> noParticipan = customIntent.getNoParticipan();
            List<VistaSocioActividad> vsaList = new ArrayList<>();
            List<Boolean> selecteds = adapterAddSocios.getSelecteds();
            Boolean pagado = true;
            if(customIntent.getPrecioAct()>0) {
                pagado =false;
            }
            for (int i = 0; i < selecteds.size(); ++i) {
                if(selecteds.get(i)) {
                    SocioActividad sa = new SocioActividad();
                    sa.setActividad_id(idAct);
                    sa.setSocio_id(noParticipan.get(i).getId());
                    sa.setPagado(pagado);
                    try {
                        sa = mSocioActTable.insert(sa).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    VistaSocioActividad vsa = new VistaSocioActividad();
                    vsa.setSocioActividad(sa);
                    vsa.setSocio(noParticipan.get(i));
                    vsaList.add(vsa);
                }
            }

            for (int i = 0; i < selecteds.size(); ++i) {
                if(selecteds.get(i)) {
                    noParticipan.remove(i);
                }
            }

            customIntent.setSocVActParticipan(vsaList);
            customIntent.setNoParticipan(noParticipan);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            finaliza();
        }
    }

    private void finaliza() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }
}
