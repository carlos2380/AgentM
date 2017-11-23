package com.example.agentm;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.Patterns.Utils;
import com.example.agentm.adapters.AdapterListBalance;
import com.example.agentm.model.Registro;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceFragment extends Fragment {


    MobileServiceClient mClient;
    private static MobileServiceTable<Registro> mRegistroTable;

    private static View view;
    private Boolean showFilter;
    private static ListView listView;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton btnNewRegistro;
    private static AdapterListBalance adapterBalance;
    private RelativeLayout rlFiltros, rlDesde, rlHasta;
    private static TextView tvDesde, tvHasta;
    private static TextView tvTotal;
    private static Date dateDesde, dateHasta;
    private static Switch swFiltrar;
    private static Context context;


    public static final int NEWREGISTRO_REQUEST = 1;
    private static final int VIEWREGISTRO_REQUEST = 6;

    public BalanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_balance, container, false);
        mClient = ClientFactory.getInstance().getClient();
        mRegistroTable = mClient.getTable(Registro.class);
        listView = (ListView) view.findViewById(R.id.listaBalance);
        btnNewRegistro = (FloatingActionButton) view.findViewById(R.id.btnNewRegistro);
        tvTotal = (TextView) view.findViewById(R.id.tvTotal);
        tvDesde = (TextView) view.findViewById(R.id.fechadesde);
        tvHasta = (TextView) view.findViewById(R.id.fechahasta);
        rlDesde = (RelativeLayout) view.findViewById(R.id.rlDesde);
        rlHasta = (RelativeLayout) view.findViewById(R.id.rlHasta);

        showFilter = false;
        rlFiltros = (RelativeLayout) view.findViewById(R.id.rlFechaBalance);
        rlFiltros.setVisibility(View.GONE);
        swFiltrar = (Switch) view.findViewById(R.id.swFiltrar);

        context = getContext();

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutBalance);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                new GetRegistros().execute();
            }
        });

        //Init Fecha
        dateDesde = new Date();
        dateHasta = new Date();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = format.format(c.getTime());
        tvDesde.setText(fecha);
        tvHasta.setText(fecha);
        try {
            dateDesde = format.parse(fecha);
            dateHasta = format.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "Error al obtener la fecha", Toast.LENGTH_SHORT).show();
        }

        new GetRegistros().execute();

        rlDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragmentDesde();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        rlHasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragmentHasta();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        btnNewRegistro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getActivity(), NewRegistroActivity.class);
               startActivityForResult(intent, NEWREGISTRO_REQUEST);
            }
        });

        swFiltrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(swFiltrar.isChecked()) {
                    new GetRegistrosFiltroFecha().execute();
                }else {
                    new GetRegistros().execute();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Registro registro = (Registro) adapterBalance.getItem(i);
                Intent intent = new Intent(getActivity(), RegsitroActivity.class);
                intent.putExtra("id", registro.getId());
                intent.putExtra("titulo", registro.getTitulo());
                if(registro.getDescripcion() != null && !registro.getDescripcion().isEmpty()){
                    intent.putExtra("descripcion", registro.getDescripcion());
                }else {
                    intent.putExtra("descripcion", "");
                }
                intent.putExtra("precio", String.valueOf(registro.getPrecio()));


                String s = Utils.dateToString(registro.getFecha());
                intent.putExtra("fecha", s);

                if(registro.getImagen() != null &&!registro.getImagen().isEmpty()){
                    intent.putExtra("foto", registro.getImagen());
                }else {
                    intent.putExtra("foto", "");
                }

                intent.putExtra("modificada", registro.getModifiedBy());

                startActivityForResult(intent, VIEWREGISTRO_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.balance_actionbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemFiltroBalance:
                filterSeleccted(item);
                return true;
            case R.id.iteCVSBalance:
                createBalanceCSV();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEWREGISTRO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                new GetRegistros().execute();
            }
        }else if (requestCode == VIEWREGISTRO_REQUEST){
            if (resultCode == RESULT_OK) {
                new GetRegistros().execute();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createBalanceCSV(){

        String result = "TITULO;DESCRIPCION;FECHA;PRECIO;MODIFICADO POR\n";


        for(int i = 0; i < adapterBalance.getCount(); ++i) {
            Registro registro = (Registro) adapterBalance.getItem(i);

            result += registro.getTitulo() + ";";
            if(registro.getDescripcion() != null) {
                result += registro.getDescripcion() + ";";
            }else {
                result += ";";
            }
            result += Utils.dateToString(registro.getFecha()) + ";";

            result += String.valueOf(registro.getPrecio()) + ";";

            result += registro.getModifiedBy() + "\n";

        }
        File file = new File(getContext().getFilesDir(), "Balance.csv");
        if (file.exists()) {
            file.delete();
        }
        file.toURI();


        boolean created = false;

        FileOutputStream outputStream;

        try {
            outputStream = getContext().openFileOutput( "Balance.csv", Context.MODE_WORLD_READABLE);
            outputStream.write(result.getBytes());
            outputStream.close();
            created = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Balance.csv");
        intent.putExtra(intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivityForResult(Intent.createChooser(intent, "Save File"), 4);
    }
    private void filterSeleccted(MenuItem item) {
        if(showFilter) {
            item.setIcon(R.drawable.ic_filter_list_white_48dp);
            rlFiltros.setVisibility(View.GONE);
        }
        else {
            item.setIcon(R.drawable.rotate_filter);
            rlFiltros.setVisibility(View.VISIBLE);
        }
        showFilter = !showFilter;
    }

    private static void inflateListSocios(List<Registro> registros) {
        adapterBalance = new AdapterListBalance(context, registros);
        listView.setAdapter(adapterBalance);
        calcularTotal(registros);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private static void calcularTotal(List<Registro> registros) {
        float total = 0;
        for (int i = 0; i < registros.size(); ++i) {
            total += registros.get(i).getPrecio();
        }
        tvTotal.setText(String.valueOf( (float) Math.round(total * 100) / 100));
    }

    //Show toast
    public void show(String s) {
        Toast.makeText(view.getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private class GetRegistros extends AsyncTask<Void, Void, List<Registro>> {

        @Override
        protected List<Registro> doInBackground(Void... content) {

            try {
                return mRegistroTable.orderBy("fecha", QueryOrder.Descending).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (ExecutionException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            }
            List<Registro> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<Registro> result) {
            inflateListSocios(result);
        }
    }

    private static class GetRegistrosFiltroFecha extends AsyncTask<Void, Void, List<Registro>> {

        @Override
        protected List<Registro> doInBackground(Void... content) {

            try {
                return mRegistroTable.where().field("fecha").ge(dateDesde).and()
                        .field("fecha").le(dateHasta)
                        .orderBy("fecha", QueryOrder.Descending)
                        .execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error al obtener los registros", Toast.LENGTH_SHORT).show();
            } catch (ExecutionException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error al obtener los registros", Toast.LENGTH_SHORT).show();
            }
            List<Registro> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<Registro> result) {
            inflateListSocios(result);
        }
    }

    public static class DatePickerFragmentDesde extends DialogFragment
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
            c.set(year,month,day,0,0);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = format.format(c.getTime());
            tvDesde.setText(fecha);
            try {
                dateDesde = format.parse(fecha);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(view.getContext(), "Error al obtener la fecha", Toast.LENGTH_SHORT).show();
            }
            if(dateDesde.getTime() > dateHasta.getTime()) {
                dateHasta = dateDesde;
                tvHasta.setText(fecha);
            }
            getRegistrosDesdeHasta();
        }
    }



    public static void getRegistrosDesdeHasta() {
        if(swFiltrar.isChecked()) new GetRegistrosFiltroFecha().execute();
    }

    public static class DatePickerFragmentHasta extends DialogFragment
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
            c.set(year,month,day,0,0);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = format.format(c.getTime());
            tvHasta.setText(fecha);
            try {
                dateHasta = format.parse(fecha);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(view.getContext(), "Error al obtener la fecha", Toast.LENGTH_SHORT).show();
            }
            if(dateDesde.getTime() > dateHasta.getTime()) {
                dateDesde = dateHasta;
                tvDesde.setText(fecha);
            }
            getRegistrosDesdeHasta();
        }
    }

}
