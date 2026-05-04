package com.liuxuanhui.aicodehelper.controller;

/**
 * @Author:Liu Xuanhui
 * @Description:
 * @DateTime: 2026/3/17 17:40
 * @Component:
 */

import com.liuxuanhui.aicodehelper.ai.AICodeHelperService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private AICodeHelperService aiCodeHelperService;

    @GetMapping(value="/chat",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(int memoryId, String message) {
        return aiCodeHelperService.chatStream(memoryId, message)
            .map(chunk -> ServerSentEvent.<String>builder()
                    .data(chunk)
                    .build());
    }
}
