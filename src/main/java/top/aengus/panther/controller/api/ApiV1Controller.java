package top.aengus.panther.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 除 /api/v1/image 接口外，继承此类的其他接口均由 {@link top.aengus.panther.security.AdminRequestFilter} 进行拦截
 *
 * @author sunyongsheng (sunyongsheng@bytedance.com)
 * <p>
 * date 2021/4/13
 */
@RequestMapping("/api/v1")
public class ApiV1Controller {
}
