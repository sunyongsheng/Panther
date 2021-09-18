package top.aengus.panther.security;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.PantherConfigService;

public class AdminRealm extends AuthorizingRealm {

    private final PantherConfigService configService = SpringUtil.getBean(PantherConfigService.class);
    private final AppInfoService appInfoService = SpringUtil.getBean(AppInfoService.class);

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        Object principal = token.getPrincipal();
        if (principal instanceof ImageUploadPrincipal) {
            String appId = ((ImageUploadPrincipal) principal).getAppId();
            if (appId == null || appInfoService.findByAppId(appId) == null) {
                return null;
            }
            return new SimpleAuthenticationInfo(principal, appId, "uploader");
        } else if (principal instanceof AdminRequestPrincipal) {
            String username = ((AdminRequestPrincipal) principal).getUsername();
            if (!username.equals(configService.getAdminUsername())) {
                return null;
            }
            return new SimpleAuthenticationInfo(principal, configService.getAdminPassword(), "admin");
        }
        throw new RuntimeException("不支持的认证信息！");
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 能进入这里说明用户已经通过验证了
        Object principal = principals.getPrimaryPrincipal();
        if (principal instanceof AdminRequestPrincipal) {
            return new SimpleAuthorizationInfo(CollectionUtil.newHashSet("admin"));
        }
        return null;
    }

}
