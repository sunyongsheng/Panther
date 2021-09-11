package top.aengus.panther.enums;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/29
 */
public enum AppSettingKey {
    IMG_NAMING_STRATEGY("img_naming_strategy", "图片上传策略");

    private final String code;
    private final String desc;

    AppSettingKey(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
