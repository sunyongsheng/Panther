package top.aengus.panther.enums;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/6/12
 */
public enum AppRole {
    NORMAL(0, "普通"),
    SUPER(1, "超级");

    private final Integer code;
    private final String desc;

    AppRole(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AppRole fromCode(Integer code) {
        if (SUPER.getCode().equals(code)) {
            return SUPER;
        } else {
            return NORMAL;
        }
    }
}
