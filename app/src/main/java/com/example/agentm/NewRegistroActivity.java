package com.example.agentm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.example.agentm.Patterns.Utils;
import com.example.agentm.model.Registro;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.agentm.BalanceFragment.getRegistrosDesdeHasta;

public class NewRegistroActivity extends AppCompatActivity {

    private MobileServiceClient mClient;
    private MobileServiceTable<Registro> mRegistroTable;

    EditText etTitulo, etDescripcion, etPrecio;
    RelativeLayout btnFecha, btnAddFoto, btnDelFoto;
    static TextView tvFecha;
    ImageView imageView;
    private String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 10;
    private String sFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registro);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.crearRegistro_toolbar);
        myToolbar.inflateMenu(R.menu.create_actionbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.crearregistro);

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

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = format.format(c.getTime());
        tvFecha.setText(fecha);
        sFoto = null;
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
        }else  if (etPrecio.getText().toString().isEmpty()){
            isValid= false;
            etPrecio.setError(getString(R.string.campoVacio));
        }

        if (isValid) {
            Registro newRegistro = new Registro();
            newRegistro.setTitulo(etTitulo.getText().toString());
            if(!etDescripcion.getText().toString().isEmpty()){
                newRegistro.setDescripcion(etDescripcion.getText().toString());
            }
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = format.parse(tvFecha.getText().toString());
            } catch (ParseException e) {
                show("Error Parse String To Date");
            }
            if (date != null)newRegistro.setFecha(date);

            newRegistro.setPrecio(Float.parseFloat(etPrecio.getText().toString()));

            if(btnAddFoto.getVisibility() == View.GONE) {
                newRegistro.setImagen(sFoto);
            }
            newRegistro.setModifiedBy(mClient.getCurrentUser().getUserId().toString());

            List<Registro> registros = new ArrayList<>();
            registros.add(newRegistro);
            new CreateRegistro().execute(newRegistro);
        }
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

    /*private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/

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
        //storeImageInBlobStorage(mCurrentPhotoPath);
        //new Up().execute();
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {


        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        sFoto = mCurrentPhotoPath;
        imageView.setImageBitmap(bitmap);
    }

    private static final String storageURL = "https://agentmblob.blob.core.windows.net/";
    private static final String storageContainer = "imagenes";
    private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=agentmblob;AccountKey=3SR9+61hbL9/hbT3uiQVzrjpL0pbVA1rA1GKBqIzPl8M8gU/h+xQXKik85Xo8Dz29a3wl2Yb1D4O/JJuhzZoaQ==;EndpointSuffix=core.windows.net";

    protected void storeImageInBlobStorage(String imgPath){

    }
    //Show toast
    private void show(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
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

    private class CreateRegistro extends AsyncTask<Registro, Void, Void> {


        @Override
        protected Void doInBackground(Registro... registros) {
            if(registros.length > 0) mRegistroTable.insert(registros[0]);
            else show(getString(R.string.ErrorSaveSocio));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private class Up extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... voids) {
            try
            {
                // Retrieve storage account from connection-string.
                CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                // Create the blob client.
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                // Retrieve reference to a previously created container.
                CloudBlobContainer container = blobClient.getContainerReference(storageContainer);

                // Create or overwrite the blob (with the name "example.jpeg") with contents from a local file.
                CloudBlockBlob blob = container.getBlockBlobReference("example.jpg");
                File source = new File(mCurrentPhotoPath);
                blob.upload(new FileInputStream(source), source.length());
            }
            catch (Exception e)
            {
                // Output the stack trace.
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
