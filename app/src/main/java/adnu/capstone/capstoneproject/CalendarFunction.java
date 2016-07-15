package adnu.capstone.capstoneproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import adnu.capstone.capstoneproject.Models.Schedule;

/**
 * Created by Martinez Edwin on 7/9/2016.
 */
public class CalendarFunction extends AppCompatActivity {

    String date, time;
    int year_x, month_x, day_x;
    int hour_x, min_x;

    public CalendarFunction() {

    }

    public void setYear_x(int year_x) {
        this.year_x = year_x;
    }

    public void setMonth_x(int month_x) {
        this.month_x = month_x;
    }

    public void setDay_x(int day_x) {
        this.day_x = day_x;
    }

    public int getYear_x() {
        return year_x;
    }

    public int getMonth_x() {
        return month_x;
    }

    public int getDay_x() {
        return day_x;
    }

    public int getHour_x() {
        return hour_x;
    }

    public int getMin_x() {
        return min_x;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    hour_x = hourOfDay;
                    min_x = minute;
                }
            };
    public  DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
        }
    };
    public void EditDateAndTime(int sDay, int sMonth, int sYear, int photoID, int sHour, int sMinute){
        String d = String.valueOf(sDay);
        String m = String.valueOf(sMonth);
        String y = String.valueOf(sYear);
        String photoId_x = String.valueOf(photoID);
        String h = String.valueOf(sHour);
        String min = String.valueOf(sMinute);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String dateInString = d + "-" + m + "-" + y + " " + h + ":" + min + " AM";
        Date date = null;
        String processDate = "", saveTime = "", saveDate ="";
        try {
            date = formatter.parse(dateInString);
            processDate = formatter.format(date);
            Log.d("ViewPet", "Date " + saveDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.date = processDate.substring(0,10);
        this.time = processDate.substring(11, processDate.length());
    }

}
