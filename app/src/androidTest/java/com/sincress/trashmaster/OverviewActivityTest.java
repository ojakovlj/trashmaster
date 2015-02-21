package com.sincress.trashmaster;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import static android.test.TouchUtils.clickView;
import static android.test.ViewAsserts.assertGroupContains;
import static org.assertj.android.api.Assertions.assertThat;

public class OverviewActivityTest extends ActivityInstrumentationTestCase2<OverviewActivity> {

    private OverviewActivity activity;
    private TabHost tabHolder;
    private TabWidget tabWidget;
    private FrameLayout frame;
    private RelativeLayout layoutRoot;
    private TextView recordInfo , tab1Data, tab2Data, tab3Data, randomTip, youDisposed;
    private ImageView instructions;

    public OverviewActivityTest() {
        super(OverviewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        activity = getActivity();
        layoutRoot = (RelativeLayout) activity.findViewById(R.id.root);
        tabHolder = (TabHost) activity.findViewById(R.id.tabHost);
        tabWidget = (TabWidget) activity.findViewById(android.R.id.tabs);
        frame = (FrameLayout) activity.findViewById(android.R.id.tabcontent);
        recordInfo = (TextView) activity.findViewById(R.id.record);
        instructions = (ImageView) activity.findViewById(R.id.instructionsImage);

        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_overview,null);
        tab1Data = (TextView) view.findViewById(R.id.tab1);
        tab2Data = (TextView) view.findViewById(R.id.tab2);
        tab3Data = (TextView) view.findViewById(R.id.tab3);
        randomTip = (TextView) activity.findViewById(R.id.infoRC);
        youDisposed = (TextView) activity.findViewById(R.id.infoOS);

        testTabs();
    }

    @MediumTest
    public void testLayout() {
        assertThat(layoutRoot)
                .hasChildCount(1)
                .isVisible();
        assertGroupContains(layoutRoot, tabHolder);
    }

    @MediumTest
    public void testTabsHeader() {
        assertThat(tabHolder)
                .hasChildCount(2)
                .isVisible();
        assertGroupContains(tabHolder, tabWidget);
        assertGroupContains(tabHolder, frame);
    }

    private void testTabs() {
        testTab1();
        testTab2();
        testTab3();
    }

    @MediumTest
    public void testTab1(){
        clickView(this, tab1Data);
        assertThat(instructions).isVisible();
    }
    @MediumTest
    public void testTab2(){
        clickView(this, tab2Data);
        assertThat(youDisposed).isVisible();
        assertThat(randomTip).isVisible();
    }
    @MediumTest
    public void testTab3(){
        clickView(this, tab3Data);
        assertThat(recordInfo).isVisible();
    }

}
