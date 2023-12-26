package com.xqj.nutoj.model.dto.questioncomment;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 */
@Data
public class QuestionCommentAddRequest implements Serializable {

    /**
     * 内容
     */
    private String content;

    /**
     * 关联题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}