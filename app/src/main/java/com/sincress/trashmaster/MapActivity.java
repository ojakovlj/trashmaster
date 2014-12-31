package com.sincress.trashmaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuWidget;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuItem;

import java.util.ArrayList;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng clickCoords;
    private Point clickPos, screenSize;
    private RadialMenuWidget typeSelectMenu, voteMenu;
    private ServerCommunicator servComm;
    private ArrayList<MarkerEntry> markersOnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markersOnMap = new ArrayList<>();

        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);

        initPieMenu();
        servComm = new ServerCommunicator(this);
        servComm.getMarkersForArea();
    }

    /**
     * This function receives the callback from the instance of our ServerCommunicator when the
     * function getMarkersForArea is called
     * @param readFromDB
     */
    public final void populateMapWithMarkers(ArrayList<MarkerEntry> readFromDB){
        markersOnMap.addAll(readFromDB);
        for(int i=0; i<readFromDB.size(); i++) {
            Log.e("DB: ", "Got Marker: " + readFromDB.get(i).latitude + ", " + readFromDB.get(i).longitude);
            putMarkerOnMap(readFromDB.get(i));
        }
    }

    /**
     * This method receives the callback from the AsyncTask AddMarker in the ServerCommunicator.
     * It simply displays a message describing the outcome of the add-to-database operation
     * @param outcome
     */
    public void displayConfirmationMsg(final String outcome){
        runOnUiThread(new Runnable() {
            public void run() {
                if(outcome.equals("Success"))
                    Toast.makeText(MapActivity.this, "Marker successfully added to database!", Toast.LENGTH_SHORT).show();
                if(outcome.equals("Failure"))
                    Toast.makeText(MapActivity.this, "Error: unable to add marker to database!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method is used for putting markers on the map after they've been fetched from the database
     * @param thisMarker
     */
    private void putMarkerOnMap(MarkerEntry thisMarker) {
        Bitmap bmp;

        switch(thisMarker.type){
            case 0: bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_yellow);
                break;
            case 1: bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_blue);
                break;
            case 2: bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_brown);
                break;
            case 3: bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_grey);
                break;
            case 4: bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_green);
                break;
            default: bmp =  BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                break;
        }

        bmp = Bitmap.createScaledBitmap(bmp, 35, 35, false);
        LatLng markerPos = new LatLng(thisMarker.latitude, thisMarker.longitude);

        mMap.addMarker(new MarkerOptions()
                .position(markerPos)
                .title("Recycle Bin!"))
                .setIcon(BitmapDescriptorFactory.fromBitmap(bmp));
    }

    /**
     * This method is invoked when a long press is detected on the map. It places a marker on the map and
     * sends its data to the database where it will be stored.
     * @param id
     */
    private void addMarkerToMap(int id) {
        Bitmap bmp;

        switch(id){
            case 0: bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_yellow);
                break;
            case 1: bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_blue);
                break;
            case 2: bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_brown);
                break;
            case 3: bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_grey);
                break;
            case 4: bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_green);
                break;
            default: bmp =  BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                break;
        }

        bmp = Bitmap.createScaledBitmap(bmp, 35, 35, false);

        if(clickCoords != null)
            mMap.addMarker(new MarkerOptions()
                    .position(clickCoords)
                    .title("Recycle Bin!"))
                    .setIcon(BitmapDescriptorFactory.fromBitmap(bmp));

        MarkerEntry marker = new MarkerEntry();
        marker.longitude = clickCoords.longitude;
        marker.latitude = clickCoords.latitude;
        marker.upvotes = marker.downvotes = 0;
        marker.type = id;
        //Add the marker to the database as well!
        servComm.addMarkerToDB(marker);
        //Add marker to list of current markers on the map
        markersOnMap.add(marker);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        UiSettings uiSettings = mMap.getUiSettings();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        //set the onclicklistener for markers - show vote menu
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MarkerEntry thisMarker = null;
                int i;
                clickPos = mMap.getProjection().toScreenLocation(marker.getPosition());
                RelativeLayout rellay = (RelativeLayout) findViewById(R.id.rellay);

                //find MarkerEntry which represents our marker:
                for(i=0; i<markersOnMap.size(); i++){
                    thisMarker = markersOnMap.get(i);
                    if(thisMarker.latitude == marker.getPosition().latitude &&
                       thisMarker.longitude == marker.getPosition().longitude)
                        break;
                }
                //prepare the votemenu for displaying number of upvotes/downvotes

                RadialMenuItem voteUp = new RadialMenuItem("True","Good: "+thisMarker.upvotes);
                RadialMenuItem voteDown = new RadialMenuItem("False","Bad: "+thisMarker.downvotes);
                initVoteMenu();
                voteUp.setDisplayIcon(R.drawable.voteup);
                voteMenu.addMenuEntry(voteUp);
                //nuisance
                final MarkerEntry finalThisMarker = thisMarker;
                final int finalI = i;
                voteUp.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        servComm.updateMarkerVotes(finalThisMarker, "Increment votes");
                        markersOnMap.get(finalI).upvotes++;
                        voteMenu.dismiss();
                    }
                });
                voteDown.setDisplayIcon(R.drawable.votedown);
                voteMenu.addMenuEntry(voteDown);

                voteDown.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {

                        if(finalThisMarker.downvotes > 2 && Math.abs(finalThisMarker.upvotes-finalThisMarker.downvotes) > 4)
                            servComm.deleteMarker(finalThisMarker);
                        else{
                            servComm.updateMarkerVotes(finalThisMarker, "Decrement votes");
                            markersOnMap.get(finalI).downvotes++;
                        }
                        voteMenu.dismiss();
                    }
                });
                voteMenu.setX(clickPos.x - screenSize.x / 2);
                voteMenu.setY(clickPos.y - screenSize.y / 2);
                voteMenu.show(rellay);
                return true; //return false for default behavior
            }
        });
        //set the onlongclicklistener for adding markers to map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                clickPos = mMap.getProjection().toScreenLocation(latLng);
                RelativeLayout rellay = (RelativeLayout) findViewById(R.id.rellay);
                typeSelectMenu.setX(clickPos.x - screenSize.x / 2);
                typeSelectMenu.setY(clickPos.y - screenSize.y / 2);
                typeSelectMenu.show(rellay);
                clickCoords = latLng;
            }
        });

    }

    private void initVoteMenu() {
        voteMenu = new RadialMenuWidget(this);
        voteMenu.setOutlineColor(Color.BLACK, 225);
        voteMenu.setInnerRingColor(0x33AA33, 180);
        voteMenu.setOuterRingColor(0x0099CC, 180);
        voteMenu.setScaleX(0.7f);
        voteMenu.setScaleY(0.7f);
        voteMenu.setTextSize(20);
        voteMenu.setTextColor(Color.BLACK, 255);
    }

    private void initPieMenu(){
        RadialMenuItem itemPlastic = new RadialMenuItem("0","Plastic");
        RadialMenuItem itemPaper = new RadialMenuItem("1","Paper");
        RadialMenuItem itemBio = new RadialMenuItem("2","Bio");
        RadialMenuItem itemGlass = new RadialMenuItem("3","Glass");
        RadialMenuItem itemMetal = new RadialMenuItem("4","Metal");

        typeSelectMenu = new RadialMenuWidget(MapActivity.this);
        typeSelectMenu.setOutlineColor(Color.BLACK, 225);
        typeSelectMenu.setInnerRingColor(0x33AA33, 180);
        typeSelectMenu.setOuterRingColor(0x0099CC, 180);

        typeSelectMenu.addMenuEntry(itemPlastic);
        itemPlastic.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                typeSelectMenu.dismiss();
                addMarkerToMap(0);
            }
        });
        typeSelectMenu.addMenuEntry(itemPaper);
        itemPaper.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                typeSelectMenu.dismiss();
                addMarkerToMap(1);
            }
        });
        typeSelectMenu.addMenuEntry(itemBio);
        itemBio.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                typeSelectMenu.dismiss();
                addMarkerToMap(2);
            }
        });
        typeSelectMenu.addMenuEntry(itemGlass);
        itemGlass.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                typeSelectMenu.dismiss();
                addMarkerToMap(3);
            }
        });
        typeSelectMenu.addMenuEntry(itemMetal);
        itemMetal.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                typeSelectMenu.dismiss();
                addMarkerToMap(4);
            }
        });
    }
}
