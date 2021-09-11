package top.aengus.panther.tool;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/29
 */
public class UrlUtil {

    private static final String SEPARATOR = "/";

    public static String ensureSuffix(String url) {
        if (url == null || url.isEmpty()) return SEPARATOR;
        return url.endsWith(SEPARATOR) ? url : url + SEPARATOR;
    }

    public static String ensureNoSuffix(String url) {
        if (url == null || url.isEmpty()) return "";
        return url.endsWith(SEPARATOR) ? url.substring(0, url.length() - 1) : url;
    }
}
