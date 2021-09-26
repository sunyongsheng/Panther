package top.aengus.panther.tool;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/6/8
 */
public class StringUtil {

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    public static String joinToString(String[] strings) {
        return joinToString(strings, ",");
    }

    public static String joinToString(String[] strings, String separator) {
        if (strings == null || strings.length == 0) {
            return "";
        }
        return StringUtils.join(strings, separator);
    }

}
