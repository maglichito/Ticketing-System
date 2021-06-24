package com.example.ticketing_system.data;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {

    private Activity activity;

    public Network(Activity activity) {
        this.activity = activity;
    }

    // Checking if user is connected to any network ( Mobile or WiFi)
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if((wifiConn != null && wifiConn.isConnected()) || (mobileConn !=null && mobileConn.isConnected())){
            return true;
        }
        else{
            return false;
        }
    }
    // If user is connected to network we want to check if it is connected to internet
    // Expecting google servers always to be up
    public boolean checkConnectivity(){
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 1);
        } catch (Exception e) {
            return false;
        }
    }
}
