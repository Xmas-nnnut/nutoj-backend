package com.xqj.nutoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqj.nutoj.annotation.AuthCheck;
import com.xqj.nutoj.common.BaseResponse;
import com.xqj.nutoj.common.DeleteRequest;
import com.xqj.nutoj.common.ErrorCode;
import com.xqj.nutoj.common.ResultUtils;
import com.xqj.nutoj.constant.UserConstant;
import com.xqj.nutoj.exception.BusinessException;
import com.xqj.nutoj.exception.ThrowUtils;
import com.xqj.nutoj.model.dto.questioncomment.QuestionCommentAddRequest;
import com.xqj.nutoj.model.dto.questioncomment.QuestionCommentEditRequest;
import com.xqj.nutoj.model.dto.questioncomment.QuestionCommentQueryRequest;
import com.xqj.nutoj.model.dto.questioncomment.QuestionCommentUpdateRequest;
import com.xqj.nutoj.model.entity.QuestionComment;
import com.xqj.nutoj.model.entity.User;
import com.xqj.nutoj.model.vo.QuestionCommentVO;
import com.xqj.nutoj.service.QuestionCommentService;
import com.xqj.nutoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评论接口
 *
 */
@RestController
@RequestMapping("/question_comment")
@Slf4j
public class QuestionCommentController {

    @Resource
    private QuestionCommentService questionCommentService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param questionCommentAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionComment(@RequestBody QuestionCommentAddRequest questionCommentAddRequest, HttpServletRequest request) {
        if (questionCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionComment questionComment = new QuestionComment();
        BeanUtils.copyProperties(questionCommentAddRequest, questionComment);
        questionCommentService.validQuestionComment(questionComment, true);
        User loginUser = userService.getLoginUser(request);
        questionComment.setUserId(loginUser.getId());
        questionComment.setFavourNum(0);
        questionComment.setThumbNum(0);
        boolean result = questionCommentService.save(questionComment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionCommentId = questionComment.getId();
        return ResultUtils.success(newQuestionCommentId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionComment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionComment oldQuestionComment = questionCommentService.getById(id);
        ThrowUtils.throwIf(oldQuestionComment == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionComment.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionCommentService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionCommentUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionComment(@RequestBody QuestionCommentUpdateRequest questionCommentUpdateRequest) {
        if (questionCommentUpdateRequest == null || questionCommentUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionComment questionComment = new QuestionComment();
        BeanUtils.copyProperties(questionCommentUpdateRequest, questionComment);
        // 参数校验
        questionCommentService.validQuestionComment(questionComment, false);
        long id = questionCommentUpdateRequest.getId();
        // 判断是否存在
        QuestionComment oldQuestionComment = questionCommentService.getById(id);
        ThrowUtils.throwIf(oldQuestionComment == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionCommentService.updateById(questionComment);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionCommentVO> getQuestionCommentVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionComment questionComment = questionCommentService.getById(id);
        if (questionComment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionCommentService.getQuestionCommentVO(questionComment, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionCommentVO>> listQuestionCommentVOByPage(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest,
            HttpServletRequest request) {
        long current = questionCommentQueryRequest.getCurrent();
        long size = questionCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionComment> questionCommentPage = questionCommentService.page(new Page<>(current, size),
                questionCommentService.getQueryWrapper(questionCommentQueryRequest));
        return ResultUtils.success(questionCommentService.getQuestionCommentVOPage(questionCommentPage, request));
    }

    /**
     * 根据题目 id 分页获取列表（封装类）
     *
     * @param questionCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/questionId/list/page/vo")
    public BaseResponse<Page<QuestionCommentVO>> listIdQuestionCommentVOByPage(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest,
                                                                             HttpServletRequest request) {
        if (questionCommentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionCommentQueryRequest.getCurrent();
        long size = questionCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionComment> questionCommentPage = questionCommentService.page(new Page<>(current, size),
                questionCommentService.getQueryWrapper(questionCommentQueryRequest));
        return ResultUtils.success(questionCommentService.getQuestionCommentVOPage(questionCommentPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionCommentVO>> listMyQuestionCommentVOByPage(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest,
            HttpServletRequest request) {
        if (questionCommentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        questionCommentQueryRequest.setUserId(loginUser.getId());
        long current = questionCommentQueryRequest.getCurrent();
        long size = questionCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionComment> questionCommentPage = questionCommentService.page(new Page<>(current, size),
                questionCommentService.getQueryWrapper(questionCommentQueryRequest));
        return ResultUtils.success(questionCommentService.getQuestionCommentVOPage(questionCommentPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionCommentEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestionComment(@RequestBody QuestionCommentEditRequest questionCommentEditRequest, HttpServletRequest request) {
        if (questionCommentEditRequest == null || questionCommentEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionComment questionComment = new QuestionComment();
        BeanUtils.copyProperties(questionCommentEditRequest, questionComment);
        // 参数校验
        questionCommentService.validQuestionComment(questionComment, false);
        User loginUser = userService.getLoginUser(request);
        long id = questionCommentEditRequest.getId();
        // 判断是否存在
        QuestionComment oldQuestionComment = questionCommentService.getById(id);
        ThrowUtils.throwIf(oldQuestionComment == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestionComment.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionCommentService.updateById(questionComment);
        return ResultUtils.success(result);
    }

}
