package top.aengus.panther.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/1
 */
public class DateFormatter {

    private static final SimpleDateFormat SEPARATOR_FORMATTER = new SimpleDateFormat("yyyy/MM/dd/HH/mm/");
    private static final SimpleDateFormat HOUR_COLON_MIN_FORMATTER = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATE_UNDERLINE_FORMATTER = new SimpleDateFormat("yyyy_MM_dd");
    private static final SimpleDateFormat MONTH_DAY_FORMATTER = new SimpleDateFormat("MM/dd");
    private static final SimpleDateFormat DETAIL_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String separatorFormat(Date date) {
        return SEPARATOR_FORMATTER.format(date);
    }

    public static String hourColonMinFormat(Date date) {
        return HOUR_COLON_MIN_FORMATTER.format(date);
    }
    
    public static String dateUnderlineFormat(Date date) {
        return DATE_UNDERLINE_FORMATTER.format(date);
    }

    public static String monthDayFormat(long time) {
        return MONTH_DAY_FORMATTER.format(time);
    }

    public static String detailFormat(long time) {
        return DETAIL_FORMATTER.format(time);
    }

    public static String formatTimeDesc(long millis) {
        long seconds = millis / 1000;
        if (seconds < 60) {
            return seconds + "秒";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + "分" + (seconds - minutes * 60) + "秒";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            long minutes = (seconds - hours * 3600) / 60;
            return hours + "时" + minutes + "分";
        } else {
            long days = seconds / 86400;
            long hours = (seconds - days * 86400) / 3600;
            return days + "天" + hours + "时";
        }
    }
}
