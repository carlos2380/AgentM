package com.example.agentm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.model.Socio;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.List;


public class UpdateSocioActivity extends AppCompatActivity {

    private MobileServiceClient mClient;
    private MobileServiceTable<Socio> mSocioTable;
    private Socio socio;
    public static final int RESULT_DELETED = 600;


    private CheckBox cbIsUPC;
    private EditText etNombre, etApellidos, etDni, etTelefono, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_socio);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.crearSocio_toolbar);
        myToolbar.inflateMenu(R.menu.upate_actionbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.updateSocio);

        mClient = ClientFactory.getInstance().getClient();
        mSocioTable = mClient.getTable(Socio.class);

        socio = new Socio();
        socio.setId(getIntent().getExtras().getString("id"));
        socio.setNombre(getIntent().getExtras().getString("nombre"));
        socio.setApellidos(getIntent().getExtras().getString("apellidos"));
        socio.setDni(getIntent().getExtras().getString("dni"));
        socio.setTelefono(Integer.parseInt(getIntent().getExtras().getString("telefono")));
        socio.setEmail(getIntent().getExtras().getString("email"));
        if(getIntent().getExtras().getString("isupc").equals("true"))socio.setEsUPC(true);
        else socio.setEsUPC(false);

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

        etNombre.setText(socio.getNombre());
        etApellidos.setText(socio.getApellidos());
        etDni.setText(socio.getDni());
        etTelefono.setText(String.valueOf(socio.getTelefono()));
        etEmail.setText(socio.getEmail());
        cbIsUPC.setChecked(socio.isEsUPC());

    }

    //Validate and Save socio
    private void saveSocio() {
        if(validateInfo()) {

            socio.setNombre(etNombre.getText().toString());
            socio.setApellidos(etApellidos.getText().toString());
            socio.setDni(etDni.getText().toString());
            socio.setTelefono(Integer.parseInt(etTelefono.getText().toString()));
            socio.setEmail(etEmail.getText().toString());
            socio.setEsUPC(cbIsUPC.isChecked());

            List<Socio> socios = new ArrayList<>();
            socios.add(socio);
            new UpdateSocio().execute(socio);
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
        getMenuInflater().inflate(R.menu.upate_actionbar, menu);
        return true;
    }

    private void createAlertDelete() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.eliminarSocio))
                .setMessage(getString(R.string.areYouSureDeleteSocio))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteSocio().execute(socio);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveToolbar:
                saveSocio();
                return true;
            case R.id.deleteToolbar:
                createAlertDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Show toast
    private void show(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private class UpdateSocio extends AsyncTask<Socio, Void, Void> {


        @Override
        protected Void doInBackground(Socio... socios) {
            if(socios.length > 0) mSocioTable.update(socios[0]);
            else show(getString(R.string.ErrorSaveSocio));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Intent intent = new Intent();
            intent.putExtra("id", socio.getId());
            intent.putExtra("nombre", socio.getNombre());
            intent.putExtra("apellidos", socio.getApellidos());
            intent.putExtra("dni", socio.getDni());
            intent.putExtra("telefono", String.valueOf(socio.getTelefono()));
            intent.putExtra("email", socio.getEmail());
            if(socio.isEsUPC()) intent.putExtra("isupc", "true");
            else intent.putExtra("isupc", "false");
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private class DeleteSocio extends AsyncTask<Socio, Void, Void> {


        @Override
        protected Void doInBackground(Socio... socios) {
            if(socios.length > 0) mSocioTable.delete(socios[0]);
            else show(getString(R.string.ErrorSaveSocio));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            setResult(RESULT_DELETED);
            finish();
        }
    }
}
