package com.xqj.nutoj.model.vo;

import com.google.gson.Gson;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论视图
 *
 */
@Data
public class RecordVO implements Serializable {

    private final static Gson GSON = new Gson();

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人信息
     */
    private UserVO user;

    private static final long serialVersionUID = 1L;

}
