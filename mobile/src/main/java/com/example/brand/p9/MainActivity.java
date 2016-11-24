package com.example.brand.p9;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String COUNT_KEY = "com.example.key.count";

    private int count = 0;
    private int steps;
    private String TAG = "HELLO";
    public GoogleApiClient mGoogleApiClient;
    private boolean nodeConnected = false;
    private Node connectedNode;
    private Uri uri;
    private String path = "/count";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();




    }
    private void getData() {
        Uri uri = new Uri.Builder()
                .scheme(PutDataRequest.WEAR_URI_SCHEME)
                .path("wear://*/count")
                .build();


        Wearable.DataApi.getDataItems(mGoogleApiClient, uri)
                .setResultCallback(new ResultCallback<DataItemBuffer>() {
                                       @Override
                                       public void onResult(DataItemBuffer dataItems) {
                                           for(int i=0;i<dataItems.getCount();i++)
                                           {
                                               Log.d("WEAR APP", "The data is from: " +           dataItems.get(i).getUri().getAuthority());
                                               DataMap data = DataMap.fromByteArray(dataItems.get(i).getData());
                                               String data1 = data.getString("data");
                                               Log.d(TAG, data1);
                                           }
                                       }
                                   }
                );
    }
    private void getConnectedNode()
    {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    connectedNode = node;
                }
            }
        });
    }

    private void buildUri(){
         uri = new Uri.Builder()
                .scheme(PutDataRequest.WEAR_URI_SCHEME)
                .path("/count")
                .authority(connectedNode.getId())
                .build();
    }

    private void getStoredData(){
        Wearable.DataApi.getDataItems(mGoogleApiClient, uri)
                .setResultCallback(new ResultCallback<DataItemBuffer>() {
                                       @Override
                                       public void onResult(DataItemBuffer dataItems) {
                                           for(int i=0;i<dataItems.getCount();i++)
                                           {
                                               Log.d("WEAR APP", "The data is from: " +           dataItems.get(i).getUri().getAuthority());
                                               DataMap data = DataMap.fromByteArray(dataItems.get(i).getData());
                                               String data1 = data.getString("data");
                                               Log.d(TAG, data1);
                                           }
                                       }
                                   }
                );
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: " + bundle);
        // Now you can use the Data Layer API
        nodeConnected = true;
        Wearable.DataApi.addListener(mGoogleApiClient, this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: " + i);
        nodeConnected = false;

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
        nodeConnected = false;
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/count") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateCount(dataMap.getInt(COUNT_KEY));

                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    public void updateCount(int c){
        Log.d("3333", String.valueOf(c));
    }

}
