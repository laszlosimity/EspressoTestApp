package com.example.lsimity.mytestapp;

import android.os.SystemClock;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import java.util.Calendar;

import static java.lang.Thread.sleep;

/**
 * Use this helper anywhere there is a time picker to manage. This helper
 * will set time specified in a Calendar object.
 */
class TimePickerHelper {

    private UiDevice theDevice;

    public TimePickerHelper(UiDevice myDevice) {
        theDevice = myDevice;
    }

    public void setTime(Calendar cal) throws UiObjectNotFoundException {
        //First, we should ensure we are at the correct place
        SystemClock.setCurrentTimeMillis(1514188325);

        UiObject timeDialog = theDevice.findObject(new UiSelector().resourceId("android:id/hours"));
        if (timeDialog.getText().isEmpty()) {
            //we aren't in the right place, we should gracefully exit
            throw new UiObjectNotFoundException("We are not at the time box dialog - it was " + timeDialog.getText());
        }

        int calHour =  cal.get(Calendar.HOUR_OF_DAY);
        int calMinute = cal.get(Calendar.MINUTE);

        //now we need to click on the mode button to open the slider
        theDevice.findObject(new UiSelector().resourceId("android:id/toggle_mode")).click();

        //click on hour
        UiObject hoursSlider = theDevice.findObject(new UiSelector().resourceId("android:id/input_hour"));
        hoursSlider.clearTextField();
        hoursSlider.setText(Integer.toString(calHour));

        //click on minutes
        UiObject minutesSlider = theDevice.findObject(new UiSelector().resourceId("android:id/input_minute"));
        minutesSlider.clearTextField();
        minutesSlider.setText(Integer.toString(calMinute));

        //click OK
        theDevice.findObject(new UiSelector().resourceId("android:id/button1")).click();

        //ToDo - check that time is set correctly.

    }

    public void setTimeSamsung(Calendar cal) throws UiObjectNotFoundException {
        //First, we should ensure we are at the correct place
        UiObject timeDialog = theDevice.findObject(new UiSelector().resourceId("android:id/numberpicker_input").instance(0));

        int calHour =  cal.get(Calendar.HOUR_OF_DAY);
        int calMinute = cal.get(Calendar.MINUTE);

        //we can just send the keys directly to numberpicker_input to input hours & minutes
        //we might have to sleep for a bit, as there's some animations, I know I know....
        try {
            sleep(2000);
        }
        catch (InterruptedException i){
            //whatevs
        }
        timeDialog.click();

        String theHour = Integer.toString(calHour);
        if (theHour.length() < 2) {
            theHour = "0" + theHour;
        }

        timeDialog.setText(theHour);

        String theMinutes = Integer.toString(calMinute);
        if (theMinutes.length() < 2) {
            theMinutes = "0" + theMinutes;
        }

        theDevice.findObject(new UiSelector().className("android.widget.EditText").instance(1).index(0)).setText(theMinutes);

        //click OK
        theDevice.findObject(new UiSelector().resourceId("android:id/button1").text("DONE")).click();

        //ToDo - check that time is set correctly.

    }
}