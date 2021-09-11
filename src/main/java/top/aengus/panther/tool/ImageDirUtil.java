package top.aengus.panther.tool;

import java.io.File;

public class ImageDirUtil {

    public static final String NAME_APP = "app";
    public static final String NAME_COMMON = "common";
    public static final String NAME_POST = "post";
    public static final String NAME_TRAVEL = "travel";
    public static final String NAME_SCREENSHOTS = "screenshots";

    public static boolean isValidDir(String dir) {
        return (StringUtil.isEmpty(dir) || NAME_COMMON.equals(dir) || NAME_POST.equals(dir) || NAME_TRAVEL.equals(dir));
    }

    public static String concat(String parent, String current) {
        return parent + FileUtil.FILE_SEPARATOR + current;
    }

    public static void init(String basePath) {
        FileUtil.checkAndCreateDir(new File(basePath, NAME_APP));
        FileUtil.checkAndCreateDir(new File(basePath, NAME_COMMON));
        FileUtil.checkAndCreateDir(new File(basePath, NAME_POST));
        FileUtil.checkAndCreateDir(new File(basePath, NAME_TRAVEL));
        FileUtil.checkAndCreateDir(new File(basePath, NAME_SCREENSHOTS));
    }
}
