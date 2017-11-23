package com.example.agentm;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.Patterns.Utils;
import com.example.agentm.adapters.AdapterListCuotas;
import com.example.agentm.model.CuotasSocio;
import com.example.agentm.model.Socio;
import com.example.agentm.model.VistaCuota;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CuotasFragment extends Fragment {

    private View view;
    private ListView listView;
    MobileServiceClient mClient;

    private MobileServiceTable<Socio> mSocioTable;
    private MobileServiceTable<CuotasSocio> mCuotasSocioTable;

    private List<VistaCuota> vistaCuotas;
    private LinearLayout llFiltro;
    private AdapterListCuotas adapterCuotas;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Spinner spYear, psCuotas;
    private String yearSelected;
    private int tipoCuota;
    //Variable para saber el orderBy de la query
    private int orederBy;

    private Boolean showFilter;

    public CuotasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_cuotas, container, false);

        mClient = ClientFactory.getInstance().getClient();
        mSocioTable = mClient.getTable(Socio.class);
        mCuotasSocioTable = mClient.getTable(CuotasSocio.class);
        orederBy = R.id.cuotasOrderApellidos;
        listView = (ListView) view.findViewById(R.id.listaCuotas);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutCuotas);
        mSwipeRefreshLayout.setRefreshing(true);
        new GetCuotasSocio().execute();
        llFiltro = (LinearLayout) view.findViewById(R.id.llFiltroCuotas);
        showFilter = false;

        spYear = (Spinner) view.findViewById(R.id.spinnerYear);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.year_array, R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spYear.setAdapter(adapter);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        yearSelected = String.valueOf(year);
        spYear.setSelection(year - 2017);


        psCuotas = (Spinner) view.findViewById(R.id.spinnerCuota);
        ArrayAdapter<CharSequence> adapterb = ArrayAdapter.createFromResource(getContext(),
                R.array.cuotas_array, R.layout.spinner_style);
        adapterb.setDropDownViewResource(R.layout.spinner_dropdown_item);
        psCuotas.setAdapter(adapterb);

        psCuotas.setSelection(0);
        tipoCuota = 0;

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                new GetCuotasSocio().execute();
            }
        });

        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                mSwipeRefreshLayout.setRefreshing(true);
                yearSelected = String.valueOf(2017+i);
                new GetCuotasSocio().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        psCuotas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSwipeRefreshLayout.setRefreshing(true);
                tipoCuota = i;
                new GetCuotasSocio().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cuotas_actionbar, menu);
        final MenuItem searchItem = menu.findItem(R.id.searchCuotas);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //permite modificar el hint que el EditText muestra por defecto
        searchView.setQueryHint(getText(R.string.NombreDNI));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mSwipeRefreshLayout.setRefreshing(true);
                new GetSearchCuotasSocio().execute(searchView.getQuery().toString());

                //se oculta el EditText
                searchView.setQuery("", false);
                searchView.setIconified(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cuotasOrderNombre:
                orederBy = R.id.cuotasOrderNombre;
                mSwipeRefreshLayout.setRefreshing(true);
                new GetCuotasSocio().execute();
                return true;
            case R.id.cuotasOrderApellidos:
                mSwipeRefreshLayout.setRefreshing(true);
                orederBy = R.id.cuotasOrderApellidos;
                new GetCuotasSocio().execute();
                return true;
            case R.id.cuotasOrderDni:
                mSwipeRefreshLayout.setRefreshing(true);
                orederBy = R.id.cuotasOrderDni;
                new GetCuotasSocio().execute();
                return true;
            case R.id.exportarSocios:
                createCuotasCSV();
                return true;
            case R.id.itemFiltroCuotas:
                filterSeleccted(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void filterSeleccted(MenuItem item) {
        if(showFilter) {
            item.setIcon(R.drawable.ic_filter_list_white_48dp);
            llFiltro.setVisibility(View.GONE);
        }
        else {
            item.setIcon(R.drawable.rotate_filter);
            llFiltro.setVisibility(View.VISIBLE);
        }
        showFilter = !showFilter;
    }

    //Show toast
    public void show(String s) {
        Toast.makeText(view.getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void inflateListVistaCuotas(List<VistaCuota> vistaCuotas) {
        adapterCuotas = new AdapterListCuotas(getContext(), vistaCuotas);
        listView.setAdapter(adapterCuotas);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    public void createCuotasCSV(){

        String result = "NOMBRE;APELLIDOS;DNI;PAGADO;AÑO\n";


        for(int i = 0; i < adapterCuotas.getCount(); ++i) {
            VistaCuota vistaCuota = adapterCuotas.getItem(i);

            result += vistaCuota.getNombre() + ";" + vistaCuota.getApellidos() + ";" +
                    vistaCuota.getDni() + ";";
            if(vistaCuota.getPagado()) result += "true" + ";";
            else result += "false" + ";";

            result += vistaCuota.getYear();

        }
        File file = new File(getContext().getFilesDir(), "Cuotas.csv");
        if (file.exists()) {
            file.delete();
        }
        file.toURI();


        boolean created = false;

        FileOutputStream outputStream;

        try {
            outputStream = getContext().openFileOutput( "Cuotas.csv", Context.MODE_WORLD_READABLE);
            outputStream.write(result.getBytes());
            outputStream.close();
            created = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Cuotas.csv");
        intent.putExtra(intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivityForResult(Intent.createChooser(intent, "Save File"), 4);
    }

    private class GetCuotasSocio extends AsyncTask<Void, Void, List<VistaCuota>> {

        @Override
        protected List<VistaCuota> doInBackground(Void... content) {
            try {
                vistaCuotas = new ArrayList<>();
                List<Socio> socis;

                switch (orederBy) {
                    case R.id.cuotasOrderNombre:
                        socis = mSocioTable.orderBy("nombre", QueryOrder.Ascending).select("id", "nombre", "apellidos", "dni").execute().get();
                        break;
                    case R.id.cuotasOrderApellidos:
                        socis = mSocioTable.orderBy("apellidos", QueryOrder.Ascending).select("id", "nombre", "apellidos", "dni").execute().get();
                        break;
                    case R.id.cuotasOrderDni:
                        socis = mSocioTable.orderBy("dni", QueryOrder.Ascending).select("id", "nombre", "apellidos", "dni").execute().get();
                        break;
                    default:
                        socis = mSocioTable.execute().get();
                }

                List<CuotasSocio> cslist;
                switch (tipoCuota) {
                    case 0:
                        cslist = mCuotasSocioTable.where()
                                .field("year").eq(yearSelected).execute().get();
                        break;
                    case 1:
                        cslist = mCuotasSocioTable.where()
                                .field("pagado").eq(false).and()
                                .field("year").eq(yearSelected).execute().get();
                        break;
                    case 2:
                        cslist = mCuotasSocioTable.where()
                                .field("pagado").eq(true).and()
                                .field("year").eq(yearSelected).execute().get();
                        break;
                    default:
                        cslist = mCuotasSocioTable.where()
                                .field("year").eq(yearSelected).execute().get();
                }

                for(int i = 0; i < socis.size(); ++i) {
                    int iter = 0;
                    boolean trobat = false;

                    while (iter < cslist.size() && !trobat) {

                        if (socis.get(i).getId().equals(cslist.get(iter).getId_socio())) {
                            VistaCuota vCuota = new VistaCuota();
                            vCuota.setId(cslist.get(iter).getId());
                            vCuota.setPagado(cslist.get(iter).getPagado());
                            vCuota.setYear(cslist.get(iter).getYear());
                            vCuota.setId_socio(socis.get(i).getId());
                            vCuota.setNombre(socis.get(i).getNombre());
                            vCuota.setApellidos(socis.get(i).getApellidos());
                            vCuota.setDni(socis.get(i).getDni());
                            vistaCuotas.add(vCuota);
                            trobat = true;
                            cslist.remove(iter);
                        }
                        ++iter;
                    }
                }
                /*
                for(int i = 0; i < socis.size(); ++i) {
                    VistaCuota vCuota = new VistaCuota();
                    String id_socio = socis.get(i).getId();
                    List<CuotasSocio> cSocio;
                    switch (tipoCuota) {
                        case 0:
                            cSocio = mCuotasSocioTable.where()
                                    .field("year").eq(yearSelected).and()
                                    .field("id_socio").eq(id_socio).execute().get();
                            break;
                        case 1:
                            cSocio = mCuotasSocioTable.where()
                                    .field("pagado").eq(false).and()
                                    .field("year").eq(yearSelected).and()
                                    .field("id_socio").eq(id_socio).execute().get();
                            break;
                        case 2:
                            cSocio = mCuotasSocioTable.where()
                                    .field("pagado").eq(true).and()
                                    .field("year").eq(yearSelected).and()
                                    .field("id_socio").eq(id_socio).execute().get();
                            break;
                        default:
                            cSocio = mCuotasSocioTable.where()
                                    .field("year").eq(yearSelected).and()
                                    .field("id_socio").eq(id_socio).execute().get();
                    }
                    if(cSocio.size() > 0) {
                        vCuota.setId(cSocio.get(0).getId());
                        vCuota.setPagado(cSocio.get(0).getPagado());
                        vCuota.setYear(cSocio.get(0).getYear());
                        vCuota.setId_socio(id_socio);
                        vCuota.setNombre(socis.get(i).getNombre());
                        vCuota.setApellidos(socis.get(i).getApellidos());
                        vCuota.setDni(socis.get(i).getDni());
                        vistaCuotas.add(vCuota);
                    }
                }*/
                return vistaCuotas;
            } catch (InterruptedException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (ExecutionException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (MobileServiceException e) {
                e.printStackTrace();
            }
            List<VistaCuota> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<VistaCuota> result) {
            inflateListVistaCuotas(result);
        }
    }

    private class GetSearchCuotasSocio extends AsyncTask<String, Void, List<VistaCuota>> {

        @Override
        protected List<VistaCuota> doInBackground(String... content) {

            try {
                vistaCuotas = new ArrayList<>();
                List<Socio> socis;

                switch (orederBy) {
                    case R.id.cuotasOrderNombre:
                        socis = mSocioTable.where()
                                .subStringOf(content[0], "nombre").
                                        or().subStringOf(content[0], "apellidos").
                                        or().subStringOf(content[0], "dni").
                                        orderBy("nombre", QueryOrder.Ascending).
                                        select("id", "nombre", "apellidos", "dni").
                                        execute().get();
                        break;
                    case R.id.cuotasOrderApellidos:
                        socis = mSocioTable.where()
                                .subStringOf(content[0], "nombre").
                                        or().subStringOf(content[0], "apellidos").
                                        or().subStringOf(content[0], "dni").
                                        orderBy("apellidos", QueryOrder.Ascending).
                                        select("id", "nombre", "apellidos", "dni").
                                        execute().get();
                        break;
                    case R.id.cuotasOrderDni:
                        socis = mSocioTable.where()
                                .subStringOf(content[0], "nombre").
                                        or().subStringOf(content[0], "apellidos").
                                        or().subStringOf(content[0], "dni").
                                        orderBy("dni", QueryOrder.Ascending).
                                        select("id", "nombre", "apellidos", "dni").
                                        execute().get();
                        break;
                    default:
                        socis = mSocioTable.where()
                                .subStringOf(content[0], "nombre").
                                        or().subStringOf(content[0], "apellidos").
                                        or().subStringOf(content[0], "dni").
                                        execute().get();
                }

                List<CuotasSocio> cslist;
                switch (tipoCuota) {
                    case 0:
                        cslist = mCuotasSocioTable.where()
                                .field("year").eq(yearSelected).execute().get();
                        break;
                    case 1:
                        cslist = mCuotasSocioTable.where()
                                .field("pagado").eq(false).and()
                                .field("year").eq(yearSelected).execute().get();
                        break;
                    case 2:
                        cslist = mCuotasSocioTable.where()
                                .field("pagado").eq(true).and()
                                .field("year").eq(yearSelected).execute().get();
                        break;
                    default:
                        cslist = mCuotasSocioTable.where()
                                .field("year").eq(yearSelected).execute().get();
                }
                for(int i = 0; i < socis.size(); ++i) {
                    int iter = 0;
                    boolean trobat = false;

                    while (iter < cslist.size() && !trobat) {

                        if (socis.get(i).getId().equals(cslist.get(iter).getId_socio())) {
                            VistaCuota vCuota = new VistaCuota();
                            vCuota.setId(cslist.get(iter).getId());
                            vCuota.setPagado(cslist.get(iter).getPagado());
                            vCuota.setYear(cslist.get(iter).getYear());
                            vCuota.setId_socio(socis.get(i).getId());
                            vCuota.setNombre(socis.get(i).getNombre());
                            vCuota.setApellidos(socis.get(i).getApellidos());
                            vCuota.setDni(socis.get(i).getDni());
                            vistaCuotas.add(vCuota);
                            trobat = true;
                            cslist.remove(iter);
                        }
                        ++iter;
                    }
                }
                return vistaCuotas;
            } catch (InterruptedException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (ExecutionException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            }
            List<VistaCuota> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<VistaCuota> result) {
            inflateListVistaCuotas(result);
        }
    }

}
