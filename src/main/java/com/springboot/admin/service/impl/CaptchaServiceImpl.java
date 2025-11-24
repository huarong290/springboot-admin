package com.springboot.admin.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.springboot.admin.model.dto.CaptchaDTO;
import com.springboot.admin.service.ICaptchaService;
import com.springboot.admin.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CaptchaServiceImpl implements ICaptchaService {

    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRE_MINUTES = 5;
    private static final int CAPTCHA_LENGTH = 4;
    @Autowired
    private  IRedisService redisService;
    @Override
    public Mono<CaptchaDTO> generateCaptcha() {
        return Mono.fromSupplier(() -> {
            // 1. 定义验证码图片的宽高
            int width = 120;
            int height = 40;

            // 2. 使用 Hutool 的 LineCaptcha 生成验证码
            LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(width, height, 4, 10);

            // 3. 生成一个验证码 ID
            String captchaId = RandomStringUtils.randomAlphanumeric(16);

            // 4. 获取验证码文本和图片
            String captchaCode = lineCaptcha.getCode();
            String imageBase64 = lineCaptcha.getImageBase64();

            // 5. 存储到 Redis，设置过期时间
            redisService.setValue(CAPTCHA_PREFIX + captchaId, captchaCode, CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);

            // 6. 构造返回对象
            CaptchaDTO dto = new CaptchaDTO();
            dto.setCaptchaId(captchaId);
            dto.setCaptchaImage(imageBase64);
            dto.setExpireTime(System.currentTimeMillis() + CAPTCHA_EXPIRE_MINUTES * 60 * 1000);
            dto.setCaptchaEnabled(true);

            log.debug("生成验证码: captchaId={}, code={}", captchaId, captchaCode);
            return dto;
        });
    }

    @Override
    public Mono<Boolean> validateCaptcha(String captchaId, String userInput) {
        return redisService.getValue(CAPTCHA_PREFIX + captchaId)
                .map(storedCode -> {
                    if (storedCode == null) {
                        log.warn("验证码 {} 已过期或不存在", captchaId);
                        return false;
                    }
                    boolean valid = storedCode.equalsIgnoreCase(userInput);
                    log.debug("校验验证码: captchaId={}, userInput={}, storedCode={}, result={}",
                            captchaId, userInput, storedCode, valid);
                    return valid;
                })
                .defaultIfEmpty(false);
    }
    @Override
    public Mono<Boolean> deleteCaptchaReturnBoolean(String captchaId) {
        return redisService.deleteKey(CAPTCHA_PREFIX + captchaId)
                .map(count -> count != null && count > 0) // 删除数量 > 0 表示成功
                .doOnNext(success -> log.info("删除验证码: captchaId={}, success={}", captchaId, success))
                .onErrorReturn(false);
    }

    /**
     * 删除验证码，返回删除数量
     */
    @Override
    public Mono<Long> deleteCaptchaReturnCount(String captchaId) {
        return redisService.deleteKey(CAPTCHA_PREFIX + captchaId)
                .doOnNext(count -> log.info("删除验证码: captchaId={}, count={}", captchaId, count))
                .onErrorReturn(0L);
    }


}
