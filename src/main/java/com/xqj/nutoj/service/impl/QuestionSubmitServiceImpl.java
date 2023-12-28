package com.xqj.nutoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqj.nutoj.common.ErrorCode;
import com.xqj.nutoj.constant.CommonConstant;
import com.xqj.nutoj.exception.BusinessException;
import com.xqj.nutoj.judge.JudgeService;
import com.xqj.nutoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xqj.nutoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xqj.nutoj.model.dto.record.RecordQueryRequest;
import com.xqj.nutoj.model.entity.Question;
import com.xqj.nutoj.model.entity.QuestionSubmit;
import com.xqj.nutoj.model.entity.Record;
import com.xqj.nutoj.model.entity.User;
import com.xqj.nutoj.model.enums.QuestionSubmitLanguageEnum;
import com.xqj.nutoj.model.enums.QuestionSubmitStatusEnum;
import com.xqj.nutoj.model.vo.QuestionSubmitVO;
import com.xqj.nutoj.mq.SendMessage;
import com.xqj.nutoj.service.QuestionService;
import com.xqj.nutoj.service.QuestionSubmitService;
import com.xqj.nutoj.mapper.QuestionSubmitMapper;
import com.xqj.nutoj.service.RecordService;
import com.xqj.nutoj.service.UserService;
import com.xqj.nutoj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.xqj.nutoj.mq.MyMessageProducer;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.xqj.nutoj.constant.MqConstant.EXCHANGE_NAME;
import static com.xqj.nutoj.constant.MqConstant.ROUTING_KEY;


/**
* @author xuqingjian
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2023-08-09 15:49:27
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private RecordService recordService;

    @Resource
    private MyMessageProducer myMessageProducer;

    @Resource
    @Lazy
    private JudgeService judgeService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser 登录用户
     * @return 提交题目 id
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        long userId = loginUser.getId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 封装查询对象，根据用户 id 查询 record
        RecordQueryRequest recordQueryRequest = new RecordQueryRequest();
        recordQueryRequest.setUserId(userId);
        QueryWrapper<Record> queryWrapper = recordService.getQueryWrapper(recordQueryRequest);
        Record record = recordService.getOne(queryWrapper);
        if (record == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        // 执行判题服务
        Long questionSubmitId = questionSubmit.getId();
        // 发送消息
        SendMessage message = new SendMessage();
        message.setQuestionSubmitId(questionSubmitId);
        message.setUserId(userId);
        myMessageProducer.sendMessage(EXCHANGE_NAME, ROUTING_KEY, message);
//        myMessageProducer.sendMessage(EXCHANGE_NAME, ROUTING_KEY, String.valueOf(questionSubmitId));
        // 异步执行判题服务
//        CompletableFuture.runAsync(() ->{
//            judgeFeignClient.doJudge(questionSubmitId);
//        });

        // todo: 设置提交数
        Integer submitNum = question.getSubmitNum();
        Question updateQuestion = new Question();
        synchronized (question.getSubmitNum()) {
            submitNum = submitNum + 1;
            updateQuestion.setId(questionId);
            updateQuestion.setSubmitNum(submitNum);
            save = questionService.updateById(updateQuestion);
            if (!save) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据保存失败");
            }
        }
        Integer userSubmitNum = record.getSubmitNum();
        Record updateRecord = new Record();
        synchronized (record.getSubmitNum()) {
            userSubmitNum = userSubmitNum + 1;
            updateRecord.setId(questionId);
            updateRecord.setSubmitNum(userSubmitNum);
            save = recordService.updateById(updateRecord);
            if (!save) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据保存失败");
            }
        }

        return questionSubmitId;
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取查询封装类（单个）
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * 获取查询脱敏信息
     *
     * @param questionSubmitPage 题目提交分页
     * @param loginUser          直接获取到用户信息，减少查询数据库
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    /**
     * 获取查询脱敏信息（列表）
     *
     * @param questionSubmitQueryRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<QuestionSubmitVO> getQuestionSubmitVOList(QuestionSubmitQueryRequest questionSubmitQueryRequest, User loginUser) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 根据查询信息获取列表
        List<QuestionSubmit> questionSubmitList = list(getQueryWrapper(questionSubmitQueryRequest));
        // 脱敏列表
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        return questionSubmitVOList;
    }


}




