package com.monitoring.Springbackend.utils;

import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utilities
{
    public static String formatURL(String inputUrl){
        return inputUrl.replace("^(http[s]?:)","");
    }

    public static Timestamp formatDate(long time){
        String systemTime = time+"";
        if (StringUtils.hasLength(systemTime) && systemTime != null) {
            Date date = new Date(Long.parseLong(systemTime));
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            String formatted = format.format(date);
            Timestamp timeStamp = Timestamp.valueOf(formatted);
            return timeStamp;
        } else {
            return null;
        }
    }
}
