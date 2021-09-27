package top.aengus.panther.core;

public class Constants {

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
        public static final String APP_INFO_INTERNAL = "internal-app-info";
    }

}
