package top.aengus.panther.security;

import cn.hutool.core.collection.ListUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.aengus.panther.core.Constants;
import top.aengus.panther.core.Response;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.TokenUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/18
 */
@Slf4j
@Component
@Order(0)
public class AdminRequestFilter extends AbstractRequestFilter {

    private final PantherConfigService configService;

    @Autowired
    public AdminRequestFilter(PantherConfigService configService) {
        this.configService = configService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(Constants.Header.AUTHORIZATION);
        if (authorization == null || !TokenUtil.verify(authorization, configService.getAdminUsername())) {
            log.warn("拦截到Admin Api请求，地址【{} {}】，无Token", request.getMethod(), request.getRequestURI());
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(new Response<String>().noAuth().msg("Token无效，请先登录！")));
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected List<String> getInterceptUrl() {
        return ListUtil.of("/api/**");
    }

    @Override
    protected List<String> getExcludeUrl() {
        return ListUtil.of("/api/v1/image");
    }
}
