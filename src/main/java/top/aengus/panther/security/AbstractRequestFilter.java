package top.aengus.panther.security;

import lombok.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/18
 */
public abstract class AbstractRequestFilter extends OncePerRequestFilter {

    private final Set<String> interceptUrls = new HashSet<>();
    private final Set<String> excludeUrls = new HashSet<>();

    private final PathMatcher antPathMatcher;
    private final UrlPathHelper urlPathHelper;

    public AbstractRequestFilter() {
        this.antPathMatcher = new AntPathMatcher();
        this.urlPathHelper = new UrlPathHelper();
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String requestUrl = urlPathHelper.getRequestUri(request);
        boolean excludeResult = excludeUrls.stream().anyMatch(url -> antPathMatcher.match(url, requestUrl));
        if (excludeResult) {
            return true;
        }
        return interceptUrls.stream().noneMatch(url -> antPathMatcher.match(url, requestUrl));
    }

    protected void addInterceptUrl(String... urls) {
        interceptUrls.addAll(Arrays.asList(urls));
    }

    protected void addExcludeUrl(String... urls) {
        excludeUrls.addAll(Arrays.asList(urls));
    }

}
