package com.ta.elivgo.globalfunction;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

public class EpochConverter {

    public String fromEpoch(long epoch, String format) {
//        long epochTimeMillis = Long.parseLong(listReservation.getDatestart()); // Example epoch time in milliseconds
        Instant instant = Instant.ofEpochSecond(epoch);

        ZoneId zoneId = ZoneId.systemDefault(); // Use the system default time zone
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = localDateTime.format(formatter);

        return formattedDateTime;
    }

    public String toEpoch(String dt) {
        String epoch;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        try {
            Date date = df.parse(dt);
            epoch = String.valueOf(date.getTime()/1000);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return epoch;
    }

}
