package com.example.brand.p9;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApiClientBuilder{

    private Context context;
    GoogleApiClient mGoogleApiClient;

    public ApiClientBuilder(Context context){
        this.context = context;
    }

    public GoogleApiClient buildClient(){

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
