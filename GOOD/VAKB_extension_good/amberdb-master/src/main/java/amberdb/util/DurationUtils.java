package amberdb.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtils {
    
    private static final Logger log = LoggerFactory.getLogger(DurationUtils.class);
    
    /**
     * Converts the duration in the format of HH:MM:SS:ss to HH:MM:SS
     * @param periodHHMMSSmm
     * @return
     */
    public static String convertDuration(final String periodHHMMSSmm){
        String newDuration = periodHHMMSSmm;
        PeriodFormatter hoursMinutesSecondsMilli = new PeriodFormatterBuilder()
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .appendSeparator(":")
            .appendSeconds()
            .appendSeparator(":")
            .appendMillis()
            .toFormatter();
        try{
            if (StringUtils.isNotBlank(periodHHMMSSmm)){
                Period period = hoursMinutesSecondsMilli.parsePeriod(periodHHMMSSmm);
                newDuration = String.format("%02d:%02d:%02d", period.getHours(), period.getMinutes(), period.getSeconds());
            }
        }catch(IllegalArgumentException e){
            log.error("Invalid duration format: " + periodHHMMSSmm);
        }
        return newDuration;
    }

    public static Float convertDurationToSeconds(String duration) {
        duration = convertDuration(duration);
        Pattern durationPattern = Pattern.compile("(\\d\\d+):(\\d\\d):(\\d\\d)");
        if (duration != null && !duration.isEmpty()) {
            Matcher matcher = durationPattern.matcher(duration);
            int hour = 0, minute = 0, second = 0;
            if (matcher.matches()) {
                hour = Integer.parseInt(matcher.group(1), 10);
                minute = Integer.parseInt(matcher.group(2), 10);
                second = Integer.parseInt(matcher.group(3), 10);
                if (minute < 60 && second < 60) {
                    return (float)(hour * 3600 + minute * 60 + second);
                } else {
                    throw new RuntimeException("Invalid duration: " + duration);
                }
            } else {
                throw new RuntimeException("Invalid duration: " + duration);
            }
        }
        return 0f;
    }

    public static String convertDurationFromSeconds(Float durationAsSeconds) {
        if (durationAsSeconds != null && durationAsSeconds >= 0) {
            int secs = durationAsSeconds.intValue();
            int hour = secs / 3600;
            secs -= hour * 3600;
            int minute = secs / 60;
            int second = secs - minute * 60;
            return StringUtils.join(new String[] {StringUtils.leftPad("" + hour, 2, "0"),
                    StringUtils.leftPad("" + minute, 2, "0"),
                    StringUtils.leftPad("" + second, 2, "0")}, ":");
        } else if (durationAsSeconds != null && durationAsSeconds < 0) {
            throw new RuntimeException("Invalid duration: " + durationAsSeconds);
        }

        return null;
    }
}
