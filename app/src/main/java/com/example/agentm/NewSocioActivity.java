package com.example.agentm;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.model.CuotasSocio;
import com.example.agentm.model.Socio;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class NewSocioActivity extends AppCompatActivity {

    private MobileServiceClient mClient;
    private MobileServiceTable<Socio> mSocioTable;
    private MobileServiceTable<CuotasSocio> mCuotasTable;

    private CheckBox cbIsUPC;
    private EditText etNombre, etApellidos, etDni, etTelefono, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_socio);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.crearSocio_toolbar);
        myToolbar.inflateMenu(R.menu.create_actionbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.crearsocio);

        mClient = ClientFactory.getInstance().getClient();
        mSocioTable = mClient.getTable(Socio.class);
        mCuotasTable = mClient.getTable(CuotasSocio.class);

        etNombre = (EditText) findViewById(R.id.edNameSocio);
        etApellidos = (EditText) findViewById(R.id.edApellidoSocio);
        etDni = (EditText) findViewById(R.id.edDniSocio);
        etTelefono = (EditText) findViewById(R.id.edTelSocio);
        etEmail = (EditText) findViewById(R.id.edEmailSocio);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rlchkisupc);
        cbIsUPC = (CheckBox) findViewById(R.id.edisUpcSocio);

        //El checkbox funciona en tot el relative layout
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cbIsUPC.setChecked(!cbIsUPC.isChecked());
            }
        });

    }

    //Validate and Save socio
    private void saveSocio() {
        if(validateInfo()) {
            Socio newSocio = new Socio();
            newSocio.setNombre(etNombre.getText().toString());
            newSocio.setApellidos(etApellidos.getText().toString());
            newSocio.setDni(etDni.getText().toString());
            newSocio.setTelefono(Integer.parseInt(etTelefono.getText().toString()));
            newSocio.setEmail(etEmail.getText().toString());
            newSocio.setEsUPC(cbIsUPC.isChecked());

            List<Socio> socios = new ArrayList<>();
            socios.add(newSocio);
            new CreateSocio().execute(newSocio);
        }
    }

    private boolean validateInfo() {
        boolean validation = true;
        removeEndLines();
        if(!Socio.validateNombreOrApellido(etNombre.getText().toString())) {
            validation = false;
            etNombre.setError(getString(R.string.campoVacio));
        }
        if(!Socio.validateNombreOrApellido(etApellidos.getText().toString())) {
            validation = false;
            etApellidos.setError(getString(R.string.campoVacio));
        }
        if(!Socio.validateDni(etDni.getText().toString())) {
            validation = false;
            etDni.setError(getString(R.string.invalidnif));
        }
        if(!Socio.validateTel(etTelefono.getText().toString())) {
            validation = false;
            etTelefono.setError(getString(R.string.invalidtel));
        }
        if(!Socio.validateEmail(etEmail.getText().toString())) {
            validation = false;
            etEmail.setError(getString(R.string.invalidemail));
        }

        return validation;
    }

    private void removeEndLines() {
        etNombre.setText(etNombre.getText().toString().replace("\n", " ").replace("\r", ""));
        etApellidos.setText(etApellidos.getText().toString().replace("\n", "").replace("\r", ""));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_actionbar, menu);
        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveToolbar:
                saveSocio();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Show toast
    private void show(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }


    private class CreateSocio extends AsyncTask<Socio, Void, Void> {


        @Override
        protected Void doInBackground(Socio... socios) {
            if(socios.length > 0){
                try {
                    Socio s = mSocioTable.insert(socios[0]).get();
                    CuotasSocio cs = new CuotasSocio();
                    cs.setId_socio(s.getId());
                    int y = Calendar.getInstance().get(Calendar.YEAR);
                    cs.setYear(String.valueOf(y));
                    cs.setPagado(false);
                    mCuotasTable.insert(cs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
            else show(getString(R.string.ErrorSaveSocio));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            setResult(RESULT_OK);
            finish();
        }
    }
}
