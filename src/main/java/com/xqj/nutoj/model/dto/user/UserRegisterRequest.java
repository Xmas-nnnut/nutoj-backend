package com.xqj.nutoj.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户注册请求体(接收前端json)
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserRegisterRequest implements Serializable {

    // serialVersionUID 的作用是验证序列化和反序列化的过程中，对象是否保持一致
    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
