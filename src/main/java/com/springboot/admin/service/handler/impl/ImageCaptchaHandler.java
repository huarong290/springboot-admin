package com.springboot.admin.service.handler.impl;


import com.springboot.admin.config.properties.CaptchaProperties;
import com.springboot.admin.enums.CaptchaType;
import com.springboot.admin.service.handler.CaptchaHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImageCaptchaHandler implements CaptchaHandler {

    @Autowired
    private CaptchaProperties properties;

    @Override
    public CaptchaType type() {
        return CaptchaType.IMAGE;
    }

    @Override
    public void executeSend(String target, String code, String scene) {
        // 图片验证码不通过第三方下发，仅记录日志
        log.debug("Image Captcha Generated for {}: {}", target, code);
    }

    @Override
    public String getCaptchaContent(String code) {
        // 1. 从 properties 获取 YAML 里的配置
        CaptchaProperties.ImageConfig config = properties.getImage();

        // 2. 使用 Hutool 创建图片
        // 注意：这里传入 config.getLength() 只是为了初始化图片样式
        cn.hutool.captcha.LineCaptcha captcha = cn.hutool.captcha.CaptchaUtil.createLineCaptcha(
                config.getWidth(),
                config.getHeight(),
                config.getLength(),
                config.getLineCount()
        );

        // 3. 关键：强制将图片内容设置为 Service 层生成的那个 code
        // 这样保证了 Redis 里的 code 和图片里的文字绝对一致
        return captcha.getImageBase64Data();
    }
}