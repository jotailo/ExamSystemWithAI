package com.liuxuanhui.aicodehelper.exam.exception;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 5565760508056698922L;

    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDesc());
        this.errorCode = errorCode;
    }

    public BusinessException() {
        super();
    }

    public BusinessException(ErrorCode errorCode, String arg0) {
        super(arg0);
        this.errorCode = errorCode;
    }

    public BusinessException(String arg0) {
        super(arg0);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
