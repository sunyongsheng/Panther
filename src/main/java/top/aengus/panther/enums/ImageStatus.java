package top.aengus.panther.enums;


/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/6/8
 */
public enum ImageStatus {
    NORMAL(0, "正常"),
    DELETED(-1, "回收站，主动删除"),
    DELETED_AUTO(-2, "回收站，跟随App删除");

    private final Integer code;
    private final String desc;

    ImageStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ImageStatus fromCode(Integer code) {
        for (ImageStatus status : ImageStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return ImageStatus.NORMAL;
    }

    public static ImageStatus fromCodeForDTO(Integer code) {
        if (DELETED.code.equals(code) || DELETED_AUTO.code.equals(code)) {
            return DELETED;
        }
        return NORMAL;
    }
}
