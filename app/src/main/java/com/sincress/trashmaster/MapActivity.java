package com.sincress.trashmaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

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


public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng clickCoords;
    private Point clickPos, screenSize;
    private RadialMenuWidget typeSelectMenu, voteMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        screenSize  = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);

        initPieMenu();
    }

    private void putMarkerOnMap(int id) {
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
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        UiSettings uiSettings = mMap.getUiSettings();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                clickPos = mMap.getProjection().toScreenLocation(marker.getPosition());
                RelativeLayout rellay = (RelativeLayout) findViewById(R.id.rellay);
                voteMenu.setX(clickPos.x - screenSize.x / 2);
                voteMenu.setY(clickPos.y - screenSize.y / 2);
                voteMenu.show(rellay);
                return true; //return false for default behavior
            }
        });
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

    private void initPieMenu(){
        RadialMenuItem itemPlastic = new RadialMenuItem("0","Plastic");
        RadialMenuItem itemPaper = new RadialMenuItem("1","Paper");
        RadialMenuItem itemBio = new RadialMenuItem("2","Bio");
        RadialMenuItem itemGlass = new RadialMenuItem("3","Glass");
        RadialMenuItem itemMetal = new RadialMenuItem("4","Metal");
        RadialMenuItem voteUp = new RadialMenuItem("True","Vote Up");
        RadialMenuItem voteDown = new RadialMenuItem("False","Vote Down");

        typeSelectMenu = new RadialMenuWidget(MapActivity.this);
        typeSelectMenu.setOutlineColor(Color.BLACK, 225);
        typeSelectMenu.setInnerRingColor(0x33AA33, 180);
        typeSelectMenu.setOuterRingColor(0x0099CC, 180);

        voteMenu = new RadialMenuWidget(this);
        voteMenu.setOutlineColor(Color.BLACK, 225);
        voteMenu.setInnerRingColor(0x33AA33, 180);
        voteMenu.setOuterRingColor(0x0099CC, 180);
        voteMenu.setScaleX(0.7f);
        voteMenu.setScaleY(0.7f);
        voteMenu.setTextSize(20);
        voteMenu.setTextColor(Color.BLACK, 255);
        voteUp.setDisplayIcon(R.drawable.voteup);
        voteMenu.addMenuEntry(voteUp);
        voteUp.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                voteMenu.dismiss();
            }
        });
        voteDown.setDisplayIcon(R.drawable.votedown);
        voteMenu.addMenuEntry(voteDown);
        voteDown.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                voteMenu.dismiss();
            }
        });

        typeSelectMenu.addMenuEntry(itemPlastic);
        itemPlastic.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                typeSelectMenu.dismiss();
                putMarkerOnMap(0);
            }
        });
        typeSelectMenu.addMenuEntry(itemPaper);
        itemPaper.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                typeSelectMenu.dismiss();
                putMarkerOnMap(1);
            }
        });
        typeSelectMenu.addMenuEntry(itemBio);
        itemBio.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                typeSelectMenu.dismiss();
                putMarkerOnMap(2);
            }
        });
        typeSelectMenu.addMenuEntry(itemGlass);
        itemGlass.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                typeSelectMenu.dismiss();
                putMarkerOnMap(3);
            }
        });
        typeSelectMenu.addMenuEntry(itemMetal);
        itemMetal.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                typeSelectMenu.dismiss();
                putMarkerOnMap(4);
            }
        });
    }
}
