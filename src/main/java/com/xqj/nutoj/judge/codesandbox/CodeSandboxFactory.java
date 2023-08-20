package com.xqj.nutoj.judge.codesandbox;

import com.xqj.nutoj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.xqj.nutoj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.xqj.nutoj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 * 使用工厂模式，根据用户传入的字符串参数（沙箱类别），来生成对应的代码沙箱实现类
 * 此处使用静态工厂模式
 * 由此，我们可以根据字符串动态生成实例，提高了通用性
 */
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱示例
     *
     * @param type 沙箱类型
     * @return CodeSandbox 接口而不是具体的实现类
     */
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
