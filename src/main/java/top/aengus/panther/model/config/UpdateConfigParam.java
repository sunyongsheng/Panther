package top.aengus.panther.model.config;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UpdateConfigParam {

    @Email
    private String adminEmail;

    @NotBlank
    private String urlOrPath;
}
