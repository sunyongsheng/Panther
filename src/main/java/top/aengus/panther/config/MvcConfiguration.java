package top.aengus.panther.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import top.aengus.panther.component.ApiRequestInterceptor;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.FileUtil;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/1
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    private static final String FILE_PROTOCOL = "file:///";

    private final PantherConfigService configService;

    @Autowired
    public MvcConfiguration(PantherConfigService configService) {
        this.configService = configService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        if (configService.hasInstalled()) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:static/", FILE_PROTOCOL + FileUtil.ensureSuffix(configService.getSaveRootPath()));
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiRequestInterceptor())
                .addPathPatterns("/api/v1/**");
    }

}
