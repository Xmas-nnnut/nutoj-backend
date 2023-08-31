package com.xqj.nutoj.judge;

import com.xqj.nutoj.judge.strategy.DefaultJudgeStrategy;
import com.xqj.nutoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.xqj.nutoj.judge.strategy.JudgeContext;
import com.xqj.nutoj.judge.strategy.JudgeStrategy;
import com.xqj.nutoj.judge.codesandbox.model.JudgeInfo;
import com.xqj.nutoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
