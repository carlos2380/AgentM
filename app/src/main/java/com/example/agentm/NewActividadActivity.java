package com.example.agentm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.Patterns.Utils;
import com.example.agentm.model.Actividad;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.security.AccessController.getContext;


public class NewActividadActivity extends AppCompatActivity {

    private MobileServiceClient mClient;
    private MobileServiceTable<Actividad> mActividadTable;

    private EditText etTitulo, etDescripcion, etPrecio, etLugar, etUbicacion;
    private RelativeLayout btnFecha, btnHora;
    private static TextView tvFecha, tvHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_actividad);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.crearActividad_toolbar);
        myToolbar.inflateMenu(R.menu.create_actionbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.crearEvento);

        mClient = ClientFactory.getInstance().getClient();
        mActividadTable = mClient.getTable(Actividad.class);

        etTitulo = (EditText) findViewById(R.id.edTituloAct);
        etDescripcion = (EditText) findViewById(R.id.edDescAct);
        etPrecio = (EditText) findViewById(R.id.edPrecioAct);
        etLugar = (EditText) findViewById(R.id.edLugarAct);
        etUbicacion = (EditText) findViewById(R.id.edUbicacionAct);
        btnFecha = (RelativeLayout) findViewById(R.id.rledFechaAct);
        btnHora = (RelativeLayout) findViewById(R.id.rledHoraAct);
        tvFecha = (TextView) findViewById(R.id.tvFechaAct);
        tvHora = (TextView) findViewById(R.id.tvHoraAct);

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = format.format(c.getTime());
        tvFecha.setText(fecha);

        format = new SimpleDateFormat("HH:mm");
        String hora = format.format(c.getTime());
        tvHora.setText(hora);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveToolbar:
                validateAndSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void validateAndSave() {
        boolean isValid = true;
        if(etTitulo.getText().toString().isEmpty()){
            etTitulo.setError(getString(R.string.campoVacio));
            isValid= false;
        }if (etPrecio.getText().toString().isEmpty()){
            isValid= false;
            etPrecio.setError(getString(R.string.campoVacio));
        }if (etUbicacion.getText().toString().isEmpty()){
            isValid= false;
            etUbicacion.setError(getString(R.string.campoVacio));
        }
        Calendar now = Calendar.getInstance();
        long timeEvent = 0;
        try {
            timeEvent = Utils.completeDateToDate(tvFecha.getText().toString() + " " + tvHora.getText().toString()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(now.getTimeInMillis() > timeEvent) {
            isValid= false;
            tvFecha.setError(getString(R.string.fechaPasada));
            tvHora.setError(getString(R.string.fechaPasada));
        }

        if (isValid) {
            Actividad newActividad = new Actividad();
            newActividad.setNombre(etTitulo.getText().toString());
            if(!etDescripcion.getText().toString().isEmpty()){
                newActividad.setDescripcion(etDescripcion.getText().toString());
            }
            if(!etLugar.getText().toString().isEmpty()){
                newActividad.setLugar(etLugar.getText().toString());
            }
            if(!etUbicacion.getText().toString().isEmpty()){
                newActividad.setUbicacion(etUbicacion.getText().toString());
            }

            java.text.DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = null;
            try {
                date = format.parse(tvFecha.getText().toString() + " " + tvHora.getText().toString());
            } catch (ParseException e) {
                show("Error Parse String To Date");
            }
            if (date != null) newActividad.setFecha(date);

            newActividad.setPrecio(Float.parseFloat(etPrecio.getText().toString()));

            new CreateRegistro().execute(newActividad);
        }
    }
    //Show toast
    private void show(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private class CreateRegistro extends AsyncTask<Actividad, Void, Void> {


        @Override
        protected Void doInBackground(Actividad... actividades) {
            if(actividades.length > 0) mActividadTable.insert(actividades[0]);
            else show(getString(R.string.ErrorSaveSocio));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            setResult(RESULT_OK);
            finish();
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            final Calendar c = Calendar.getInstance();
            c.set(year,month,day);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = format.format(c.getTime());
            tvFecha.setText(fecha);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of DatePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            final Calendar c = Calendar.getInstance();
            c.set(0,0,0,hourOfDay,minute);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String hora = format.format(c.getTime());

            tvHora.setText(hora);
        }
    }
}