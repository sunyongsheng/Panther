package top.aengus.panther.model.image;

import lombok.Data;

import java.util.List;

@Data
public class RefreshResult {

    private List<Item> invalidItems;

    @Data
    public static class Item {

        private Long id;

        private String name;

        private String url;

        private Long uploadTime;

        private String absolutePath;

        private String relativePath;

        private String ownerApp;

        private String ownerAppKey;

        private Long size;

        private String desc;

    }
}
