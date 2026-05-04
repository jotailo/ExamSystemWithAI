package com.liuxuanhui.aicodehelper.exam.controller;

import com.liuxuanhui.aicodehelper.exam.utils.CreateVerificationCode;
import com.liuxuanhui.aicodehelper.exam.utils.RedisUtil;
import com.liuxuanhui.aicodehelper.exam.vo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@Tag(name = "工具类接口")
@RequestMapping(value = {"/util", "/exam/util"})
@RequiredArgsConstructor
public class UtilController {

    private final RedisUtil redisUtil;

    @GetMapping("/getCodeImg")
    @Operation(summary = "获取验证码图片流")
    public void getIdentifyImage(@RequestParam("id") String id, HttpServletResponse response) throws IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "No-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        CreateVerificationCode code = new CreateVerificationCode();
        BufferedImage image = code.getIdentifyImg();
        code.getG().dispose();
        ImageIO.write(image, "JPEG", response.getOutputStream());
        redisUtil.set(id, code.getCode());
    }

    @GetMapping("/getCode")
    @Operation(summary = "获取验证码")
    public CommonResult<String> getCode(@RequestParam("id") String id) {
        return CommonResult.<String>builder().data((String) redisUtil.get(id)).build();
    }
}
