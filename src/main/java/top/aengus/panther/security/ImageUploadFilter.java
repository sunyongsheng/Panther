package top.aengus.panther.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.aengus.panther.core.Constants;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.service.AppInfoService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@Order(0)
public class ImageUploadFilter extends AbstractRequestFilter {

    private static final String APP_ID = "App-Id";

    private final AppInfoService appInfoService;

    @Autowired
    public ImageUploadFilter(AppInfoService appInfoService) {
        this.appInfoService = appInfoService;

        addInterceptUrl("/api/v1/image");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        String appId = request.getHeader(APP_ID);
        if (appId == null || appId.isEmpty()) {
            log.warn("拦截到请求，地址【{} {}】，无有效AppId", request.getMethod(), request.getRequestURI());
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(new Response<String>().noAuth().msg("请使用AppID")));
            return;
        }
        AppInfo appInfo = appInfoService.findByAppId(appId);
        if (appInfo == null) {
            log.warn("拦截到请求，地址【{} {}】，{}为【{}】", request.getMethod(), request.getRequestURI(), APP_ID, appId);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(new Response<String>().noAuth().msg("AppID无效")));
            return;
        }
        request.setAttribute(Constants.REQUEST_APP_INFO_INTERNAL, appInfo);
        filterChain.doFilter(request, response);
    }

}
