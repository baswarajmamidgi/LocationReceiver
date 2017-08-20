package com.example.baswarajmamidgi.locationreceiver;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {
    public static ArrayList<String> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
       final boolean isconnected= networkInfo!=null && networkInfo.isConnectedOrConnecting();
        if(!isconnected)

        {
            Toast.makeText(this, "No Internet..Check your internet Connection and try again ", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }


    }


}
