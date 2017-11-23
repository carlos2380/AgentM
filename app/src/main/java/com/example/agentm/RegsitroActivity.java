package com.example.agentm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentm.Patterns.Utils;
import com.example.agentm.model.Registro;

import java.text.ParseException;

import static com.example.agentm.UpdateRegistroActivity.RESULT_DELETED;


public class RegsitroActivity extends AppCompatActivity {

    private TextView tvTitulo, tvDesc, tvPrecio, tvFecha;
    private ImageView imageView;
    private Registro registro;
    private String sFecha;

    private static final int UPDATEREGISTRO_REQUEST = 23;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsitro);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.activityRegistro_toolbar);
        myToolbar.inflateMenu(R.menu.view_actionbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.registro);

        tvTitulo = (TextView) findViewById(R.id.tituloViewReg);
        tvDesc = (TextView) findViewById(R.id.desViewReg);
        tvPrecio = (TextView) findViewById(R.id.precioViewReg);
        tvFecha = (TextView) findViewById(R.id.fechaViewReg);
        imageView = (ImageView) findViewById(R.id.fotoViewReg);

        registro = new Registro();
        registro.setId(getIntent().getExtras().getString("id"));
        registro.setTitulo(getIntent().getExtras().getString("titulo"));
        registro.setPrecio(Float.parseFloat(getIntent().getExtras().getString("precio")));

        if(getIntent().getExtras().getString("descripcion") != null && !getIntent().getExtras().getString("descripcion").isEmpty()) {
            registro.setDescripcion(getIntent().getExtras().getString("descripcion"));
        }

        try {
            registro.setFecha(Utils.stringToDate(getIntent().getExtras().getString("fecha")));
            sFecha = getIntent().getExtras().getString("fecha");
        }catch (ParseException e) {
            show(getString(R.string.errorFecha));
        }

        if(getIntent().getExtras().getString("foto") != null && !getIntent().getExtras().getString("foto").isEmpty()) {
            registro.setImagen(getIntent().getExtras().getString("foto"));
        }

        if(getIntent().getExtras().getString("modificada") != null && !getIntent().getExtras().getString("modificada").isEmpty()) {
            registro.setModifiedBy(getIntent().getExtras().getString("modificada"));
        }

        initViews();

    }

    private void initViews() {
        tvTitulo.setText(registro.getTitulo());

        if(registro.getDescripcion() != null && !registro.getDescripcion().isEmpty()){
            tvDesc.setText(registro.getDescripcion());
        }
        else {
            tvDesc.setVisibility(View.GONE);
        }

        if(sFecha != null && !sFecha.isEmpty()){
            tvFecha.setText(sFecha);
        }

        tvPrecio.setText(String.valueOf(registro.getPrecio()));

        if(registro.getImagen() != null && !registro.getImagen().isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(registro.getImagen());
            if(bitmap != null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            }else {
                imageView.setVisibility(View.GONE);
            }
        }else {
            imageView.setVisibility(View.GONE);
        }


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_actionbar, menu);
        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateToolbar:
                startActivityUpdateRegistro();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == UPDATEREGISTRO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                registro = new Registro();
                registro.setId(data.getExtras().getString("id"));
                registro.setTitulo(data.getExtras().getString("titulo"));
                registro.setPrecio(Float.parseFloat(data.getExtras().getString("precio")));

                if(data.getExtras().getString("descripcion") != null && !data.getExtras().getString("descripcion").isEmpty()) {
                    registro.setDescripcion(data.getExtras().getString("descripcion"));
                }

                try {
                    registro.setFecha(Utils.stringToDate(data.getExtras().getString("fecha")));
                    sFecha = data.getExtras().getString("fecha");
                }catch (ParseException e) {
                    show(getString(R.string.errorFecha));
                }

                if(data.getExtras().getString("foto") != null && !data.getExtras().getString("foto").isEmpty()) {
                    registro.setImagen(data.getExtras().getString("foto"));
                }else {
                    registro.setImagen(null);
                }

                if(data.getExtras().getString("modificada") != null && !data.getExtras().getString("modificada").isEmpty()) {
                    registro.setModifiedBy(data.getExtras().getString("modificada"));
                }
                initViews();
            }else if(resultCode == RESULT_DELETED) {
                setResult(RESULT_OK);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startActivityUpdateRegistro() {
        Intent intent = new Intent(this, UpdateRegistroActivity.class);
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

        startActivityForResult(intent, UPDATEREGISTRO_REQUEST);
    }
    //Show toast
    private void show(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
