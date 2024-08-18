package com.mtons.mblog.config;

import com.mtons.mblog.shiro.AccountRealm;
import com.mtons.mblog.shiro.AccountSubjectFactory;
import com.mtons.mblog.shiro.AuthenticatedFilter;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro权限管理的配置
 *
 * @author langhsu
 * @since 3.0
 */
@Configuration
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
public class ShiroConfiguration {
    @Bean
    public SubjectFactory subjectFactory() {
        return new AccountSubjectFactory();
    }

    @Bean
    public Realm accountRealm() {
        return new AccountRealm();
    }

    @Bean
    public CacheManager shiroCacheManager(net.sf.ehcache.CacheManager cacheManager) {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManager(cacheManager);
        return ehCacheManager;
    }

    /**
     * Shiro的过滤器链
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setSuccessUrl("/");
        shiroFilter.setUnauthorizedUrl("/error/reject.html");

        HashMap<String, Filter> filters = new HashMap<>();
        filters.put("authc", new AuthenticatedFilter());
        shiroFilter.setFilters(filters);

        /**
         * 配置shiro拦截器链
         *
         * anon  不需要认证
         * authc 需要认证
         * user  验证通过或RememberMe登录的都可以
         *
         * 顺序从上到下,优先级依次降低
         *
         */
        Map<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("/dist/**", "anon");
        hashMap.put("/theme/**", "anon");
        hashMap.put("/storage/**", "anon");
        hashMap.put("/login", "anon");
        hashMap.put("/user/**", "authc");
        hashMap.put("/settings/**", "authc");
        hashMap.put("/post/editing", "authc");
        hashMap.put("/post/submit", "authc");
        hashMap.put("/post/delete/*", "authc");
        hashMap.put("/post/upload", "authc");

        hashMap.put("/iwdael/channel/list", "authc,perms[channel:list]");
        hashMap.put("/iwdael/channel/update", "authc,perms[channel:update]");
        hashMap.put("/iwdael/channel/delete", "authc,perms[channel:delete]");

        hashMap.put("/iwdael/post/list", "authc,perms[post:list]");
        hashMap.put("/iwdael/post/update", "authc,perms[post:update]");
        hashMap.put("/iwdael/post/delete", "authc,perms[post:delete]");

        hashMap.put("/iwdael/comment/list", "authc,perms[comment:list]");
        hashMap.put("/iwdael/comment/delete", "authc,perms[comment:delete]");

        hashMap.put("/iwdael/user/list", "authc,perms[user:list]");
        hashMap.put("/iwdael/user/update_role", "authc,perms[user:role]");
        hashMap.put("/iwdael/user/pwd", "authc,perms[user:pwd]");
        hashMap.put("/iwdael/user/open", "authc,perms[user:open]");
        hashMap.put("/iwdael/user/close", "authc,perms[user:close]");

        hashMap.put("/iwdael/options/index", "authc,perms[options:index]");
        hashMap.put("/iwdael/options/update", "authc,perms[options:update]");

        hashMap.put("/iwdael/role/list", "authc,perms[role:list]");
        hashMap.put("/iwdael/role/update", "authc,perms[role:update]");
        hashMap.put("/iwdael/role/delete", "authc,perms[role:delete]");

        hashMap.put("/iwdael/theme/*", "authc,perms[theme:index]");

        hashMap.put("/iwdael", "authc,perms[iwdael]");
        hashMap.put("/iwdael/*", "authc,perms[iwdael]");

        shiroFilter.setFilterChainDefinitionMap(hashMap);
        return shiroFilter;
    }

}
