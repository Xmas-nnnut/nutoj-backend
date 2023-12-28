package com.xqj.nutoj.judge;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xqj.nutoj.common.ErrorCode;
import com.xqj.nutoj.exception.BusinessException;
import com.xqj.nutoj.judge.codesandbox.CodeSandbox;
import com.xqj.nutoj.judge.codesandbox.CodeSandboxFactory;
import com.xqj.nutoj.judge.codesandbox.CodeSandboxProxy;
import com.xqj.nutoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xqj.nutoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.xqj.nutoj.judge.strategy.JudgeContext;
import com.xqj.nutoj.model.dto.question.JudgeCase;
import com.xqj.nutoj.judge.codesandbox.model.JudgeInfo;
import com.xqj.nutoj.model.dto.record.RecordQueryRequest;
import com.xqj.nutoj.model.entity.Question;
import com.xqj.nutoj.model.entity.QuestionSubmit;
import com.xqj.nutoj.model.entity.Record;
import com.xqj.nutoj.model.enums.QuestionSubmitStatusEnum;
import com.xqj.nutoj.service.QuestionService;
import com.xqj.nutoj.service.QuestionSubmitService;
import com.xqj.nutoj.service.RecordService;
import com.xqj.nutoj.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private RecordService recordService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId, long userId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        // 6）修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));

        // todo: 设置通过数
        Integer acceptedNum = question.getAcceptedNum();
        Question updateQuestion = new Question();
        synchronized (question.getAcceptedNum()) {
            if (Objects.equals(judgeInfo.getMessage(), "Accepted")) {
                acceptedNum = acceptedNum + 1;
            }
            updateQuestion.setId(questionId);
            updateQuestion.setAcceptedNum(acceptedNum);
            boolean save = questionService.updateById(updateQuestion);
            if (!save) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据保存失败");
            }
        }
        // 封装查询对象，根据用户 id 查询 record
        RecordQueryRequest recordQueryRequest = new RecordQueryRequest();
        recordQueryRequest.setUserId(userId);
        QueryWrapper<Record> queryWrapper = recordService.getQueryWrapper(recordQueryRequest);
        Record record = recordService.getOne(queryWrapper);
        if (record == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Integer userAcceptedNum = record.getAcceptedNum();
        Record updateRecord = new Record();
        synchronized (question.getAcceptedNum()) {
            if (Objects.equals(judgeInfo.getMessage(), "Accepted")) {
                userAcceptedNum = userAcceptedNum + 1;
            }
            updateRecord.setId(questionId);
            updateRecord.setAcceptedNum(userAcceptedNum);
            boolean save = recordService.updateById(updateRecord);
            if (!save) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据保存失败");
            }
        }


        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionId);
        return questionSubmitResult;
    }
}
