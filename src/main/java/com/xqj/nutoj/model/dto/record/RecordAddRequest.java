package com.xqj.nutoj.model.dto.record;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建请求
 *
 */
@Data
public class RecordAddRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * AC数
     */
    private Integer acceptedNum;

    /**
     * 提交数
     */
    private Integer submitNum;

    private static final long serialVersionUID = 1L;
}