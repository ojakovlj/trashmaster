package com.sincress.trashmaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuWidget;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuItem;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng clickCoords;
    private Point clickPos, screenSize;
    private RadialMenuWidget pieMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        screenSize  = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);

        RadialMenuItem itemPlastic = new RadialMenuItem("0","Plastic");
        RadialMenuItem itemPaper = new RadialMenuItem("1","Paper");
        RadialMenuItem itemBio = new RadialMenuItem("2","Bio");
        RadialMenuItem itemGlass = new RadialMenuItem("3","Glass");
        RadialMenuItem itemMetal = new RadialMenuItem("4","Metal");

        pieMenu = new RadialMenuWidget(MapActivity.this);
        pieMenu.setOutlineColor(Color.BLACK, 225);
        pieMenu.setInnerRingColor(0x33AA33, 180);
        pieMenu.setOuterRingColor(0x0099CC, 180);
        pieMenu.addMenuEntry(itemPlastic);
        itemPlastic.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                pieMenu.dismiss();
                putMarkerOnMap(0);
            }
        });
        pieMenu.addMenuEntry(itemPaper);
        itemPaper.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                pieMenu.dismiss();
                putMarkerOnMap(1);
            }
        });
        pieMenu.addMenuEntry(itemBio);
        itemBio.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                pieMenu.dismiss();
                putMarkerOnMap(2);
            }
        });
        pieMenu.addMenuEntry(itemGlass);
        itemGlass.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                pieMenu.dismiss();
                putMarkerOnMap(3);
            }
        });
        pieMenu.addMenuEntry(itemMetal);
        itemMetal.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                pieMenu.dismiss();
                putMarkerOnMap(4);
            }
        });
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
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                clickPos = mMap.getProjection().toScreenLocation(latLng);
                pieMenu.setVisibility(View.VISIBLE);
                RelativeLayout rellay = (RelativeLayout) findViewById(R.id.rellay);
                pieMenu.setX(clickPos.x-screenSize.x/2);
                pieMenu.setY(clickPos.y-screenSize.y/2);
                pieMenu.show(rellay);
                clickCoords = latLng;
            }
        });
    }
}
