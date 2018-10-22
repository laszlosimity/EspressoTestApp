
package com.example.lsimity.mytestapp;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import java.util.Calendar;
/**
 * Use this helper anywhere there is a date picker to manage. This helper
 * will set date specified in a Calendar object.
 */
class DatePickerHelper {
    private UiDevice theDevice;

    private static final int MONTHS_PER_YEAR = 12;


    public DatePickerHelper(UiDevice myDevice) {
        this.theDevice = myDevice;
    }


   public void incrementMonth(int count) throws UiObjectNotFoundException {
       UiObject theObject = theDevice.findObject(new UiSelector().resourceId("android:id/next"));
       for (int i=0; i < count; i++) {
           theObject.click();
       }
   }

    public void decrementMonth(int count) throws UiObjectNotFoundException {
        if (theDevice.findObject(new UiSelector().resourceId("android:id/prev")).exists()) {
            for (int i=0; i < count; i++) {
                theDevice.findObject(new UiSelector().resourceId("android:id/prev")).click();
            }
        }
        else if (theDevice.findObject(new UiSelector().resourceId("android:id/sem_datepicker_calendar_header_prev_button")).exists()) {
            for (int j=0; j < count; j++) {
                theDevice.findObject(new UiSelector().resourceId("android:id/sem_datepicker_calendar_header_prev_button")).click();
            }
        }

    }
    public String getYear()  throws UiObjectNotFoundException
    {

        //likely to be an emulator or a very vanilla Android OS
        if (theDevice.findObject(new UiSelector().resourceId("android:id/date_picker_header_year")).exists()) {
            return theDevice.findObject(new UiSelector().resourceId("android:id/date_picker_header_year")).getText();
        }
        //This is probably Samsung
        else if (theDevice.findObject(new UiSelector().resourceId("android:id/sem_datepicker_calendar_header_text")).exists()) {
            //we now need to do some work here, as the selector appears with the year
            String monthYearString = theDevice.findObject(new UiSelector().resourceId("android:id/sem_datepicker_calendar_header_text")).getText();
            if (!monthYearString.isEmpty()) {
                String[] theStrings = monthYearString.split(" ");
                // new UiObjectNotFoundException("Year was: §" + theStrings[1] + "§ and month §" + theStrings[0]);
                return theStrings[1]; //this should be the year
            }
        }

        return "";
    }

    public String getMonth()  throws Exception
    {
        if (theDevice.findObject(new UiSelector().resourceId("android:id/date_picker_header_date")).exists()) {

            //we are probably an emulator or a pretty vanilla build
            UiObject theObject = theDevice.findObject(new UiSelector().resourceId("android:id/date_picker_header_date"));
            String theWholeDateString = theObject.getText();
            //Do some fancy footwork to find out if we can decipher a month here - lots of Androids like to mess around here
            String[] theSubs = theWholeDateString.split(" ");
            String theMonthString = ""; // our return string
            for (int i=0; i<theSubs.length; i++) {
                if (toMonthNumber(theSubs[i]) != 0) {
                    //we have found a match
                    theMonthString = theSubs[i];
                }
            }

            if (theMonthString.equalsIgnoreCase("")) {
                //couldn't find a month, very problematic, best to fail
                throw new Exception("Could not decipher a month in the picker - likely this is some Android customisation I can't cope with ");
            }
            return theMonthString;
        }
        else if (theDevice.findObject(new UiSelector().resourceId("android:id/sem_datepicker_calendar_header_text")).exists())
        {
            //probably Samsung
            //we now need to do some work here, as the selector appears with the year
            String monthYearString = theDevice.findObject(new UiSelector().resourceId("android:id/sem_datepicker_calendar_header_text")).getText();
            if (!monthYearString.isEmpty()) {
                String[] theStrings = monthYearString.split(" ");
                return theStrings[0]; //this should be the month
            }

        }

        return "";
    }

