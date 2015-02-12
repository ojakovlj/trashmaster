package com.sincress.trashmaster;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Random;


public class OverviewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);


        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        final TabWidget tabWidget = tabHost.getTabWidget();
        final FrameLayout tabContent = tabHost.getTabContentView();

        // Get the original tab textviews and remove them from the viewgroup.
        TextView[] originalTextViews = new TextView[tabWidget.getTabCount()];
        for (int i = 0; i < tabWidget.getTabCount(); i++) {
            originalTextViews[i] = (TextView) tabWidget.getChildTabViewAt(i);
        }
        tabWidget.removeAllViews();

        // neka random zanimljivost
        DidYouKnow();

        //show records and stats
        ShowStats();

        // Ensure that all tab content childs are not visible at startup.
        for (int i = 0; i < tabContent.getChildCount(); i++) {
            tabContent.getChildAt(i).setVisibility(View.GONE);
        }

        // Create the tabspec based on the textview childs in the xml file.
        // Or create simple tabspec instances in any other way...
        for (int i = 0; i < originalTextViews.length; i++) {
            final TextView tabWidgetTextView = originalTextViews[i];
            final View tabContentView = tabContent.getChildAt(i); //each tab has its own linearlayout
            //TabSpec = content + indicator + tag
            TabHost.TabSpec tabSpec = tabHost.newTabSpec((String) tabWidgetTextView.getTag());
            tabSpec.setContent(new TabHost.TabContentFactory() { //CONTENT
                @Override
                public View createTabContent(String tag) {
                    return tabContentView;
                }
            });
            if (tabWidgetTextView.getBackground() == null) {
                tabSpec.setIndicator(tabWidgetTextView.getText()); //INDICATOR
            } else {
                tabSpec.setIndicator(tabWidgetTextView.getText(), tabWidgetTextView.getBackground());
            }
            tabHost.addTab(tabSpec);
        }
		tabHost.setCurrentTab(0);
    }

    /**
     * This method reads random fun fact from local file and prints it on the screen
     * on stats tab
     */
    private void DidYouKnow(){
        try {
            InputStream is = getResources().getAssets().open("info.txt");
            TextView tx = (TextView) findViewById(R.id.infoOS);
            BufferedReader rS = new BufferedReader(new InputStreamReader(is));
            String lin;
            int cnt=0;
            while ((lin = rS.readLine())!=null)
            {
                cnt+=1;
            }
            is.close();
            rS.close();

            InputStream is2 = getResources().getAssets().open("info.txt");
            BufferedReader rS2 = new BufferedReader(new InputStreamReader(is2));
            Random r = new Random();
            int i = r.nextInt(cnt);
            int a=0;
            for (a=0;a<i;a++)
            {
                lin = rS2.readLine();
            }
            tx.setText("\nDid you know?\n - "+lin);
            is2.close();
            rS2.close();
        }catch (Exception e) {}
    }

    /**
     * This method shows stats of waste disposal on client device.
     * Dividing waste per group and showing mass of disposed waste for every group
     * in records tab and sum of all groups in stats tab
     */
    private void ShowStats(){
        TextView rc = (TextView) findViewById(R.id.record);
        TextView strc = (TextView) findViewById(R.id.infoRC);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("trashmaster", 0);

        Map<String,?> allRec = pref.getAll();

        float count=0;
        String zapis="";
        for (String k : allRec.keySet()){
            float i = pref.getLong(k,0);
            count += i;
            zapis += k.toUpperCase() + " waste = " + (float) (i/1000.0) + " kg\n";
        }

        rc.setText(zapis);
        strc.setText("\nYou disposed " + (float) (count/1000.0) + " kg of waste!");
    }

}
