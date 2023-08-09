package com.xqj.nutoj.service;

import com.xqj.nutoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xqj.nutoj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqj.nutoj.model.entity.User;

/**
* @author xuqingjian
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-08-09 15:49:27
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

//    /**
//     * 题目提交（内部服务）
//     *
//     * @param userId
//     * @param questionId
//     * @return
//     */
//    int doQuestionSubmitInner(long userId, long questionId);

}
