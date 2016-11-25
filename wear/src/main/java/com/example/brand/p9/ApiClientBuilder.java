package com.example.brand.p9;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by EmilSiegenfeldt on 25/11/2016.
 */

public class ApiClientBuilder{

    private Context context;

    public ApiClientBuilder(Context context){
        this.context = context;
    }
    public GoogleApiClient buildClient(){
        GoogleApiClient mGoogleApiClient;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d("Connected","Onconnected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d("Connection suspsended","connection suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("Connection failed","connect failed "+connectionResult);
                    }
                })
                .build();
        mGoogleApiClient.connect();
        return mGoogleApiClient;
    }

}
