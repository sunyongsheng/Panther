package top.aengus.panther.model.config;

import lombok.Data;

@Data
public class PantherConfigDTO {

    private String adminUsername;

    private String adminEmail;

    private String saveRootPath;

    private String hostUrl;
}
