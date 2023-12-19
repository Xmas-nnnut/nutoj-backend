package com.xqj.nutoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqj.nutoj.model.dto.question.QuestionQueryRequest;
import com.xqj.nutoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xqj.nutoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xqj.nutoj.model.entity.Question;
import com.xqj.nutoj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqj.nutoj.model.entity.User;
import com.xqj.nutoj.model.vo.QuestionSubmitVO;
import com.xqj.nutoj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author xuqingjian
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-08-09 15:49:27
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 题目提交
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

    /**
     * 分页获取题目封装列表
     *
     * @param questionSubmitQueryRequest
     * @param loginUser
     * @return
     */
    List<QuestionSubmitVO> getQuestionSubmitVOList(QuestionSubmitQueryRequest questionSubmitQueryRequest, User loginUser);

}
