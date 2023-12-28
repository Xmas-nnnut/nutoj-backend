package com.xqj.nutoj.mq;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 提交信息类
 *
 **/
@Data
public class SendMessage implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 提交 id
     */
    private Long questionSubmitId;

    private static final long serialVersionUID = 1L;
}