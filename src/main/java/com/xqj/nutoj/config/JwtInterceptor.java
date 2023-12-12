package com.xqj.nutoj.config;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xqj.nutoj.common.ErrorCode;
import com.xqj.nutoj.common.ResultUtils;
import com.xqj.nutoj.exception.BusinessException;
import com.xqj.nutoj.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.xqj.nutoj.constant.UserConstant.USER_LOGIN_STATE;

/**
 * jwt拦截器
 */
@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String JWT = request.getHeader("Authorization");
        String message="";
        try {
            // 1.校验JWT字符串
            DecodedJWT decodedJWT = JwtUtils.decode(JWT);
            // 2.取出JWT字符串载荷中的tokenID，从Redis中获取⽤户信息
            // todo
            String redisKey = decodedJWT.getClaim("id").asString();
            // 从Redis中获取用户信息
//            String redisKey = "user:" + tokenId;
            Object user = redisTemplate.opsForValue().get(redisKey);
            if (user != null) {
                // 验证通过，可以继续处理请求
                request.setAttribute(USER_LOGIN_STATE,user);
                return true;
            } else {
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }
        } catch (SignatureVerificationException e) {
            message="无效签名";
            log.error("⽆效签名");
        } catch (TokenExpiredException e) {
            message="token已经过期";
            log.error("token已经过期");
        } catch (AlgorithmMismatchException e) {
            message="算法不⼀致";
            log.error("算法不⼀致");
        } catch (Exception e) {
            message="token⽆效";
            log.error("token⽆效", e);
        }
        // 将HttpResult以json的形式响应到前台  HttpResult --> json  (jackson) data
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(ResultUtils.error(ErrorCode.UNAUTHORIZED,message));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }
}