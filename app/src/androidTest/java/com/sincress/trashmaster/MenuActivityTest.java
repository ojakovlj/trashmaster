package com.sincress.trashmaster;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuWidget;

import static android.test.TouchUtils.clickView;
import static android.test.ViewAsserts.assertGroupContains;
import static org.assertj.android.api.Assertions.assertThat;

public class MenuActivityTest extends ActivityInstrumentationTestCase2<MenuActivity> {

    public static final int ACTIVITY_TIMEOUT = 4000;

    private MenuActivity activity;
    private LinearLayout layoutRoot;
    private RelativeLayout rellay;
    private TextView informations;
    private Button launchMapBtn, launchOVBtn, launchLogBtn;

    public MenuActivityTest() {
        super(MenuActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        activity = getActivity();
        layoutRoot = (LinearLayout) activity.findViewById(R.id.linlay);
        launchMapBtn = (Button) activity.findViewById(R.id.launch_map);
        launchOVBtn = (Button) activity.findViewById(R.id.launch_overview);
        launchLogBtn = (Button) activity.findViewById(R.id.launch_record);
        rellay = (RelativeLayout) activity.findViewById(R.id.rellay);
        informations = (TextView) activity.findViewById(R.id.zanimljivosti);
    }

    @MediumTest
    public void testInfoIsShown() {
        assertThat(informations).isVisible().isNotNull();
    }

    @MediumTest
    public void testLayout() {
        assertThat(layoutRoot)
                .hasChildCount(3)
                .isVertical()
                .isVisible();
        assertGroupContains(layoutRoot, rellay);
        assertGroupContains(layoutRoot, informations);
        assertGroupContains(layoutRoot, launchLogBtn);
    }

    @MediumTest
    public void testRadialMenuWorks() { testRadialMenu(); }

    @MediumTest
    public void testLaunchOverviewActivity() {
        testLaunchActivity(launchOVBtn, OverviewActivity.class);
    }

    @MediumTest
    public void testLaunchMapActivity() {
        testLaunchActivity(launchMapBtn, MapActivity.class);
    }

    private void testRadialMenu() {
        clickView(this, launchLogBtn);
        try {
            Thread.sleep(2000); //wait for the radial menu to appear
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //for some reason this just doesn't work :/
        RadialMenuWidget menu = (RadialMenuWidget) activity.findViewById(R.id.radialLogMenu);
        assertThat(menu).isVisible();
    }

    private void testLaunchActivity(final View button, Class<? extends Activity> activityClass) {
        final Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(activityClass.getName(), null, false);
        clickView(this, button);

        Activity activity = monitor.waitForActivityWithTimeout(ACTIVITY_TIMEOUT);
        assertThat(activity).isNotNull();
        assertEquals(activityClass, activity.getClass());

        activity.finish();
    }

}
