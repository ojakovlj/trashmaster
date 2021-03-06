package com.sincress.trashmaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuWidget;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuItem;

import java.util.ArrayList;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng clickCoords;
    private Point clickPos, screenSize;
    private RadialMenuWidget typeSelectMenu, voteMenu;
    private ServerCommunicator servComm;
    private ArrayList<MarkerEntry> markersOnMap, myRecentMarkers, myVotedMarkers; //myrecentmarkers - for "undo" operations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markersOnMap = new ArrayList<>();
        myRecentMarkers = new ArrayList<>();
        myVotedMarkers = new ArrayList<>();

        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);

        initPieMenu();
        servComm = new ServerCommunicator(this);
    }

    /**
     * This function receives the callback from the instance of our ServerCommunicator when the
     * function getMarkersForArea is called
     *
     * @param readFromDB Received list of marker entries from database
     */
    public final void populateMapWithMarkers(ArrayList<MarkerEntry> readFromDB) {
        markersOnMap.clear();
        mMap.clear();
        markersOnMap.addAll(readFromDB);
        for (int i = 0; i < readFromDB.size(); i++) {
            Log.i("DB: ", "Got Marker: " + readFromDB.get(i).latitude + ", " + readFromDB.get(i).longitude);
            putMarkerOnMap(readFromDB.get(i));
        }
    }

    /**
     * This method receives the callback from the AsyncTask AddMarker in the ServerCommunicator.
     * It simply displays a message describing the outcome of the add-to-database operation
     *
     * @param outcome "Success" or "Failure"
     */
    public void displayConfirmationMsg(final String outcome) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (outcome.equals("Success"))
                    Toast.makeText(MapActivity.this, "Marker successfully added to database!", Toast.LENGTH_SHORT).show();
                if (outcome.equals("Failure"))
                    Toast.makeText(MapActivity.this, "Error: unable to add marker to database!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method is used for putting markers on the map after they've been fetched from the database
     *
     * @param thisMarker Marker to be added to map
     */
    private void putMarkerOnMap(MarkerEntry thisMarker) {
        Bitmap bmp;

        switch (thisMarker.type) {
            case 0:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_yellow);
                break;
            case 1:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_blue);
                break;
            case 2:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_brown);
                break;
            case 3:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_grey);
                break;
            case 4:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_green);
                break;
            default:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
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
     *
     * @param id menu item ID
     */
    private void addMarkerToMap(int id) {
        Bitmap bmp;

        switch (id) {
            case 0:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_yellow);
                break;
            case 1:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_blue);
                break;
            case 2:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_brown);
                break;
            case 3:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_grey);
                break;
            case 4:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_green);
                break;
            default:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                break;
        }

        bmp = Bitmap.createScaledBitmap(bmp, 35, 35, false);

        if (clickCoords != null)
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
        myRecentMarkers.add(marker);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //get desired markers now, ASAP
        servComm.getMarkersForArea(mMap.getProjection().getVisibleRegion().latLngBounds.northeast,
                mMap.getProjection().getVisibleRegion().latLngBounds.southwest);

        UiSettings uiSettings = mMap.getUiSettings();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        //set the onclicklistener for markers - show vote menu
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                MarkerEntry thisMarker = null;
                int i;
                clickPos = mMap.getProjection().toScreenLocation(marker.getPosition());
                RelativeLayout rellay = (RelativeLayout) findViewById(R.id.rellay);

                //*************************************
                //find MarkerEntry which represents our marker:
                for (i = 0; i < markersOnMap.size(); i++) {
                    thisMarker = markersOnMap.get(i);
                    if (thisMarker.latitude == marker.getPosition().latitude &&
                            thisMarker.longitude == marker.getPosition().longitude)
                        break;
                }
                //nuisance
                final MarkerEntry foundMarker = thisMarker;
                final int posInMarkersList = i;

                //prepare the votemenu for displaying number of upvotes/downvotes, if not previously voted
                for (i = 0; i < myVotedMarkers.size(); i++) //if myRecentMarkers contains the clicked marker
                    if (myVotedMarkers.get(i).latitude == thisMarker.latitude &&
                            myVotedMarkers.get(i).longitude == thisMarker.longitude)
                        break;
                if (i < myVotedMarkers.size())  //if thisMarker is not found in myVotedMarkers, show vote menu
                    return true; //you cant vote and you sure cant delete it
                //**************************************

                RadialMenuItem voteUp = new RadialMenuItem("True", "Good: " + thisMarker.upvotes);
                RadialMenuItem voteDown = new RadialMenuItem("False", "Bad: " + thisMarker.downvotes);
                initVoteMenu();
                voteUp.setDisplayIcon(R.drawable.voteup);
                voteMenu.addMenuEntry(voteUp);
                voteUp.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        servComm.updateMarkerVotes(foundMarker, "Increment votes");
                        markersOnMap.get(posInMarkersList).upvotes++;
                        voteMenu.dismiss();
                        myVotedMarkers.add(foundMarker);
                    }
                });
                voteDown.setDisplayIcon(R.drawable.votedown);
                voteMenu.addMenuEntry(voteDown);

                voteDown.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {

                        if (foundMarker.downvotes > 2) { //delete it
                            servComm.deleteMarker(foundMarker);
                            markersOnMap.remove(posInMarkersList);
                            marker.setVisible(false);
                            marker.remove();
                        } else {
                            servComm.updateMarkerVotes(foundMarker, "Decrement votes");
                            markersOnMap.get(posInMarkersList).downvotes++;
                            myVotedMarkers.add(foundMarker);
                        }
                        voteMenu.dismiss();
                    }
                });

                voteMenu.setX(clickPos.x - screenSize.x / 2);
                voteMenu.setY(clickPos.y - screenSize.y / 2);

                //************************************
                //check if the user put the button there and if he did, he should be able to remove it as well
                RadialMenuItem deleteEntry = new RadialMenuItem("Delete", "Delete");
                deleteEntry.setDisplayIcon(R.drawable.x_red_delete);
                for (i = 0; i < myRecentMarkers.size(); i++) //if myRecentMarkers contains the clicked marker
                    if (myRecentMarkers.get(i).latitude == thisMarker.latitude &&
                            myRecentMarkers.get(i).longitude == thisMarker.longitude)
                        break;
                final int finalI2 = i;
                if (i < myRecentMarkers.size()) {
                    voteMenu.addMenuEntry(deleteEntry);
                    deleteEntry.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                        @Override
                        public void execute() {
                            myRecentMarkers.remove(finalI2);
                            servComm.deleteMarker(foundMarker);
                            markersOnMap.remove(posInMarkersList);
                            marker.setVisible(false);
                            marker.remove();
                            voteMenu.dismiss();
                        }
                    });
                }
                //=====================================

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
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                LatLng upperRight = bounds.northeast;
                LatLng lowerLeft = bounds.southwest;
                servComm.getMarkersForArea(upperRight, lowerLeft);
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener()
        {
            @Override
            public boolean onMyLocationButtonClick()
            {
                // Getting LocationManager object from System Service LOCATION_SERVICE
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                // Creating a criteria object to retrieve provider
                Criteria criteria = new Criteria();
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_COARSE);
                // Getting the name of the best provider
                String provider = locationManager.getBestProvider(criteria, true);
                // Getting Current Location
                Location location = locationManager.getLastKnownLocation(provider);
                if(location != null) {
                    LatLng ll = new LatLng(location.getLatitude(), location.getLatitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 20);
                    mMap.animateCamera(update);
                }
                else
                    Toast.makeText(MapActivity.this, "Current location unavailable",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    /**
     * This method initialises the radial menu for upvoting or downvoting individual markers, called by a tap on the marker.
     */
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

    /**
     * This method initialises the radial menu for adding markers, called by a long hold on the map.
     */
    private void initPieMenu() {
        RadialMenuItem itemPlastic = new RadialMenuItem("0", "Plastic");
        RadialMenuItem itemPaper = new RadialMenuItem("1", "Paper");
        RadialMenuItem itemBio = new RadialMenuItem("2", "Bio");
        RadialMenuItem itemGlass = new RadialMenuItem("3", "Metal");
        RadialMenuItem itemMetal = new RadialMenuItem("4", "Glass");

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
