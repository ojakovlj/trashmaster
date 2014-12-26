package com.sincress.trashmaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by OJ on 25-Dec-14.
 * Used for communication with database (CRUD operations):
 * 1) Adding markers to database
 * 2) Updating marker votes
 * 3) Deleting downvoted markers
 * 4) Fetching local markers to display
 * Uses JSONParser class for making POST/GET HTTP requests
 */
public class ServerCommunicator {

    private JSONParser jParser = new JSONParser();
    private static String url_get_markers = "http://10.0.2.2/Trashmaster/get_markers.php"; //TODO
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TYPE = "type";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_UPVOTES = "upvotes";
    private static final String TAG_DOWNVOTES = "downvotes";
    private static final String TAG_MARKERS = "markers";
    private JSONArray markersRead;
    private ArrayList<MarkerEntry> markers = new ArrayList<>();

    MapActivity caller;
    public ServerCommunicator(Activity activity) {
        caller = (MapActivity)activity;
    }

    public void getMarkersForArea(/*TODO*/){
        new LoadMarkers().execute(); //load markers from the database
    }

    private class LoadMarkers extends AsyncTask<String, String, String> {
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_markers, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    markersRead = json.getJSONArray(TAG_MARKERS);

                    // looping through All Products
                    for (int i = 0; i < markersRead.length(); i++) {
                        JSONObject c = markersRead.getJSONObject(i);

                        MarkerEntry currentMarker = new MarkerEntry();
                        currentMarker.downvotes = c.getInt(TAG_DOWNVOTES);
                        currentMarker.longitude = c.getDouble(TAG_LONGITUDE);
                        currentMarker.latitude = c.getDouble(TAG_LATITUDE);
                        currentMarker.upvotes = c.getInt(TAG_UPVOTES);
                        currentMarker.type = c.getInt(TAG_TYPE);
                        markers.add(currentMarker);
                        // Storing each json item in variable
                    }
                } else {
                    // no products found
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            caller.populateMapWithMarkers(markers); //callback function
        }
    }
}
