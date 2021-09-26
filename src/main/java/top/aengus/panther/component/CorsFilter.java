package top.aengus.panther.component;

import org.springframework.stereotype.Component;
import top.aengus.panther.core.Constants;
import top.aengus.panther.tool.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsFilter implements Filter {

    private static final String[] ALLOW_METHODS = {"GET", "POST", "PUT", "DELETE" };

    private static final String[] ALLOW_HEADERS = {
            "Content-Type", "X-CAF-Authorization-Token", "X-TOKEN", "Set-Cookie",
            Constants.AUTHORIZATION, Constants.UPLOAD_TOKEN
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.addHeader("Access-Control-Allow-Credentials", "true");
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.addHeader("Access-Control-Allow-Methods", StringUtil.joinToString(ALLOW_METHODS));
        res.addHeader("Access-Control-Allow-Headers", StringUtil.joinToString(ALLOW_HEADERS));
        if (((HttpServletRequest) request).getMethod().equals("OPTIONS")) {
            response.getWriter().println("ok");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig filterConfig) {}
}