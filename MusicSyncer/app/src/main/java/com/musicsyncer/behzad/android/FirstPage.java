package com.musicsyncer.behzad.android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;

public class FirstPage extends AppCompatActivity {
    BillingProcessor bp;
    Button bluetooth, wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        bluetooth = findViewById(R.id.Bluetooth);
        //bp = new BillingProcessor(this, null, this);
        wifi = findViewById(R.id.wifi);
        int Permission_All = 1;
        String[] Permmissions = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION};
        if (!hasPermission(this, Permmissions)) {
            ActivityCompat.requestPermissions(this, Permmissions, Permission_All);
        }





        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bp.purchase(FirstPage.this, "android.test.purchased");
                Intent purchased = new Intent(FirstPage.this, Bluetooth.class);
                purchased.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(purchased);
            }
        });
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternet() && checkLocation()){
                    Intent wifiactivity = new Intent(FirstPage.this, MainActivity.class);
                    wifiactivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(wifiactivity);
                }

            }
        });
    }
/**
    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(this, "Purchase was NOT successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
*/
    public static boolean hasPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT > -Build.VERSION_CODES.M && context != null && permissions != null) {
            for(String permission : permissions){
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }
    public boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
           return true;
        }
        else
            Toast.makeText(this, "Not connected to the network", Toast.LENGTH_SHORT).show();
        return false;
    }
    public boolean checkLocation(){
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled) {
            Toast.makeText(this, "Turn on location service", Toast.LENGTH_SHORT).show();
           return false;

        }

        return true;

    }
}

