package com.example.agentm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.Patterns.Utils;
import com.example.agentm.model.Registro;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpdateRegistroActivity extends AppCompatActivity {

    private MobileServiceClient mClient;
    private MobileServiceTable<Registro> mRegistroTable;

    EditText etTitulo, etDescripcion, etPrecio;
    RelativeLayout btnFecha, btnAddFoto, btnDelFoto;
    static TextView tvFecha;
    ImageView imageView;
    private String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 10;
    public static final int RESULT_DELETED = 600;
    private String sFoto, sFecha;
    private Registro registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registro);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.crearRegistro_toolbar);
        myToolbar.inflateMenu(R.menu.upate_actionbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.updateRegistro);

        mClient = ClientFactory.getInstance().getClient();
        mRegistroTable = mClient.getTable(Registro.class);

        etTitulo = (EditText) findViewById(R.id.edTituloReg);
        etDescripcion = (EditText) findViewById(R.id.edDescReg);
        etPrecio = (EditText) findViewById(R.id.edPrecioDes);
        btnFecha = (RelativeLayout) findViewById(R.id.rledFechaReg);
        btnAddFoto = (RelativeLayout) findViewById(R.id.rlFotoReg);
        btnDelFoto = (RelativeLayout) findViewById(R.id.rlDelFotoReg);
        tvFecha = (TextView) findViewById(R.id.tvFechaReg);
        imageView = (ImageView) findViewById(R.id.edImgFotoReg);

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        btnAddFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        btnDelFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddFoto.setVisibility(View.VISIBLE);
                btnDelFoto.setVisibility(View.GONE);
                imageView.setImageResource(android.R.color.transparent);
                imageView.setVisibility(View.GONE);
                sFoto = null;
            }
        });

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

        etTitulo.setText(registro.getTitulo());

        if(registro.getDescripcion() != null && !registro.getDescripcion().isEmpty()){
            etDescripcion.setText(registro.getDescripcion());
        }
        if(sFecha != null && !sFecha.isEmpty()){
            tvFecha.setText(sFecha);
        }

        etPrecio.setText(String.valueOf(registro.getPrecio()));

        if(registro.getImagen() != null && !registro.getImagen().isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(registro.getImagen());
            if(bitmap != null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
                btnDelFoto.setVisibility(View.VISIBLE);
                btnAddFoto.setVisibility(View.GONE);
            }else {
                imageView.setVisibility(View.GONE);
                btnDelFoto.setVisibility(View.GONE);
                btnAddFoto.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.upate_actionbar, menu);
        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveToolbar:
                validateAndSave();
                return true;
            case R.id.deleteToolbar:
                createAlertDelete();
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
        }else  if (etPrecio.getText().toString().isEmpty()){
            isValid= false;
            etPrecio.setError(getString(R.string.campoVacio));
        }

        if (isValid) {
            registro.setTitulo(etTitulo.getText().toString());
            if(!etDescripcion.getText().toString().isEmpty()){
                registro.setDescripcion(etDescripcion.getText().toString());
            }
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = format.parse(tvFecha.getText().toString());
            } catch (ParseException e) {
                show("Error Parse String To Date");
            }
            if (date != null)registro.setFecha(date);

            registro.setPrecio(Float.parseFloat(etPrecio.getText().toString()));

            if(btnAddFoto.getVisibility() == View.GONE) {
                registro.setImagen(sFoto);
            }else registro.setImagen(null);

            registro.setModifiedBy(mClient.getCurrentUser().getUserId().toString());

            List<Registro> registros = new ArrayList<>();
            registros.add(registro);
            new UpdateRegistro().execute(registro);
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
            c.set(year,month,day,0,0);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = format.format(c.getTime());
            tvFecha.setText(fecha);
        }
    }

    private void createAlertDelete() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.eliminarRegistro))
                .setMessage(getString(R.string.areYouSureDeleteRegistro))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        registro.setModifiedBy(mClient.getCurrentUser().getUserId().toString());
                        new DeleteRegistro().execute(registro);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageView.setVisibility(View.VISIBLE);
            btnAddFoto.setVisibility(View.GONE);
            btnDelFoto.setVisibility(View.VISIBLE);
            galleryAddPic();
            setPic();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                show("Error Take Foto");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.zumoappname.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {


        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        sFoto = mCurrentPhotoPath;
        imageView.setImageBitmap(bitmap);
    }
    //Show toast
    private void show(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }


    private class UpdateRegistro extends AsyncTask<Registro, Void, Void> {


        @Override
        protected Void doInBackground(Registro... socios) {
            if(socios.length > 0) mRegistroTable.update(socios[0]);
            else show(getString(R.string.ErrorSaveSocio));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Intent intent = new Intent();
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
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private class DeleteRegistro extends AsyncTask<Registro, Void, Void> {


        @Override
        protected Void doInBackground(Registro... socios) {
            if(socios.length > 0) mRegistroTable.update(socios[0]);
            if(socios.length > 0) mRegistroTable.delete(socios[0]);
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
