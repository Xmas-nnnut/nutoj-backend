package com.xqj.nutoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqj.nutoj.model.dto.post.PostQueryRequest;
import com.xqj.nutoj.model.dto.questioncomment.QuestionCommentQueryRequest;
import com.xqj.nutoj.model.entity.Post;
import com.xqj.nutoj.model.entity.QuestionComment;
import com.xqj.nutoj.model.vo.PostVO;
import com.xqj.nutoj.model.vo.QuestionCommentVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author xuqingjian
* @description 针对表【question_comment(题目评论表)】的数据库操作Service
* @createDate 2023-12-25 15:37:19
*/
public interface QuestionCommentService extends IService<QuestionComment> {

    /**
     * 校验
     *
     * @param questionComment
     * @param add
     */
    void validQuestionComment(QuestionComment questionComment, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionCommentQueryRequest
     * @return
     */
    QueryWrapper<QuestionComment> getQueryWrapper(QuestionCommentQueryRequest questionCommentQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param questionComment
     * @param request
     * @return
     */
    QuestionCommentVO getQuestionCommentVO(QuestionComment questionComment, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param questionCommentPage
     * @param request
     * @return
     */
    Page<QuestionCommentVO> getQuestionCommentVOPage(Page<QuestionComment> questionCommentPage, HttpServletRequest request);

}
