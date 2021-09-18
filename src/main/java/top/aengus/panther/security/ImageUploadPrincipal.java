package top.aengus.panther.security;

public class ImageUploadPrincipal {

    private final String appId;

    public ImageUploadPrincipal(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }
}
