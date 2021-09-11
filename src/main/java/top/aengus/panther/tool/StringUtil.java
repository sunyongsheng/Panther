package top.aengus.panther.tool;

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
}
