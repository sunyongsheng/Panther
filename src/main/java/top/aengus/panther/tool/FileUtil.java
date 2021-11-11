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
    public static final char FILE_SEPARATOR_CHAR = '/';

    /**
     * 确保开头只有一个 /
     */
    public static String ensurePrefix(String string) {
        if (string == null || string.isEmpty()) return FILE_SEPARATOR;
        int length = string.length();
        if (length == 1) {
            if (string.charAt(0) == FILE_SEPARATOR_CHAR) return string;
            else return FILE_SEPARATOR + string;
        } else {
            if (string.charAt(0) == FILE_SEPARATOR_CHAR) {
                int beginIndex = 0;
                for (int i = 1; i < length; i++) {
                    if (string.charAt(i) == FILE_SEPARATOR_CHAR) {
                        beginIndex++;
                    } else break;
                }
                return string.substring(beginIndex);
            } else {
                return FILE_SEPARATOR + string;
            }
        }
    }

    /**
     * 确保开头没有 /
     */
    public static String ensureNoPrefix(String string) {
        if (string == null || string.isEmpty()) return "";
        int length = string.length();
        if (length == 1) {
            if (string.charAt(0) == FILE_SEPARATOR_CHAR) return "";
            else return string;
        } else {
            if (string.charAt(0) == FILE_SEPARATOR_CHAR) {
                int beginIndex = 1;
                for (int i = 1; i < length; i++) {
                    if (string.charAt(i) == FILE_SEPARATOR_CHAR) {
                        beginIndex++;
                    } else break;
                }
                return string.substring(beginIndex);
            } else {
                return string;
            }
        }
    }

    /**
     * 确保结尾只有一个 /
     */
    public static String ensureSuffix(String string) {
        if (string == null || string.isEmpty()) return FILE_SEPARATOR;
        int length = string.length();
        if (length == 1) {
            if (string.charAt(0) == FILE_SEPARATOR_CHAR) return string;
            else return string + FILE_SEPARATOR;
        } else {
            if (string.charAt(length - 1) == FILE_SEPARATOR_CHAR) {
                int endIndex = length;
                for (int i = length - 2; i >= 0; i--) {
                    if (string.charAt(i) == FILE_SEPARATOR_CHAR) {
                        endIndex--;
                    } else break;
                }
                return string.substring(0, endIndex);
            } else {
                return string + FILE_SEPARATOR;
            }
        }
    }

    /**
     * 确保结尾没有 /
     */
    public static String ensureNoSuffix(String string) {
        if (string == null || string.isEmpty()) return "";
        int length = string.length();
        if (length == 1) {
            if (string.charAt(0) == FILE_SEPARATOR_CHAR) return "";
            else return string;
        } else {
            if (string.charAt(length - 1) == FILE_SEPARATOR_CHAR) {
                int endIndex = length - 1;
                for (int i = length - 2; i >= 0; i--) {
                    if (string.charAt(i) == FILE_SEPARATOR_CHAR) {
                        endIndex--;
                    } else break;
                }
                return string.substring(0, endIndex);
            } else {
                return string;
            }
        }
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

    public static boolean isDirnameIllegal(String dirname) {
        if (StringUtil.isEmpty(dirname)) return true;
        return dirname.contains(".") || dirname.contains("?")
                || dirname.contains("*") || dirname.contains("=")
                || dirname.contains(">") || dirname.contains("<")
                || dirname.contains(":") || dirname.contains("\"")
                || dirname.contains("|") || dirname.contains("\\");
    }

    public static boolean isPathIllegal(String path) {
        return path.contains("\\");
    }

    public static String modifyPathSeparator(String path) {
        if (StringUtil.isEmpty(path)) return "";
        return path.replace("\\", "/");
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
