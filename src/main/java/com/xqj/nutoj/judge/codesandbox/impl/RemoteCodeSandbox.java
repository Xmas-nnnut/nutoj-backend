package com.xqj.nutoj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xqj.nutoj.common.ErrorCode;
import com.xqj.nutoj.exception.BusinessException;
import com.xqj.nutoj.judge.codesandbox.CodeSandbox;
import com.xqj.nutoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.xqj.nutoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class RemoteCodeSandbox implements CodeSandbox {
    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://localhost:8090/codesandbox/executeCode";
//        String url = "http://178.173.230.157:8090/codesandbox/executeCode";
//        String url = "http://101.37.21.199:8090/codesandbox/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        //字符串转换响应类
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }

}
