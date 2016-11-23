package com.example.brand.p9;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by EmilSiegenfeldt on 23/11/2016.
 */

public class ServerTask extends AsyncTask<String, Void, JSONObject> {

    private final Callback callback;
    private final Context context;

    public ServerTask(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {

        JSONObject result = null;
        try {
            result = new JSONObject("string");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        //POST EXECUTE
    }
}
