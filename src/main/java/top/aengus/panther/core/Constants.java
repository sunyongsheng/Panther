package top.aengus.panther.core;

public class Constants {

    public static final int PAGE_SIZE = 20;

    /**
     * 图片及App在回收站中的保留时间
     */
    public static final int RETENTION_DAYS = 30;

    public static class Cookie {
        /**
         * 存储在Cookie中，进行Router请求时鉴权使用
         */
        public static final String ACCESS_TOKEN = "access-token";
    }

    public static class Header {
        /**
         * 存储在Request Header中，Admin请求时鉴权使用
         */
        public static final String AUTHORIZATION = "Authorization";

        /**
         * 存储在Request Header中，图片上传时鉴权使用
         */
        public static final String UPLOAD_TOKEN = "Upload-Token";

        /**
         * 存储在Request Header中，图片上传时鉴权成功后服务器写入到Header中
         */
        public static final String INTERNAL_APP_KEY = "internal-app-key";
    }

}
