package com.example.agentm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.agentm.Patterns.ClientFactory;
import com.microsoft.windowsazure.mobileservices.MobileServiceActivityResult;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

public class LoginActivity extends AppCompatActivity {

    // You can choose any unique number here to differentiate auth providers from each other. Note this is the same code at login() and onActivityResult().
    public static final int AAD_LOGIN_REQUEST_CODE = 1;
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializar Mobile Client
        try {
            ClientFactory clientFactory = ClientFactory.getInstance();
            clientFactory.initClient(this);
            mClient = clientFactory.getClient();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            show(getString(R.string.ErrorConectarServicio));
        }
        authenticate();
    }

    //Authentificacion MobileServiceClient
    private void authenticate() {
        // Login using the Google provider.
        mClient.login("windowsazureactivedirectory", "zumoagentm2017", AAD_LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When request completes
        if (resultCode == RESULT_OK) {
            // Check the request code matches the one we send in the login request
            if (requestCode == AAD_LOGIN_REQUEST_CODE) {
                MobileServiceActivityResult result = ClientFactory.getInstance().getClient().onActivityResult(data);
                if (result.isLoggedIn()) {
                    // login succeeded
                    //show(String.format("You are now logged in - %1$2s", mClient.getCurrentUser().getUserId()) + "Success");
                    Intent intent = new Intent(this, DrawerActivity.class);
                    startActivityForResult(intent, 444);

                } else {
                    // login failed, check the error message
                    String errorMessage = result.getErrorMessage();
                    show(errorMessage + "Error");
                    finish();
                }
            }else {
                finish();
            }
        }else {
            finish();
        }
    }

    //Show toast
    private void show(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
