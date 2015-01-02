package com.sincress.trashmaster;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
    private static String url_add_marker = "http://10.0.2.2/Trashmaster/add_marker.php";
    private static String url_update_marker = "http://10.0.2.2/Trashmaster/update_marker.php";
    private static String url_delete_marker = "http://10.0.2.2/Trashmaster/delete_marker.php";
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
        caller = (MapActivity) activity;
    }

    /**
     * This method will invoke an AsyncTask which will retrieve markers from the map and
     * place them in an ArrayList<MarkerEntry>. The callback will default to the
     * populateMapWithMarkers(ArrayList<MarkerEntry>) method.
     */
    public void getMarkersForArea(LatLng UL, LatLng LR) {
        new LoadMarkers(UL, LR).execute(); //load markers from the database
    }

    private class LoadMarkers extends AsyncTask<String, String, String> {
        /**
         * getting All markers from url
         */
        LatLng NE, SW;
        public LoadMarkers(LatLng ULbound, LatLng LRbound){
            NE = ULbound; //NORTHEAST
            SW = LRbound; //SOUTHWEST
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_markers, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Markers found
                    // Getting Array of Markers
                    markersRead = json.getJSONArray(TAG_MARKERS);
                    markers.clear(); //OR ELSE YOU GET LIKE 5 MILLION MARKERS
                    // looping through All Markers
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
                }
                    //ELSE no Markers found
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            for(int i=0; i<markers.size(); i++)
                if(markers.get(i).latitude > NE.latitude || markers.get(i).latitude < SW.latitude ||
                   markers.get(i).longitude > NE.longitude || markers.get(i).longitude < SW.longitude){
                    markers.remove(i);
                    i--;
                }
            caller.populateMapWithMarkers(markers); //callback function
            markers.clear(); //once again just in case
        }
    }

    /**
     * This method adds a marker to the database. The marker must be described by a MarkerEntry
     * class and all its fields must be filled. The callback will default to the
     * displayConfirmationMsg(String outcome) where outcome is "Success" or "Failure"
     * @param markerToAdd Marker to be added
     */
    public void addMarkerToDB(MarkerEntry markerToAdd) {
        new AddMarker(markerToAdd).execute(); //add marker to the database
    }

    private class AddMarker extends AsyncTask<String, String, String> {
        /**
         * adding a marker to DB on given url
         */
        private MarkerEntry markerToAdd;

        public AddMarker(MarkerEntry mkr) {
            markerToAdd = mkr;
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("downvotes", String.valueOf(markerToAdd.downvotes)));
            params.add(new BasicNameValuePair("longitude", String.valueOf(markerToAdd.longitude)));
            params.add(new BasicNameValuePair("latitude", String.valueOf(markerToAdd.latitude)));
            params.add(new BasicNameValuePair("upvotes", String.valueOf(markerToAdd.upvotes)));
            params.add(new BasicNameValuePair("type", String.valueOf(markerToAdd.type)));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_add_marker, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    caller.displayConfirmationMsg("Success");
                } else {
                    caller.displayConfirmationMsg("Failure");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void updateMarkerVotes(MarkerEntry markerToUpdt, String operation){
        if(operation.equals("Increment votes"))
            new UpdateMarker(markerToUpdt, "increment").execute();
        if(operation.equals("Decrement votes"))
            new UpdateMarker(markerToUpdt, "decrement").execute();
    }

    private class UpdateMarker extends AsyncTask<String, String, String> {
        /**
         * adding a marker to DB on given url
         */
        private MarkerEntry markerToUpdt;
        private String operation;

        public UpdateMarker(MarkerEntry mkr, String requiredOperation) {
            markerToUpdt = mkr;
            operation = requiredOperation;
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("downvotes", String.valueOf(markerToUpdt.downvotes)));
            params.add(new BasicNameValuePair("longitude", String.valueOf(markerToUpdt.longitude)));
            params.add(new BasicNameValuePair("latitude", String.valueOf(markerToUpdt.latitude)));
            params.add(new BasicNameValuePair("upvotes", String.valueOf(markerToUpdt.upvotes)));
            params.add(new BasicNameValuePair("type", String.valueOf(markerToUpdt.type)));
            params.add(new BasicNameValuePair("operation", operation));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_update_marker, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    caller.displayConfirmationMsg("Successfully reached DB");
                } else {
                    caller.displayConfirmationMsg("Failure in DB comms");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void deleteMarker(MarkerEntry markerToDelete){
        new DeleteMarker(markerToDelete).execute();
    }

    private class DeleteMarker extends AsyncTask<String, String, String> {
        /**
         * deleting a marker from database
         */
        private MarkerEntry markerToDel;

        public DeleteMarker(MarkerEntry mkr) {
            markerToDel = mkr;
        }

        protected String doInBackground(String... args) {
            // Building Parameters, we pass only lat and lng because they're the primary key
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            DecimalFormat df = new DecimalFormat("#.00000");
            Double coord = markerToDel.longitude;
            coord = Double.parseDouble(df.format(coord));
            params.add(new BasicNameValuePair("longitude", String.valueOf(coord)));
            coord = markerToDel.latitude;
            coord = Double.parseDouble(df.format(coord));
            params.add(new BasicNameValuePair("latitude", String.valueOf(coord)));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_delete_marker, "POST", params);
            markers.remove(markerToDel);

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    caller.displayConfirmationMsg("Successfully reached DB");
                } else {
                    caller.displayConfirmationMsg("Failure in DB comms");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