   /* public String getDay() throws UiObjectNotFoundException
    {
        UiObject theObject = theDevice.findObject(new UiSelector().resourceId("android:id/date_picker_header_date"));
        String theWholeDateString = theObject.getText();
        String theMonthString = theWholeDateString.substring(theWholeDateString.length()-2, theWholeDateString.length());
        theMonthString = theMonthString.replaceAll("\\s","");
        return theMonthString;
    }*/

    public void selectDay(int day) throws UiObjectNotFoundException {
       if (theDevice.findObject(new UiSelector().text(Integer.toString(day))).exists()) {
           theDevice.findObject(new UiSelector().text(Integer.toString(day))).click();
       }
       //ToDo arbitary way to check if it's Samsung - need a much better way to do this - perhaps preserve state?
       else if (theDevice.findObject(new UiSelector().className("android.view.View").index(10)).exists())
        {
            //I need the actual day description - or do I?
            theDevice.findObject(new UiSelector().className("android.view.View").descriptionContains(day + "")).click();
        }

    }

    public void clickOK() throws UiObjectNotFoundException {
        UiObject theObject = theDevice.findObject(new UiSelector().resourceId("android:id/button1"));
        theObject.click();
    }

    public void setDate(Calendar cal) throws Exception {
        int calYear = cal.get(Calendar.YEAR);
        int calMonth = cal.get(Calendar.MONTH)-1;
        int calDay = cal.get(Calendar.DAY_OF_MONTH);

        int dpYear = Integer.parseInt(getYear());

        String monthShort = getMonth();
        int dpMonth;
        if (monthShort.length() < 4) {
            dpMonth = toMonthNumber(monthShort);
        }
        else {
            dpMonth = toMonthNumberLong(monthShort);
        }

        if (calYear > dpYear) {
            //year we want to set is in the future
            //We need to calculate.
            //First, calculate the number of clicks required to increment to December of this year
            int monthsToIncrement = MONTHS_PER_YEAR-dpMonth+1;
            //Then we add on the number of months at te destination year
            monthsToIncrement = monthsToIncrement+calMonth;
            //we need to also increment by 12 full months for any other years
            monthsToIncrement = monthsToIncrement+(MONTHS_PER_YEAR*((calYear-dpYear)-1)); //do

            incrementMonth(monthsToIncrement);

        } else if (calYear < dpYear) {
            //year we want to set is in the past

            //Just add firstly the number of months in current year
            int monthsToDecrement = MONTHS_PER_YEAR-calMonth;
            monthsToDecrement = monthsToDecrement+dpMonth-1;
            //we need to also increment by 12 full months for any other years
            monthsToDecrement = monthsToDecrement+(MONTHS_PER_YEAR*((dpYear-calYear)-1)); //do

            decrementMonth(monthsToDecrement);
        }
        else {
            //it's the correct year, just change month
            if (dpMonth > calMonth) {
                //we want to go back

                decrementMonth(dpMonth-calMonth-1);
            }
            else {
                //we want to go forward
                incrementMonth(calMonth-dpMonth+1);
            }
        }

        selectDay(calDay);

        /*int newYear = Integer.parseInt(getYear());
        int newMonth = toMonthNumber(getMonth());
        int newDay = Integer.parseInt(getDay());

        if ((newYear == calYear) && (newMonth == calMonth+1) && (newDay == calDay)) {

            //success
            clickOK();
        }
        else {
            throw new UiObjectNotFoundException("Could not change date - details " + cal.toString() + " and " + newYear + newMonth + newDay);
        }*/

        clickOK();

    }



    private int toMonthNumber(String monthName) {
        String months[] = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (int x = 0; x < months.length; x++) {
            if (months[x].contains(monthName))
                return x;
        }
        return 0;
    }


    private int toMonthNumberLong(String monthName) {
        String months[] = new String[] {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        for (int x = 0; x < months.length; x++) {
            if (months[x].contains(monthName))
                return x;
        }
        return 0;
    }
    /**
     * Get the number of days in the month
     * @param year
     * @param month
     * @return
     */
    private int getDaysInMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}