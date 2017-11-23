package com.example.agentm;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.Patterns.Utils;
import com.example.agentm.adapters.AdapterCardActividad;
import com.example.agentm.adapters.RecyclerItemClickListener;
import com.example.agentm.model.Actividad;
import com.example.agentm.model.Socio;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActividadFragment extends Fragment {

    private View view;
    private MobileServiceClient mClient;
    private MobileServiceTable<Actividad> mActividadTable;
    private static RecyclerView recycler, recycler2;
    private static RecyclerView.Adapter adapter, adapter2;
    private static RecyclerView.LayoutManager lManager, lManager2;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton btnNewActividad;
    private List<Actividad> actividades, actividades2;
    private TextView tvMesCalendar, tvAnyCalendar;
    private LinearLayout llCalendario;

    private CompactCalendarView compactCalendarView;;

    public static final int NEWACTIVIDAD_REQUEST = 1;
    public static final int VIEWACTIVIDAD_REQUEST = 76;
    public ActividadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_actividad, container, false);

        mClient = ClientFactory.getInstance().getClient();
        mActividadTable = mClient.getTable(Actividad.class);

        recycler = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recycler2 = (RecyclerView) view.findViewById(R.id.my_recycler_view2);
        btnNewActividad = (FloatingActionButton) view.findViewById(R.id.btnNewActividad);
        actividades = new ArrayList<>();
        actividades2 = new ArrayList<>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutActividad);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                new GetActividades().execute();
            }
        });

        llCalendario = (LinearLayout) view.findViewById(R.id.calendarioRelative);
        llCalendario.setVisibility(View.GONE);
        new GetActividades().execute();

        btnNewActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewActividadActivity.class);
                startActivityForResult(intent, NEWACTIVIDAD_REQUEST);
            }
        });

        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {

                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), ActividadActivity.class);
                        Actividad actividad = actividades.get(position);
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

                        startActivityForResult(intent, VIEWACTIVIDAD_REQUEST);
                    }
                })
        );

        recycler2.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {

                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), ActividadActivity.class);
                        Actividad actividad = actividades2.get(position);
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

                        startActivityForResult(intent, VIEWACTIVIDAD_REQUEST);
                    }
                })
        );

        compactCalendarView = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        tvMesCalendar = (TextView) view.findViewById(R.id.mesCalendar);
        tvAnyCalendar = (TextView) view.findViewById(R.id.anyCalendar);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);



        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        //Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
        tvMesCalendar.setText(Utils.intMonthToString(month));
        tvAnyCalendar.setText(String.valueOf(year));
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                if(events.size() >0) {
                    List<Actividad> acts = new ArrayList<Actividad>();
                    for (int i = 0; i < events.size(); ++i) {
                        Actividad a = (Actividad) events.get(i).getData();
                        acts.add(a);
                    }
                    recycler2 = (RecyclerView) view.findViewById(R.id.my_recycler_view2);
                    recycler2.setHasFixedSize(true);

                    // Usar un administrador para LinearLayout
                    lManager2 = new LinearLayoutManager(view.getContext());
                    recycler2.setLayoutManager(lManager2);

                    adapter2 = new AdapterCardActividad(acts);
                    actividades2 = acts;
                    recycler2.setAdapter(adapter2);
                }
            }

            @Override
            public void onMonthScroll(Date date) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                //Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                tvMesCalendar.setText(Utils.intMonthToString(month));
                tvAnyCalendar.setText(String.valueOf(year));
            }
        });


        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEWACTIVIDAD_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                new GetActividades().execute();
            }
        }else if (requestCode == VIEWACTIVIDAD_REQUEST){
            if (resultCode == RESULT_OK) {
                new GetActividades().execute();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void inflateRecyclerActividad(List<Actividad> actividads) {
        mSwipeRefreshLayout.setRefreshing(false);
        recycler = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recycler.setHasFixedSize(true);

        actividades = actividads;
        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(view.getContext());
        recycler.setLayoutManager(lManager);

        adapter = new AdapterCardActividad(actividads);
        recycler.setAdapter(adapter);
    }

    private void inflateClendar(List<Actividad> actividads) {
        compactCalendarView.removeAllEvents();
        for(int i = 0;i < actividads.size(); ++i) {
            Actividad a = actividads.get(i);
            Event e = new Event(Color.GREEN,a.getFecha().getTime(), a);
            compactCalendarView.addEvent(e);
        }
    }
    //Show toast
    private void show(String s) {
        Toast.makeText(view.getContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actividad_actionbar, menu);
        // super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calendarToolbar:
                if(llCalendario.getVisibility() == View.GONE) {
                    llCalendario.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setVisibility(View.GONE);
                    item.setIcon(R.drawable.ic_view_headline_white_24dp);
                }else {
                    llCalendario.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    item.setIcon(R.drawable.ic_date_range_white_24dp);
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetActividades extends AsyncTask<Void, Void, List<Actividad>> {

        @Override
        protected List<Actividad> doInBackground(Void... content) {

            try {
                return mActividadTable.orderBy("fecha", QueryOrder.Descending).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            } catch (ExecutionException e) {
                e.printStackTrace();
                show(getString(R.string.ErrorGetSocios));
            }
            List<Actividad> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<Actividad> result) {
            inflateRecyclerActividad(result);
            inflateClendar(result);
        }
    }

}
