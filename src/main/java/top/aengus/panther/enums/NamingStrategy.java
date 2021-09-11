package top.aengus.panther.enums;

public enum NamingStrategy {

    UUID("使用自动生成的「UUID」作为文件名"),
    ORIGIN("使用「原始名称」作为文件名"),
    DATE_UUID_HYPHEN("使用「日期-UUID」作为文件名"),
    DATE_ORIGIN_HYPHEN("使用「日期-原始名称」作为文件名"),
    DATE_UUID_UNDERLINE("使用「日期_UUID」作为文件名"),
    DATE_ORIGIN_UNDERLINE("使用「日期_原始名称」作为文件名");

    private final String desc;

    NamingStrategy(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
