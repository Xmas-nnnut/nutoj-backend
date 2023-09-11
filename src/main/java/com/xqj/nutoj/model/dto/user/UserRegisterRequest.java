package com.xqj.nutoj.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户注册请求体(接收前端json)
 *
 */
@Data
public class UserRegisterRequest implements Serializable {

    // serialVersionUID 的作用是验证序列化和反序列化的过程中，对象是否保持一致
    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String userName;
}
