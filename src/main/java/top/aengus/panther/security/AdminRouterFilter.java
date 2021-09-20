package top.aengus.panther.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import top.aengus.panther.core.Constants;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.TokenUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@Order(1)
public class AdminRouterFilter extends AbstractRequestFilter {

    private final PantherConfigService configService;

    public AdminRouterFilter(PantherConfigService configService) {
        this.configService = configService;
        addInterceptUrl("/admin/**");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        if (!configService.hasInstalled()) {
            log.warn("Panther未安装时访问地址 {} {}", urlPathHelper.getRequestUri(request), request.getMethod());
            response.sendRedirect("/install");
            return;
        }
        String adminUsername = configService.getAdminUsername();
        if (request.getCookies() == null ||
                Arrays.stream(request.getCookies()).noneMatch(cookie -> Constants.ACCESS_TOKEN.equals(cookie.getName()) && TokenUtil.verify(cookie.getValue(), adminUsername))) {
            log.warn("拦截到访问Admin页面请求 {} {}", urlPathHelper.getRequestUri(request), request.getMethod());
            response.sendRedirect("/login");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
