package com.springboot.admin.service.handler;


import com.springboot.admin.enums.CaptchaType;

public interface CaptchaHandler {
    /** 支持的验证码类型 */
    CaptchaType type();

    /** 具体的生成与发送逻辑 */
    void executeSend(String target, String code, String scene);

    /** 是否需要返回内容给前端（比如图片 Base64） */
    default String getCaptchaContent(String code) { return null; }
}
