package com.xqj.nutoj.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public JwtInterceptor getJwtInterceptor() {
        return new JwtInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自定义拦截器，添加拦截路径和排除拦截路径
        registry.addInterceptor(getJwtInterceptor())
                .addPathPatterns("/**") // 需要进行JWT验证的路径
                .excludePathPatterns("/user/login", "/user/register") // 登录注册接口不需要验证
                .excludePathPatterns("/swagger-resources/**", "/swagger-ui/**", "/v2/**", "/webjars/**", "/doc.html", "/error");//放行swagger
    }

}