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

    public static String separatorFormat(Date date) {
        return SEPARATOR_FORMATTER.format(date);
    }

    public static String hourColonMinFormat(Date date) {
        return HOUR_COLON_MIN_FORMATTER.format(date);
    }
    
    public static String dateUnderlineFormat(Date date) {
        return DATE_UNDERLINE_FORMATTER.format(date);
    }
}
