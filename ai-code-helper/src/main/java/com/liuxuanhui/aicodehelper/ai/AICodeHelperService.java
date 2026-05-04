package com.liuxuanhui.aicodehelper.ai;

import com.liuxuanhui.aicodehelper.ai.guardrail.SafeInputGuardRail;
import com.liuxuanhui.aicodehelper.ai.guardrail.SafeOutputGuardRail;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import dev.langchain4j.service.guardrail.OutputGuardrails;
import reactor.core.publisher.Flux;

import java.util.List;

@InputGuardrails({SafeInputGuardRail.class})
@OutputGuardrails({SafeOutputGuardRail.class})
public interface AICodeHelperService {

//    直接写在注解里面@SystemMessage("你好，我是编程领域的编程小助手，有什么问题我可以为你解答吗？^-^")
    @SystemMessage(fromResource = "system-prompt-test.txt") //指定配置文件
    String chat(String userMessage);

    @SystemMessage(fromResource = "system-prompt-test.txt")
    Report chatforReport(String userMessage);

    record Report(String name, List<String> suggestion){};

    // 流式对话
    @SystemMessage(fromResource = "system-prompt-test.txt")
    Flux<String> chatStream(@MemoryId int memoryId, @UserMessage String userMessage);
}
