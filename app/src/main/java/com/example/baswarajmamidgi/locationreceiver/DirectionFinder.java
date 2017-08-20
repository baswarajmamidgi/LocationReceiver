package com.example.baswarajmamidgi.locationreceiver;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;


public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private static final String GOOGLE_API_KEY ="AIzaSyD5uUG0E8KJYeTXWIytfZcyIiloccpkh7M";
    private DirectionFinderListener listener;
    private LatLng origin;
    private LatLng destination;
    public DirectionFinder(DirectionFinderListener listener, LatLng origin, LatLng destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String originlat =String.valueOf(origin.latitude);
        String originlon=String.valueOf(origin.longitude);
        String Destinationlat= String.valueOf(destination.latitude);
        String Destinationlon=String.valueOf(destination.longitude);
        return DIRECTION_URL_API + "origins="+ originlat+","+originlon+ "&destinations="+Destinationlat+","+Destinationlon+"&key=" + GOOGLE_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                Log.i("log",buffer.toString());
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;
        Route route = new Route();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRows = jsonData.getJSONArray("rows");
        JSONObject jsonElements = jsonRows.getJSONObject(0);
        JSONArray element=jsonElements.getJSONArray("elements");
        JSONObject firstelement = element.getJSONObject(0);
        JSONObject jsonDistance = firstelement.getJSONObject("distance");
        JSONObject jsonDuration = firstelement.getJSONObject("duration");
        route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
        route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
        listener.onDirectionFinderSuccess(route);
    }
}
