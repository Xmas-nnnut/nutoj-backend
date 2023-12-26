package com.xqj.nutoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqj.nutoj.common.ErrorCode;
import com.xqj.nutoj.constant.CommonConstant;
import com.xqj.nutoj.exception.BusinessException;
import com.xqj.nutoj.exception.ThrowUtils;
import com.xqj.nutoj.mapper.QuestionCommentMapper;
import com.xqj.nutoj.model.dto.questioncomment.QuestionCommentQueryRequest;
import com.xqj.nutoj.model.entity.*;
import com.xqj.nutoj.model.vo.QuestionCommentVO;
import com.xqj.nutoj.model.vo.UserVO;
import com.xqj.nutoj.service.QuestionCommentService;
import com.xqj.nutoj.service.UserService;
import com.xqj.nutoj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author xuqingjian
* @description 针对表【question_comment(题目评论表)】的数据库操作Service实现
* @createDate 2023-12-25 15:37:19
*/
@Service
public class QuestionCommentServiceImpl extends ServiceImpl<QuestionCommentMapper, QuestionComment>
    implements QuestionCommentService {

    @Resource
    private UserService userService;

    @Override
    public void validQuestionComment(QuestionComment questionComment, boolean add) {
        if (questionComment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = questionComment.getContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param questionCommentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionComment> getQueryWrapper(QuestionCommentQueryRequest questionCommentQueryRequest) {
        QueryWrapper<QuestionComment> queryWrapper = new QueryWrapper<>();
        if (questionCommentQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = questionCommentQueryRequest.getSearchText();
        String sortField = questionCommentQueryRequest.getSortField();
        String sortOrder = questionCommentQueryRequest.getSortOrder();
        Long id = questionCommentQueryRequest.getId();
        String title = questionCommentQueryRequest.getTitle();
        String content = questionCommentQueryRequest.getContent();
        Long userId = questionCommentQueryRequest.getUserId();
        Long questionId = questionCommentQueryRequest.getQuestionId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionCommentVO getQuestionCommentVO(QuestionComment questionComment, HttpServletRequest request) {
        QuestionCommentVO questionCommentVO = new QuestionCommentVO();
        BeanUtils.copyProperties(questionComment, questionCommentVO);
        long questionCommentId = questionComment.getId();
        // 1. 关联查询用户信息
        Long userId = questionComment.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionCommentVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = userService.getLoginUserPermitNull(request);
        return questionCommentVO;
    }

    @Override
    public Page<QuestionCommentVO> getQuestionCommentVOPage(Page<QuestionComment> questionCommentPage, HttpServletRequest request) {
        List<QuestionComment> questionCommentList = questionCommentPage.getRecords();
        Page<QuestionCommentVO> questionCommentVOPage = new Page<>(questionCommentPage.getCurrent(), questionCommentPage.getSize(), questionCommentPage.getTotal());
        if (CollectionUtils.isEmpty(questionCommentList)) {
            return questionCommentVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionCommentList.stream().map(QuestionComment::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户
        User loginUser = userService.getLoginUserPermitNull(request);
        // 填充信息
        List<QuestionCommentVO> questionCommentVOList = questionCommentList.stream().map(questionComment -> {
            QuestionCommentVO questionCommentVO = new QuestionCommentVO();
            BeanUtils.copyProperties(questionComment, questionCommentVO);
            Long userId = questionComment.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionCommentVO.setUser(userService.getUserVO(user));
            return questionCommentVO;
        }).collect(Collectors.toList());
        questionCommentVOPage.setRecords(questionCommentVOList);
        return questionCommentVOPage;
    }

}




