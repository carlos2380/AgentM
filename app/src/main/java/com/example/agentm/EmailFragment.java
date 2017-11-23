package com.example.agentm;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.model.Socio;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailFragment extends Fragment {

    MobileServiceClient mClient;
    private MobileServiceTable<Socio> mSocioTable;
    private View view;
    private TextView tvAsunto, tvContenido;
    private String[] emails;
    public EmailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_email, container, false);

        mClient = ClientFactory.getInstance().getClient();
        mSocioTable = mClient.getTable(Socio.class);
        new GetEmails().execute();

        tvAsunto = (TextView) view.findViewById(R.id.asunto);
        tvContenido = (TextView) view.findViewById(R.id.contenido);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.email_actionbar, menu);
         super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemSendEmail:
                    if(emails != null) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL  , emails);
                        i.putExtra(Intent.EXTRA_SUBJECT, tvAsunto.getText().toString());
                        i.putExtra(Intent.EXTRA_TEXT   , tvContenido.getText().toString());
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getContext(), "Error enviar email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetEmails extends AsyncTask<Void, Void, List<Socio>> {

        @Override
        protected List<Socio> doInBackground(Void... content) {

            try {
                return mSocioTable.select("email").execute().get();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }
            List<Socio> sError = new ArrayList<>();
            return sError;
        }

        @Override
        protected void onPostExecute(List<Socio> result) {

            emails = new String[result.size()];
            for (int i = 0; i < result.size(); ++i) {
                emails[i] = result.get(i).getEmail();
            }
        }
    }

}
