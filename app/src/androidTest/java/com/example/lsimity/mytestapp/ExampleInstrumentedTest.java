package com.example.lsimity.mytestapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.widget.DatePicker;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static android.provider.Settings.ACTION_DATE_SETTINGS;
import static android.support.v4.content.ContextCompat.startActivities;
import static android.support.v4.content.ContextCompat.startActivity;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    UiDevice mDevice;

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void init() throws Exception {
        activityActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction();

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        startActivity(appContext, new Intent(ACTION_DATE_SETTINGS), null);

        UiSelector dateSelector = new UiSelector().text("Set date");
        UiObject dateSelectorElement = mDevice.findObject(dateSelector);

        UiSelector timeSelector = new UiSelector().text("Set time");
        UiObject timeSelectorElement = mDevice.findObject(timeSelector);

        //test whether or not the automatic date and time switch is active
        if (mDevice.findObject(new UiSelector().resourceId("android:id/switch_widget").instance(0)).exists()) {
            UiSelector autoDateTimeSelector = new UiSelector().resourceId("android:id/switch_widget").instance(0);
            UiObject autoDateTimeSwitch = mDevice.findObject(autoDateTimeSelector);
            if (autoDateTimeSwitch.getText().compareToIgnoreCase("ON") == 0) {
                //turn off
                autoDateTimeSwitch.click();
            }
        }

        //may not exist if it's Samsung and we're already at the date picker, so no need to click anyway
        if (dateSelectorElement.exists()) {
            dateSelectorElement.click();
        }

        Calendar setDate = Calendar.getInstance();
        setDate.clear();
        setDate.set(Calendar.MONTH, Calendar.DECEMBER);
        setDate.set(Calendar.DAY_OF_MONTH, 25);
        setDate.set(Calendar.YEAR, 2017);
        setDate.set(Calendar.HOUR_OF_DAY, 11);
        setDate.set(Calendar.MINUTE, 05);

        DatePickerHelper myDatePickerHelper = new DatePickerHelper(mDevice);
        TimePickerHelper myTimePickerHelper = new TimePickerHelper(mDevice);

        myDatePickerHelper.setDate(setDate);

        //Todo need to assert here that we are at the correct place before attempting time

        //Here, we are almost certainly vanilla Androids - instance(2) indicates how many switches on screen.
        if (mDevice.findObject(new UiSelector().resourceId("android:id/switch_widget").instance(2)).exists()) {
            UiObject twentyFourHourTimeObject = mDevice.findObject(new UiSelector().resourceId("android:id/switch_widget").instance(2));
            if (twentyFourHourTimeObject.getText().equalsIgnoreCase("OFF"))
            {
                twentyFourHourTimeObject.click();
            }
            //24 hour time is now on to simplify things

            timeSelectorElement.click(); //click on time selector

            myTimePickerHelper.setTime(setDate);
        }
        //Massive assumption, but let's say only 2 switches so probably Samsung
        else {
           UiObject twentyFourHourTimeObject = mDevice.findObject(new UiSelector().resourceId("android:id/switch_widget").instance(1));
            if (twentyFourHourTimeObject.getText().equalsIgnoreCase("OFF"))
            {
                twentyFourHourTimeObject.click();
            }
            //forking a little here for Samsung
            timeSelectorElement.click(); //click on time selector

            myTimePickerHelper.setTimeSamsung(setDate);

        }


        //now you should resume your prior activity
        //startActivity(appContext, new Intent("android.intent.action.MAIN"), null);

        assertEquals("com.example.lsimity.mytestapp", appContext.getPackageName());


    }
}
