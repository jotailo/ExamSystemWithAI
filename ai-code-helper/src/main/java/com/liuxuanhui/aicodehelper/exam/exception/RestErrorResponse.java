package com.liuxuanhui.aicodehelper.exam.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestErrorResponse {

    private String errCode;

    private String errMsg;
}
