package com.xqj.nutoj.judge.codesandbox;

import com.xqj.nutoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xqj.nutoj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 静态代理
 * 使用代理模式，提供一个 Proxy，来增强代码沙箱的能力（代理模式的作用就是增强能力）
 * 代理模式的实现原理：
 * 1.实现被代理的接口
 * 2.通过构造函数接受一个被代理的接口实现类
 * 3.调用被代理的接口实现类，在调用前后增加对应的操作
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;


    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：" + executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息：" + executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
