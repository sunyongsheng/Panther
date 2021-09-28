package top.aengus.panther.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.aengus.panther.core.Constants;
import top.aengus.panther.core.Response;
import top.aengus.panther.enums.TokenStage;
import top.aengus.panther.model.token.AppToken;
import top.aengus.panther.service.AppTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@Order(0)
public class ImageUploadFilter extends AbstractRequestFilter {

    private final AppTokenService appTokenService;

    @Autowired
    public ImageUploadFilter(AppTokenService appTokenService) {
        this.appTokenService = appTokenService;

        addInterceptUrl("/api/v1/image");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        String uploadToken = request.getHeader(Constants.Header.UPLOAD_TOKEN);
        if (uploadToken == null || uploadToken.isEmpty()) {
            log.warn("拦截到上传请求，地址【{} {}】，无有效UploadToken", request.getMethod(), request.getRequestURI());
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(new Response<String>().noAuth().msg("请使用UploadToken")));
            return;
        }
        AppToken appToken = appTokenService.findByTokenAndStage(uploadToken, TokenStage.UPLOAD_V1);
        if (appToken == null) {
            log.warn("拦截到上传请求，地址【{} {}】，{}为【{}】", request.getMethod(), request.getRequestURI(), Constants.Header.UPLOAD_TOKEN, uploadToken);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(new Response<String>().noAuth().msg("AppKey无效")));
            return;
        }
        request.setAttribute(Constants.Header.INTERNAL_APP_KEY, appToken.getAppKey());
        filterChain.doFilter(request, response);
    }

}
