package com.sincress.trashmaster;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Plastic");
        spinnerArray.add("Paper");
        spinnerArray.add("Bio");
        spinnerArray.add("Metal");
        spinnerArray.add("Glass");

        typeSelector = new Spinner(this);
        typeSelector.setVisibility(View.INVISIBLE);
        RelativeLayout rellay = (RelativeLayout) findViewById(R.id.rellay);
        rellay.addView(typeSelector);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        typeSelector.setAdapter(spinnerArrayAdapter);
        typeSelector.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 40);
        params.leftMargin = 300;
        params.topMargin = 300;
        typeSelector.setLayoutParams(params);
        typeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSelector.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_blue);
                bmp = Bitmap.createScaledBitmap(bmp, 20, 20, false);

                typeSelector.setVisibility(View.VISIBLE);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Marker"))
                        .setIcon(BitmapDescriptorFactory.fromBitmap(bmp));
            }
        });
    }
}
