package com.sincress.trashmaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuWidget;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuItem;

import android.widget.Toast;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class MenuActivity extends ActionBarActivity {

    private String waste = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button launchMap = (Button) findViewById(R.id.launch_map);
        Button launchOverview = (Button) findViewById(R.id.launch_overview);
        launchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                        || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    Intent intent = new Intent(MenuActivity.this, MapActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MenuActivity.this, "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        launchOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, OverviewActivity.class);
                startActivity(intent);
            }
        });

        // neka random zanimljivost
        try {
            InputStream is = getResources().getAssets().open("info.txt");
            TextView tx = (TextView) findViewById(R.id.zanimljivosti);
            BufferedReader rS = new BufferedReader(new InputStreamReader(is));
            String lin;
            int cnt = 0;
            while ((lin = rS.readLine()) != null) {
                cnt += 1;
            }
            is.close();
            rS.close();

            InputStream is2 = getResources().getAssets().open("info.txt");
            BufferedReader rS2 = new BufferedReader(new InputStreamReader(is2));
            Random r = new Random();
            int i = r.nextInt(cnt);
            int a = 0;
            for (a = 0; a < i; a++) {
                lin = rS2.readLine();
            }
            tx.setText(lin);
            is2.close();
            rS2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // zabilješke bacanaj otpada

        final RadialMenuWidget recMenu;
        final RadialMenuWidget massMenu;

        recMenu = new RadialMenuWidget(this);
        recMenu.setAnimationSpeed(1000);
        recMenu.setSelectedColor(Color.rgb(10, 250, 15), 255);
        recMenu.setId(R.id.radialLogMenu); //we set an ID which will be used in the MenuActivityTest
        RadialMenuItem bioItem = new RadialMenuItem("bio", "Biowaste");
        recMenu.addMenuEntry(bioItem);
        RadialMenuItem petItem = new RadialMenuItem("pet", "Plastic");
        recMenu.addMenuEntry(petItem);
        RadialMenuItem glassItem = new RadialMenuItem("glass", "Glass");
        recMenu.addMenuEntry(glassItem);
        RadialMenuItem metItem = new RadialMenuItem("met", "Metal");
        recMenu.addMenuEntry(metItem);
        final RadialMenuItem paperItem = new RadialMenuItem("paper", "Paper");
        recMenu.addMenuEntry(paperItem);

        massMenu = new RadialMenuWidget(this);
        massMenu.setHeader("Mass", 12);
        massMenu.setAnimationSpeed(1000);
        massMenu.setSelectedColor(Color.rgb(10, 250, 15), 255);
        RadialMenuItem closeItem = new RadialMenuItem("close", "close");
        massMenu.setCenterCircle(closeItem);
        RadialMenuItem g1Item = new RadialMenuItem("1", "1g");
        massMenu.addMenuEntry(g1Item);
        RadialMenuItem g10Item = new RadialMenuItem("10", "10g");
        massMenu.addMenuEntry(g10Item);
        RadialMenuItem g50Item = new RadialMenuItem("50", "50g");
        massMenu.addMenuEntry(g50Item);
        RadialMenuItem g100Item = new RadialMenuItem("100", "100g");
        massMenu.addMenuEntry(g100Item);
        RadialMenuItem g500Item = new RadialMenuItem("500", "500g");
        massMenu.addMenuEntry(g500Item);
        RadialMenuItem g1000Item = new RadialMenuItem("1000", "1000g");
        massMenu.addMenuEntry(g1000Item);

        Button recButton = (Button) findViewById(R.id.launch_record);

        recButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recMenu.show(v);
            }
        });


        final SharedPreferences pref = getApplicationContext().getSharedPreferences("trashmaster", 0);
        final SharedPreferences.Editor editor = pref.edit(); // used for save data

        bioItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                waste = "bio";
                massMenu.show(findViewById(android.R.id.content));
                recMenu.dismiss();
                recMenu.destroyDrawingCache();
            }
        });

        petItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                waste = "pet";
                massMenu.show(findViewById(android.R.id.content));
                recMenu.dismiss();
            }
        });

        glassItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                waste = "glass";
                massMenu.show(findViewById(android.R.id.content));
                recMenu.dismiss();
            }
        });

        metItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                waste = "met";
                massMenu.show(findViewById(android.R.id.content));
                recMenu.dismiss();
            }
        });

        paperItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                waste = "paper";
                massMenu.show(findViewById(android.R.id.content));
                recMenu.dismiss();
            }
        });

        closeItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                massMenu.dismiss();
            }
        });

        g1Item.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                editor.putLong(waste, pref.getLong(waste, 0) + 1);
                editor.commit();
                Toast.makeText(MenuActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        g10Item.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                editor.putLong(waste, pref.getLong(waste, 0) + 10);
                editor.commit();
                Toast.makeText(MenuActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        g50Item.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                editor.putLong(waste, pref.getLong(waste, 0) + 50);
                editor.commit();
                Toast.makeText(MenuActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        g100Item.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                editor.putLong(waste, pref.getLong(waste, 0) + 100);
                editor.commit();
                Toast.makeText(MenuActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        g500Item.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                editor.putLong(waste, pref.getLong(waste, 0) + 500);
                editor.commit();
                Toast.makeText(MenuActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        g1000Item.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                editor.putLong(waste, pref.getLong(waste, 0) + 1000);
                editor.commit();
                Toast.makeText(MenuActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.reset_record) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("trashmaster", 0);
                    SharedPreferences.Editor editor = pref.edit(); // used for save data
                    editor.clear();
                    editor.commit();
                    Toast.makeText(MenuActivity.this, "Reset!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
