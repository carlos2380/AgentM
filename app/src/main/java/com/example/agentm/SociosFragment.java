package com.example.agentm;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.adapters.AdaptarListSocios;
import com.example.agentm.model.Socio;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SociosFragment extends Fragment {

    static final int NEWSOCIO_REQUEST = 1;
    static final int VIEWSOCIO_REQUEST = 2;

    MobileServiceClient mClient;
    private MobileServiceTable<Socio> mSocioTable;

    private View view;
    private ListView listView;
    private AdaptarListSocios adapterSocios;
    private static ProgressBar loading;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton btnNewSocio;

    //Variable para saber el orderBy de la query
    private int orederBy;

    public SociosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_socios, container, false);
        mClient = ClientFactory.getInstance().getClient();
        mSocioTable = mClient.getTable(Socio.class);
        listView = (ListView) view.findViewById(R.id.listaSocios);
        orederBy = R.id.socioOrderApellidos;
        btnNewSocio = (FloatingActionButton) view.findViewById(R.id.btnNewSocio);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutSocios);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                new GetSocios().execute();
            }
        });

        btnNewSocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewSocioActivity.class);
                startActivityForResult(intent, NEWSOCIO_REQUEST);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Socio socio = (Socio) adapterSocios.getItem(i);
                Intent intent = new Intent(getActivity(), SocioActivity.class);
                intent.putExtra("id", socio.getId());
                intent.putExtra("nombre", socio.getNombre());
                intent.putExtra("apellidos", socio.getApellidos());
                intent.putExtra("dni", socio.getDni());
                intent.putExtra("telefono", String.valueOf(socio.getTelefono()));
                intent.putExtra("email", socio.getEmail());
                if(socio.isEsUPC()) intent.putExtra("isupc", "true");
                else intent.putExtra("isupc", "false");
                startActivityForResult(intent, VIEWSOCIO_REQUEST);
            }
        });
        new GetSocios().execute();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.usuarios_actionbar, menu);
        final MenuItem searchItem = menu.findItem(R.id.searchSocio);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //permite modificar el hint que el EditText muestra por defecto
        searchView.setQueryHint(getText(R.string.NombreDNI));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchSocios().execute(searchView.getQuery().toString());

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
            case R.id.socioOrderNombre:
                orederBy = R.id.socioOrderNombre;
                new GetSocios().execute();
                return true;
            case R.id.socioOrderApellidos:
                orederBy = R.id.socioOrderApellidos;
                new GetSocios().execute();
                return true;
            case R.id.socioOrderDni:
                orederBy = R.id.socioOrderDni;
                new GetSocios().execute();
                return true;
            case R.id.exportarSocios:
                createSociosCSV();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void createSociosCSV(){

        String result = "NOMBRE;APELLIDOS;DNI;TELEFONO;EMAIL;UPC\n";


        for(int i = 0; i < adapterSocios.getCount(); ++i) {
            Socio socio = (Socio) adapterSocios.getItem(i);

            result += socio.getNombre() + ";" + socio.getApellidos() + ";" +
                    socio.getDni() + ";" + String.valueOf(socio.getTelefono()) +
                    ";" + socio.getEmail() + ";";
            if(socio.isEsUPC()) result += "true" + "\n";
            else result += "false" + "\n";

        }
        File file = new File(getContext().getFilesDir(), "Socios.csv");
        if (file.exists()) {
            file.delete();
        }
        file.toURI();


        boolean created = false;

        FileOutputStream outputStream;

        try {
            outputStream = getContext().openFileOutput( "Socios.csv", Context.MODE_WORLD_READABLE);
            outputStream.write(result.getBytes());
            outputStream.close();
            created = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Socios.csv");
        intent.putExtra(intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivityForResult(Intent.createChooser(intent, "Save File"), 4);
    }

      /*private void createAlertExport() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.createdSocioCSV))
                .setMessage(getString(R.string.archivoCreadoEn))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEWSOCIO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                new GetSocios().execute();
            }
        }
        else if(requestCode == VIEWSOCIO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                new GetSocios().execute();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void inflateListSocios(List<Socio> socios) {
        adapterSocios = new AdaptarListSocios(getContext(), socios);
        listView.setAdapter(adapterSocios);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    //Show toast
    private void show(String s) {
        Toast.makeText(view.getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private class GetSocios extends AsyncTask<Void, Void, List<Socio>> {

        @Override
        protected List<Socio> doInBackground(Void... content) {

            try {
                switch (orederBy) {
                    case R.id.socioOrderNombre:
                        return mSocioTable.orderBy("nombre", QueryOrder.Ascending).execute().get();
                    case R.id.socioOrderApellidos:
                        return mSocioTable.orderBy("apellidos", QueryOrder.Ascending).execute().get();
                    case R.id.socioOrderDni:
                        return mSocioTable.orderBy("dni", QueryOrder.Ascending).execute().get();
                    default:
                        return mSocioTable.execute().get();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (ExecutionException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (MobileServiceException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            }
            List<Socio> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<Socio> result) {
            inflateListSocios(result);
        }
    }

    private class SearchSocios extends AsyncTask<String, Void, List<Socio>> {

        @Override
        protected List<Socio> doInBackground(String... content) {

            try {
                if (content.length > 0 && !content[0].isEmpty()) {

                    switch (orederBy) {
                        case R.id.socioOrderNombre:
                            return mSocioTable.where()
                                    .subStringOf(content[0], "nombre").
                                    or().subStringOf(content[0], "apellidos").
                                    or().subStringOf(content[0], "dni").
                                    orderBy("nombre", QueryOrder.Ascending).
                                    execute().get();
                        case R.id.socioOrderApellidos:
                            return mSocioTable.where()
                                    .subStringOf(content[0], "nombre").
                                            or().subStringOf(content[0], "apellidos").
                                            or().subStringOf(content[0], "dni").
                                            orderBy("apellidos", QueryOrder.Ascending).
                                            execute().get();
                        case R.id.socioOrderDni:
                            return mSocioTable.where()
                                    .subStringOf(content[0], "nombre").
                                            or().subStringOf(content[0], "apellidos").
                                            or().subStringOf(content[0], "dni").
                                            orderBy("dni", QueryOrder.Ascending).
                                            execute().get();
                        default:
                            return mSocioTable.where()
                                    .subStringOf(content[0], "nombre").
                                            or().subStringOf(content[0], "apellidos").
                                            or().subStringOf(content[0], "dni").
                                            execute().get();
                    }
                }else {
                    return mSocioTable.execute().get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (ExecutionException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (MobileServiceException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            }
            List<Socio> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<Socio> result) {
            inflateListSocios(result);
        }
    }


}
