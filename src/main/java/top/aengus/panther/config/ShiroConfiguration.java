package top.aengus.panther.config;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.aengus.panther.security.AdminRealm;

@Configuration
public class ShiroConfiguration {

    @Bean
    Realm adminRealm() {
        return new AdminRealm();
    }

    @Bean
    SecurityManager securityManager() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setRealm(adminRealm());
        return securityManager;
    }

    @Bean
    ShiroFilterChainDefinition filterFactory() {
        DefaultShiroFilterChainDefinition definition = new DefaultShiroFilterChainDefinition();
//        definition.addPathDefinition();
        return definition;
    }
}
