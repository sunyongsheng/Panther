package top.aengus.panther.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/1
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static final String FILE_SEPARATOR = "/";

    public static String ensurePrefix(String string) {
        if (string == null || string.isEmpty()) return FILE_SEPARATOR;
        return string.startsWith(FILE_SEPARATOR) ? string : FILE_SEPARATOR + string;
    }

    public static String ensureNoPrefix(String string) {
        if (string == null || string.isEmpty()) return "";
        return string.startsWith(FILE_SEPARATOR) ? string.substring(1) : string;
    }

    public static String ensureSuffix(String string) {
        if (string == null || string.isEmpty()) return FILE_SEPARATOR;
        return string.endsWith(FILE_SEPARATOR) ? string : string + FILE_SEPARATOR;
    }

    public static String ensureNoSuffix(String string) {
        if (string == null || string.isEmpty()) return "";
        return string.endsWith(FILE_SEPARATOR) ? string.substring(0, string.length() - 1) : string;
    }

    public static String getExtension(String filename) {
        if (filename == null) return null;
        int index = filename.lastIndexOf(".");
        if (index >= 0) {
            String suffixName = filename.substring(index);
            return suffixName.toLowerCase();
        }
        return null;
    }

    public static boolean isPic(String filename) {
        if (filename == null) return false;
        String ignoreCase = filename.toLowerCase();
        return (ignoreCase.endsWith(".jpeg")
                || ignoreCase.endsWith(".jpg")
                || ignoreCase.endsWith(".png")
                || ignoreCase.endsWith(".gif")
                || ignoreCase.endsWith(".svg")
                || ignoreCase.endsWith(".bmp")
                || ignoreCase.endsWith(".ico")
                || ignoreCase.endsWith(".tiff"));
    }

    public static boolean checkAndCreateDir(File dir) {
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                logger.error("创建目录失败: " + dir);
                return false;
            }
        }
        return true;
    }

}
