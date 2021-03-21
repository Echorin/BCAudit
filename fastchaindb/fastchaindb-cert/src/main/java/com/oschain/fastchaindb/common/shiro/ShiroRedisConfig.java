package com.oschain.fastchaindb.common.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.apache.shiro.mgt.SecurityManager;

import java.util.LinkedHashMap;

@Configuration
public class ShiroRedisConfig {

    /**
     * shiro的拦截器，在spring mvc中也有相同的配置，这里不再多说
     * @author kevin
     * @date 2018/8/29
     * @param securityManager
     * @return
     **/
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置 securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 登录配置
        //shiroFilterFactoryBean.setLoginUrl("http://localhost:8080/cert/login");
        shiroFilterFactoryBean.setLoginUrl("/home");
        shiroFilterFactoryBean.setSuccessUrl("/");
        shiroFilterFactoryBean.setUnauthorizedUrl("/error?code=403");

        // 这里配置授权链，跟mvc的xml配置一样
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 设置免认证 url
//        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(shiroProperties.getAnonUrl(), ",");
//        for (String url : anonUrls) {
//            filterChainDefinitionMap.put(url, "anon");
//        }

        //测试接口用
        filterChainDefinitionMap.put("/test/**", "anon");
        filterChainDefinitionMap.put("/down/**", "anon");
        filterChainDefinitionMap.put("/cert/info/view", "anon");
        filterChainDefinitionMap.put("/cert/info/dataview", "anon");
        filterChainDefinitionMap.put("/cert/info/block", "anon");
        filterChainDefinitionMap.put("/cert/info/blockdata", "anon");
        filterChainDefinitionMap.put("/uploadfile/**", "anon");
        filterChainDefinitionMap.put("/certcheck", "anon");
        filterChainDefinitionMap.put("/checkbyid", "anon");
        filterChainDefinitionMap.put("/register/**", "anon");
        filterChainDefinitionMap.put("/assets/**", "anon");
        filterChainDefinitionMap.put("/module/**", "anon");
        filterChainDefinitionMap.put("/api/**", "anon");
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/home", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger-*/**", "anon");
        filterChainDefinitionMap.put("/query/**", "anon");
        filterChainDefinitionMap.put("/logout", "logout");



        // 除上以外所有 url都必须认证通过才可以访问，未通过认证自动访问 LoginUrl
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        System.out.println("Shiro拦截器工厂类注入成功");
        return shiroFilterFactoryBean;
    }
    /**
     * 配置各种manager,跟xml的配置很像，但是，这里有一个细节，就是各个set的次序不能乱
     * @author Super小靖
     * @date 2018/8/29
     * @param realm
     * @return
     **/
    @Bean
    @DependsOn({"shiroRealm"})
    public SecurityManager securityManager(ShiroRedisUserRealm realm, RedisTemplate<Object, Object> template) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 配置 rememberMeCookie 查看源码可以知道，这里的rememberMeManager就仅仅是一个赋值，所以先执行
        securityManager.setRememberMeManager(rememberMeManager());
        // 配置 缓存管理类 cacheManager，这个cacheManager必须要在前面执行，因为setRealm 和 setSessionManage都有方法初始化了cachemanager,看下源码就知道了
        securityManager.setCacheManager(cacheManager(template));
        // 配置 SecurityManager，并注入 shiroRealm 这个跟springmvc集成很像，不多说了
        securityManager.setRealm(realm);
        // 配置 sessionManager
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }
    /**
     * 生成一个ShiroRedisCacheManager 这没啥好说的
     * @author Super小靖
     * @date 2018/8/29
     * @param template
     * @return
     **/
    private ShiroRedisCacheManager cacheManager(RedisTemplate template){
        return new ShiroRedisCacheManager(template);
    }

    /**
     * 这是我自己的realm 我自定义了一个密码解析器，这个比较简单，稍微跟一下源码就知道这玩意
     * @param matcher
     * @param userService
     * @return
     */
//    @Bean
//    @DependsOn({"hashedCredentialsMatcher"})
//    public ShiroRealm shiroRealm(HashedCredentialsMatcher matcher, SysUserService userService) {
//        // 配置 Realm，需自己实现
//        return new ShiroRealm(matcher,userService);
//    }

    @Bean(name = "shiroRealm")
    @DependsOn("hashedCredentialsMatcher")
    public ShiroRedisUserRealm userRealm() {
        ShiroRedisUserRealm userRealm = new ShiroRedisUserRealm();
        userRealm.setCredentialsMatcher(createMatcher());
        return userRealm;
    }

    /**
     * 密码解析器 有好几种，我这是MD5 1024次加密
     * @return
     */
    @Bean(name = "hashedCredentialsMatcher")
    public HashedCredentialsMatcher createMatcher(){

        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("md5");  //散列算法
        matcher.setHashIterations(3);  //散列次数

//        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(DefaultConstants.HASH_ALGORITHM);
//        matcher.setHashIterations(DefaultConstants.HASH_INTERATIONS);
        return matcher;
    }
    /**
     * rememberMe cookie 效果是重开浏览器后无需重新登录
     *
     * @return SimpleCookie
     */
    private SimpleCookie rememberMeCookie() {
        // 这里的Cookie的默认名称是 CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME
        SimpleCookie cookie = new SimpleCookie(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME);
        // 是否只在https情况下传输
        cookie.setSecure(false);
        // 设置 cookie 的过期时间，单位为秒，这里为一天,cookie有效时长，默认30天
        cookie.setMaxAge(2592000);
        return cookie;
    }

    /**
     * cookie管理对象
     *
     * @return CookieRememberMeManager
     */
    private CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        // rememberMe cookie 加密的密钥
        cookieRememberMeManager.setCipherKey(Base64.decode("ZWvohmPdUsAWT3=KpPqda"));
        return cookieRememberMeManager;
    }

    /**
     * 用于开启 Thymeleaf 中的 shiro 标签的使用
     * 我没用过，不作评论
     * @return ShiroDialect shiro 方言对象
     */
//    @Bean
//    public ShiroDialect shiroDialect() {
//        return new ShiroDialect();
//    }


    /**
     * session 管理对象
     *
     * @return DefaultWebSessionManager
     */
    private ShiroRedisSessionManager sessionManager() {

        ShiroRedisSessionManager sessionManager = new ShiroRedisSessionManager();
        // 自定义sessionDao
        sessionManager.setSessionDAO(new EnterpriseCacheSessionDAO());
        // session的失效时长,单位是毫秒
        sessionManager.setGlobalSessionTimeout(1800000);
        // 删除失效的session
        sessionManager.setDeleteInvalidSessions(true);
        // 所有的session一定要将id设置到Cookie之中，需要提供有Cookie的操作模版
        sessionManager.setSessionIdCookie(new SimpleCookie("hiat.session.id"));
        // 定义sessionIdCookie模版可以进行操作的启用
        sessionManager.setSessionIdCookieEnabled(true);
        //logger.info("配置sessionManager");




//
//        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
//        // 设置session超时时间，单位为毫秒
//        sessionManager.setGlobalSessionTimeout(1800000);
//        sessionManager.setSessionIdCookie(new SimpleCookie("hayek.session.id"));
//        // 网上各种说要自定义sessionDAO 其实完全不必要，shiro自己就自定义了一个，可以直接使用，还有其他的DAO，自行查看源码即可
//        sessionManager.setSessionDAO(new EnterpriseCacheSessionDAO());



        return sessionManager;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 开启aop注解支持
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
