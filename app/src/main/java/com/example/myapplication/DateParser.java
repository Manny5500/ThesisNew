package com.example.myapplication;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DateParser {

    public Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date parsedDate = null;

        try {
            parsedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parsedDate;
    }

    public int calculateMonthsDifference(Date givenDate) {
        if (givenDate != null) {
            Calendar currentCalendar = Calendar.getInstance();
            Calendar givenCalendar = Calendar.getInstance();
            givenCalendar.setTime(givenDate);

            int years = currentCalendar.get(Calendar.YEAR) - givenCalendar.get(Calendar.YEAR);
            int months = currentCalendar.get(Calendar.MONTH) - givenCalendar.get(Calendar.MONTH);

            return years * 12 + months;
        }
        return 0;
    }

    public static int calculateDaysDifference(Date givenDate) {
        if (givenDate != null) {
            Calendar currentCalendar = Calendar.getInstance();
            Calendar givenCalendar = Calendar.getInstance();
            givenCalendar.setTime(givenDate);

            long currentTimeInMillis = currentCalendar.getTimeInMillis();
            long givenTimeInMillis = givenCalendar.getTimeInMillis();

            long differenceInMillis = currentTimeInMillis - givenTimeInMillis;
            int daysDifference = (int) (differenceInMillis / (24 * 60 * 60 * 1000));

            return daysDifference;
        }
        return 0;
    }

    public static String getMonthYear(){
        LocalDate currentDates = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        String formattedDate = currentDates.format(formatter);

        return formattedDate;
    }

    public static Timestamp getCurrentTimeStamp(){
        Date currentDate = new Date();
        Timestamp currentTimestamp = new Timestamp(currentDate);
        return currentTimestamp;
    }

    public static ArrayList<Timestamp> createDate(long duration, String periodType){
        Date currentDate = new Date();
        long daysInMillis = TimeUnit.DAYS.toMillis(duration);
        long oneDayMillis = TimeUnit.DAYS.toMillis(1);

        Date startDate = new Date(currentDate.getTime()-daysInMillis);
        startDate = specificTime(0,0,0,startDate);
        Date elevenFiftyNine = specificTime(23, 59, 59, startDate);

        Date previousDate = new Date(elevenFiftyNine.getTime()-oneDayMillis);
        Date previousStartDate = new Date(previousDate.getTime()-daysInMillis);

        if(periodType.equals("month")){
            ArrayList<Date> monthlyDates = monthlyPeriod(currentDate, startDate,
                    previousStartDate, elevenFiftyNine, previousDate, oneDayMillis);
            startDate = monthlyDates.get(0);
            previousStartDate = monthlyDates.get(1);
            previousDate = monthlyDates.get(2);
        }

        if(periodType.equals("year")){
            ArrayList<Date> yearlyDates = yearlyPeriod(currentDate, startDate,
                    previousStartDate, elevenFiftyNine, previousDate, oneDayMillis);
            startDate = yearlyDates.get(0);
            previousStartDate = yearlyDates.get(1);
            previousDate = yearlyDates.get(2);
        }

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

    public static ArrayList<Date> yearlyPeriod(Date currentDate, Date startDate, Date previousStartDate,
                                                Date elevenFiftyNine, Date previousDate, long oneDayMillis){
        ArrayList<Date> modifyDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int years = calendar.get(Calendar.YEAR);
        startDate = specificDate(years, 1, 1, startDate);
        calendar.setTime(startDate);
        years = calendar.get(Calendar.YEAR);
        previousStartDate = specificDate(years-1, 1,1 , previousStartDate);
        elevenFiftyNine = specificTime(23, 59, 59, startDate);
        previousDate = new Date(elevenFiftyNine.getTime()-oneDayMillis);

        modifyDates.add(startDate);
        modifyDates.add(previousStartDate);
        modifyDates.add(previousDate);

        return modifyDates;
    }

    public static ArrayList<Date> monthlyPeriod(Date currentDate, Date startDate, Date previousStartDate,
                                                Date elevenFiftyNine, Date previousDate, long oneDayMillis){
        ArrayList<Date> modifyDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int month = calendar.get(Calendar.MONTH);
        int years = calendar.get(Calendar.YEAR);
        startDate = specificDate(years, month+1, 1, startDate);
        calendar.setTime(startDate);
        month = calendar.get(Calendar.MONTH);
        years = calendar.get(Calendar.YEAR);
        if(month==0){
            month = 11;
            years = years -1;
        }
        previousStartDate = specificDate(years, month, 1, previousStartDate);
        elevenFiftyNine = specificTime(23, 59, 59, startDate);
        previousDate = new Date(elevenFiftyNine.getTime()-oneDayMillis);

        modifyDates.add(startDate);
        modifyDates.add(previousStartDate);
        modifyDates.add(previousDate);

        return   modifyDates;

    }

    public static Date specificTime(int hours, int minutes, int seconds, Date date){
        LocalDate localStartDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime specifics = LocalTime.of(hours, minutes, seconds);
        LocalDateTime dateTime = LocalDateTime.of(localStartDate, specifics);
        date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    public static Date specificDate(int years, int months, int days, Date date){
        LocalDate localStartDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate specificDate = LocalDate.of(years, months, days);

        // Convert to LocalDateTime
        LocalDateTime dateTime = specificDate.atStartOfDay();

        // Convert to Date
        date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    public static String[] createDateArray(Date startDate, Date endDate) {

        List<String> dateStringList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        while (!startDate.after(endDate)) {
            dateStringList.add(sdf.format(startDate));
            startDate.setTime(startDate.getTime() + 24 * 60 * 60 * 1000); // Add one day
        }

        return dateStringList.toArray(new String[0]);

    }

    public static String[] createDateArrayForMonth(Date startDate, Date endDate) {

        List<String> dateStringList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            dateStringList.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.MONTH, 1);
        }

        return dateStringList.toArray(new String[0]);

    }

    public static ArrayList<Date> createCurrentStartDate( long duration, String periodType) {
        Date currentDate = new Date();
        long daysInMillis = TimeUnit.DAYS.toMillis(duration);
        long oneDayMillis = TimeUnit.DAYS.toMillis(1);

        Date startDate = new Date(currentDate.getTime()-daysInMillis);
        startDate = specificTime(0,0,0,startDate);
        Date elevenFiftyNine = specificTime(23, 59, 59, startDate);

        Date previousDate = new Date(elevenFiftyNine.getTime()-oneDayMillis);
        Date previousStartDate = new Date(previousDate.getTime()-daysInMillis);

        if(periodType.equals("month")){
            ArrayList<Date> monthlyDates = monthlyPeriod(currentDate, startDate,
                    previousStartDate, elevenFiftyNine, previousDate, oneDayMillis);
            startDate = monthlyDates.get(0);
            previousStartDate = monthlyDates.get(1);
            previousDate = monthlyDates.get(2);
        }


        if(periodType.equals("year")){

            //this 5-line code is to set the date to December 31, currentyear
            //purpose is to show the line chart the whole year instead of the current month
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            int years = calendar.get(Calendar.YEAR);
            currentDate  = specificDate(years, 12, 31, startDate);


            ArrayList<Date> yearlyDates = yearlyPeriod(currentDate, startDate,
                    previousStartDate, elevenFiftyNine, previousDate, oneDayMillis);
            startDate = yearlyDates.get(0);
            previousStartDate = yearlyDates.get(1);
            previousDate = yearlyDates.get(2);
        }

        ArrayList<Date> startCurrentDate = new ArrayList<>();
        startCurrentDate.add(currentDate);
        startCurrentDate.add(startDate);
        startCurrentDate.add(previousDate);
        startCurrentDate.add(previousStartDate);

        return startCurrentDate;

    }

}
