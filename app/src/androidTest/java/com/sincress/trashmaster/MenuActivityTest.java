package com.sincress.trashmaster;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import static android.test.TouchUtils.clickView;
import static android.test.ViewAsserts.assertGroupContains;
import static org.assertj.android.api.Assertions.assertThat;

public class MenuActivityTest extends ActivityInstrumentationTestCase2<MenuActivity> {

    public static final int ACTIVITY_TIMEOUT = 1000;

    private MenuActivity activity;

    private LinearLayout layoutRoot, sublay1, sublay2;
    private TextView instruction;
    private Spinner operSelect;
    private EditText field1, field2, field3, field4;
    private Button calculate;

    public MenuActivityTest() {
        super(MenuActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        activity = getActivity();
        instruction = (TextView) activity.findViewById(R.id.textView);
        field1 = (EditText) activity.findViewById(R.id.real1);
        field2 = (EditText) activity.findViewById(R.id.real2);
        field3 = (EditText) activity.findViewById(R.id.imag1);
        field4 = (EditText) activity.findViewById(R.id.imag2);
        operSelect = (Spinner) activity.findViewById(R.id.spinner);
        layoutRoot = (LinearLayout) activity.findViewById(R.id.linlay);
        sublay1 = (LinearLayout) activity.findViewById(R.id.ll1);
        sublay2 = (LinearLayout) activity.findViewById(R.id.ll2);
        calculate = (Button) activity.findViewById(R.id.btn1);
    }

    @MediumTest
    public void testTitle() {
        assertThat(instruction).isVisible().hasText(R.string.mode_select);
    }

    @MediumTest
    public void testLayout() {
        assertThat(layoutRoot)
                .hasChildCount(6)
                .isVertical()
                .isVisible();
        assertGroupContains(layoutRoot, operSelect);
        assertGroupContains(layoutRoot, instruction);
        assertGroupContains(layoutRoot, sublay1);
        assertGroupContains(layoutRoot, sublay2);
    }

    @MediumTest
    public void testSelectionSpinnerIsShown() {
        assertThat(operSelect).isShown();
    }

    @MediumTest
    public void testInstructionIsShown() {
        assertThat(instruction).isShown();
    }

    @MediumTest
    public void testDoesTakeIntegers() { testInputIsOk(field1, "3"); }

    @MediumTest
    public void testDoesTakeNegativeIntegers() { testInputIsOk(field2, "7"); }

    @MediumTest
    public void testDoesTakeFloats() { testInputIsOk(field3, "8.5"); }

    @MediumTest
    public void testDoesTakeNegativeFloats() { testInputIsOk(field4, "2.3"); }

    @MediumTest
    public void testSpinnerWorks() { testSpinner(operSelect, 1); }


    @MediumTest
    public void testLaunchResultActivity() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                field1.setText("2");
                field2.setText("2");
                field3.setText("2");
                field4.setText("2");
            }
        });
        testLaunchActivity(calculate, ResultActivity.class);
    }

    private void testSpinner(final Spinner spinner1, final int i) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner1.requestFocus();
                spinner1.setSelection(i);
            }
        });
    }

    private void input(final View view, final String text) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(text);
        getInstrumentation().waitForIdleSync();
    }

    private void testInputIsOk(EditText view, String input) {
        input(view, input);
        assertThat(view).hasError(null);
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
