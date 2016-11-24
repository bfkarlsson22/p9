package com.example.brand.p9;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by EmilSiegenfeldt on 23/11/2016.
 */

public class ServerTask extends AsyncTask<String, Void, JSONObject> {

    private final Callback callback;
    private final Context context;
    private final HashMap<String, String> parameters;

    public ServerTask(Context context, Callback callback, HashMap<String, String> parameters){
        this.context = context;
        this.callback = callback;
        this.parameters = parameters;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result = null;
        try {
            URL url = new URL("http://p9.emilsiegenfeldt.dk/patients/functions/"+params[0]+".php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            Uri.Builder builder = new Uri.Builder();

            for (Map.Entry<String, String> entry : parameters.entrySet()){
                builder.appendQueryParameter(entry.getKey(),entry.getValue());
            }

            String query = builder.build().getEncodedQuery();
            OutputStream os = connection.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            connection.connect();
            InputStream in = new BufferedInputStream(connection.getInputStream());

            String response = IOUtils.toString(in, "UTF-8");
            Log.d("RESPONSE ASYNC",response);
            result = new JSONObject(response);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        callback.serverTaskDone(result);
    }
}
