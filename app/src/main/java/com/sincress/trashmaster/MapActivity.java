package com.sincress.trashmaster;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Spinner typeSelector;
    private LatLng clickCoords;
    private Point clickPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        typeSelector = (Spinner) findViewById(R.id.spinner);
        typeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSelector.setVisibility(View.INVISIBLE);
                putMarkerOnMap((int)id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                typeSelector.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 40);
                params.leftMargin =  clickPos.x;
                params.topMargin = clickPos.y;
                typeSelector.setLayoutParams(params);
                clickCoords = latLng;
            }
        });
    }
}
