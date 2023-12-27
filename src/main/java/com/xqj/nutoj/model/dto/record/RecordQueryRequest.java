package com.xqj.nutoj.model.dto.record;

import com.xqj.nutoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RecordQueryRequest extends PageRequest implements Serializable {

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