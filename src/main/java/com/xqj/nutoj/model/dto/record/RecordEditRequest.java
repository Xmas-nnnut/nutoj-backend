package com.xqj.nutoj.model.dto.record;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 编辑请求
 *
 */
@Data
public class RecordEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

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