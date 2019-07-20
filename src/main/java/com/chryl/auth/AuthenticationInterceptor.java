package com.chryl.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;

import com.chryl.anno.CheckToken;
import com.chryl.anno.PassToken;
import com.chryl.bean.User;
import com.chryl.mapper.UserMapper;
import com.chryl.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Created By Chr on 2019/7/19.
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 前端在每次请求时将JWT放入HTTP Header中的Authorization位。(解决XSS和XSRF问题)
         * <p>
         * 通常 http header中Authorization存放jwt的token
         */
        String token = request.getHeader("Authorization");// Bearer+token
        if (token != null) {
            token = token.replaceFirst("Bearer", "");
        }
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        //检查是否有PassToken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(CheckToken.class)) {
            CheckToken checkToken = method.getAnnotation(CheckToken.class);
            if (checkToken.required()) {
                //进行认证
                if (token == null) {
                    throw new RuntimeException("无token，请重新登录");
                }
                String userId;
                try {
                    //userId 存储在 Claim("id")
                    userId = JWT.decode(token).getClaim("id").asString();
                } catch (JWTDecodeException j) {
                    throw new RuntimeException("访问异常！");
                }
                User user = userMapper.selectByUserId(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在，请重新登录");
                }
                //jwt 验证token
                Boolean verify = JwtUtil.isVerify(token, user);
                if (!verify) {
                    throw new RuntimeException("非法访问！");
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    public static void main(String[] args) {
        String a = "Beareraasdr";
        String bearer = a.replaceFirst("Bearer", "");
        System.out.println(bearer);

    }
}
