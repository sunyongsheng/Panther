package top.aengus.panther.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.aengus.panther.core.Constants;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.service.AppInfoService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Component
public class ApiRequestInterceptor implements HandlerInterceptor {

    private static final String APP_ID = "App-Id";

    @Autowired
    private AppInfoService appInfoService;

    public static ApiRequestInterceptor interceptorHolder;

    @PostConstruct
    public void init() {
        interceptorHolder = this;
        interceptorHolder.appInfoService = this.appInfoService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        String appId = request.getHeader(APP_ID);
        if (appId == null || appId.isEmpty()) {
            log.warn("拦截到请求，地址【{} {}】，无有效AppId", request.getMethod(), request.getRequestURI());
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(new Response<String>().noAuth().msg("请使用AppID")));
            return false;
        }
        AppInfo appInfo = interceptorHolder.appInfoService.findByAppId(appId);
        if (appInfo == null) {
            log.warn("拦截到请求，地址【{} {}】，{}为【{}】", request.getMethod(), request.getRequestURI(), APP_ID, appId);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(new Response<String>().noAuth().msg("AppID无效")));
            return false;
        }
        request.setAttribute(Constants.REQUEST_APP_INFO_INTERNAL, appInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        request.setAttribute(Constants.REQUEST_APP_INFO_INTERNAL, null);
    }
}
