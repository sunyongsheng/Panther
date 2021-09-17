package top.aengus.panther.security;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/18
 */
@Slf4j
@Component
@Order(0)
public class AdminRequestFilter extends AbstractRequestFilter {

    public AdminRequestFilter() {
        addInterceptUrl("/api/**");
        addExcludeUrl("/api/v1/image");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }
}
