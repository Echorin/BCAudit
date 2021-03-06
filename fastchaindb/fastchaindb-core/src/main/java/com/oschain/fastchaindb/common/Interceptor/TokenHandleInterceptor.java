package com.oschain.fastchaindb.common.Interceptor;

import com.oschain.fastchaindb.annotations.TokenValid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class TokenHandleInterceptor implements HandlerInterceptor {

    private static final String TOKEN ="token";
    private static final String USER = "user";

    private static final Logger logger = LoggerFactory.getLogger(TokenHandleInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        //如果不是映射到方法，直接通过
        if (!(o instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod= ((HandlerMethod) o);
        Method method=handlerMethod.getMethod();

        //获取方法上的tokenvalid注解
        TokenValid tokenValid = method.getAnnotation(TokenValid.class);

        //如果方法上有tokenValid注解
        if (tokenValid != null) {
            //获取请求上传的token信息
            String token = httpServletRequest.getHeader("TOKEN");

//            //通过 tokenService 对token 进行校验 读者可以自行实现
//            TokenStatusCode statusCode=tokenService.tokenValid(token);
//            //如果token合法
//            if (statusCode==TokenStatusCode.SUCCESS) {
//                //根据token获取到user
//                UserDTO user=tokenService.queryUser(token);
//                if (user != null) {
//                    //将user 添加到 request中，以便后续操作获取user
//                    httpServletRequest.setAttribute(USER, user);
//                    return true;
//                } else {
//                    throw new BusinessException(statusCode.getMessage());
//                }
//
//            } else {
//                throw new BusinessException(statusCode.getMessage());
//            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
