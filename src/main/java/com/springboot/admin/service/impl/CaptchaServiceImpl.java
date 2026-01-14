package com.springboot.admin.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.springboot.admin.model.dto.CaptchaDTO;
import com.springboot.admin.service.ICaptchaService;
import com.springboot.admin.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * - 负责生成验证码图片、存储到 Redis、校验验证码、删除验证码。
 * - 使用 Reactor Mono 保证异步非阻塞。
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements ICaptchaService {

    /**
     * Redis key 前缀
     */
    private static final String CAPTCHA_PREFIX = "captcha:";
    /**
     * 验证码过期时间（分钟）
     */
    private static final long CAPTCHA_EXPIRE_MINUTES = 5;
    /**
     * 验证码长度
     */
    private static final int CAPTCHA_LENGTH = 4;
    /**
     * 验证码图片宽度
     */
    private static final int CAPTCHA_WIDTH = 120;
    /**
     * 验证码图片高度
     */
    private static final int CAPTCHA_HEIGHT = 40;

    private final IRedisService redisService;

    public CaptchaServiceImpl(IRedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 生成验证码：
     * 1. 使用 Hutool 生成验证码图片和文本
     * 2. 随机生成 captchaId 作为 Redis key
     * 3. 将验证码文本存入 Redis，设置过期时间
     * 4. 返回 CaptchaDTO 给前端
     */
    @Override
    public Mono<CaptchaDTO> generateCaptcha() {
        return Mono.defer(() -> {
            // 1. 使用 Hutool 生成验证码图片
            LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, CAPTCHA_LENGTH, 10);

            // 2. 随机生成 captchaId
            String captchaId = RandomStringUtils.randomAlphanumeric(16);

            // 3. 获取验证码文本和图片
            String captchaCode = lineCaptcha.getCode();
            String imageBase64 = lineCaptcha.getImageBase64();

            // 4. 存储到 Redis，并等待写入结果
            return redisService.setValue(CAPTCHA_PREFIX + captchaId, captchaCode, CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES)
                    .flatMap(success -> {
                        if (!success) {
                            // 写入失败，返回错误
                            log.error("验证码写入 Redis 失败: id={}, code={}", captchaId, captchaCode);
                            return Mono.error(new RuntimeException("验证码写入失败"));
                        }

                        // 5. 构造返回对象
                        CaptchaDTO dto = new CaptchaDTO();
                        dto.setCaptchaId(captchaId);
                        dto.setCaptchaImage(imageBase64);
                        dto.setExpireTime(System.currentTimeMillis() + CAPTCHA_EXPIRE_MINUTES * 60 * 1000);
                        dto.setCaptchaEnabled(true);

                        log.info("生成验证码成功: captchaId={}", captchaId);
                        return Mono.just(dto);
                    });
        });
    }

    /**
     * 校验验证码：
     * 1. 从 Redis 获取验证码
     * 2. 比较用户输入和存储值
     * 3. 返回校验结果
     */
    @Override
    public Mono<Boolean> validateCaptcha(String captchaId, String userInput) {
        String key = CAPTCHA_PREFIX + captchaId;
        return redisService.getValue(key)
                .flatMap(storedCode -> {
                    if (storedCode == null) {
                        log.warn("验证码为空: id={}", captchaId);
                        return Mono.just(false);
                    }
                    boolean valid = storedCode.trim().equalsIgnoreCase(userInput.trim());
                    log.info("校验验证码: id={}, userInput={}, storedCode={}, result={}", captchaId, userInput, storedCode, valid);
                    return Mono.just(valid);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Redis中未找到验证码: id={}", captchaId);
                    return Mono.just(false);
                }));
    }

    /**
     * 删除验证码，返回是否成功
     */
    @Override
    public Mono<Boolean> deleteCaptchaReturnBoolean(String captchaId) {
        return redisService.deleteKey(CAPTCHA_PREFIX + captchaId)
                .map(count -> count != null && count > 0)
                .doOnNext(success -> log.info("删除验证码: id={}, success={}", captchaId, success))
                .onErrorReturn(false);
    }

    /**
     * 删除验证码，返回删除数量
     */
    @Override
    public Mono<Long> deleteCaptchaReturnCount(String captchaId) {
        return redisService.deleteKey(CAPTCHA_PREFIX + captchaId)
                .doOnNext(count -> log.info("删除验证码: id={}, count={}", captchaId, count))
                .onErrorReturn(0L);
    }
}
