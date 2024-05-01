package com.example.myapplication;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CDAUtils {
    public static ArrayList<Timestamp> createDate(long duration, String periodType, String toValue){
        Date currentDate = new Date();
        if(toValue!=null){
            currentDate = convertDate(toValue);
        }
        long daysInMillis = TimeUnit.DAYS.toMillis(duration);
        long oneDayMillis = TimeUnit.DAYS.toMillis(1);

        Date startDate = new Date(currentDate.getTime()-daysInMillis);
        startDate = specificTime(0,0,0,startDate);
        Date elevenFiftyNine = specificTime(23, 59, 59, startDate);

        Date previousDate = new Date(elevenFiftyNine.getTime()-oneDayMillis);
        Date previousStartDate = new Date(previousDate.getTime()-daysInMillis);


        // Convert dates to Firestore timestamps
        Timestamp startTimestamp = new Timestamp(startDate);
        Timestamp currentTimestamp = new Timestamp(currentDate);


        Timestamp previousTimestamp = new Timestamp(previousDate);
        Timestamp previousStartTimestamp = new Timestamp(previousStartDate);

        ArrayList<Timestamp> timestamps = new ArrayList<>();

        timestamps.add(startTimestamp);
        timestamps.add(currentTimestamp);
        timestamps.add(previousStartTimestamp);
        timestamps.add(previousTimestamp);

        return  timestamps;
    }

    public static ArrayList<Date> createCurrentStartDate( long duration, String periodType, String toValue) {
        Date currentDate = new Date();
        if(toValue!=null){
            currentDate = convertDate(toValue);
        }
        long daysInMillis = TimeUnit.DAYS.toMillis(duration);
        long oneDayMillis = TimeUnit.DAYS.toMillis(1);

        Date startDate = new Date(currentDate.getTime()-daysInMillis);
        startDate = specificTime(0,0,0,startDate);
        Date elevenFiftyNine = specificTime(23, 59, 59, startDate);

        Date previousDate = new Date(elevenFiftyNine.getTime()-oneDayMillis);
        Date previousStartDate = new Date(previousDate.getTime()-daysInMillis);

        ArrayList<Date> startCurrentDate = new ArrayList<>();
        startCurrentDate.add(currentDate);
        startCurrentDate.add(startDate);
        startCurrentDate.add(previousDate);
        startCurrentDate.add(previousStartDate);

        return startCurrentDate;

    }
    public static Date specificTime(int hours, int minutes, int seconds, Date date){
        LocalDate localStartDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime specifics = LocalTime.of(hours, minutes, seconds);
        LocalDateTime dateTime = LocalDateTime.of(localStartDate, specifics);
        date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    public static Date convertDate(String date){
        Date endDate = new Date();
        try {
            endDate = new SimpleDateFormat("d/M/yyyy").parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return endDate;
    }
}
