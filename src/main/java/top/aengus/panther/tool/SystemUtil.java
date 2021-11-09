package top.aengus.panther.tool;

public class SystemUtil {

    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }
}
