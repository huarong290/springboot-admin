package com.springboot.admin.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.springboot.admin.constants.captcha.CaptchaConstants;
import com.springboot.admin.model.dto.CaptchaDTO;
import com.springboot.admin.service.ICaptchaService;
import com.springboot.admin.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * <p>
 * - 负责生成验证码图片、存储到 Redis、校验验证码、删除验证码
 * - 使用同步阻塞方式，适用于传统 MVC / Servlet 架构
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements ICaptchaService {



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
    public CaptchaDTO generateCaptcha() {

        // 1. 使用 Hutool 生成验证码
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(
                CaptchaConstants.CAPTCHA_WIDTH,
                CaptchaConstants.CAPTCHA_HEIGHT,
                CaptchaConstants.CAPTCHA_LENGTH,
                10
        );

        // 2. 随机生成 captchaId
        String captchaId = RandomStringUtils.randomAlphanumeric(16);

        // 3. 获取验证码文本和图片
        String captchaCode = lineCaptcha.getCode();
        String imageBase64 = lineCaptcha.getImageBase64();

        // 4. 存储到 Redis
        boolean success = redisService.setValue(
                CaptchaConstants.CAPTCHA_PREFIX + captchaId,
                captchaCode.toLowerCase(),
                CaptchaConstants.CAPTCHA_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        if (!success) {
            log.error("验证码写入 Redis 失败: id={}, code={}", captchaId, captchaCode);
            throw new RuntimeException("验证码生成失败");
        }

        // 5. 构造返回对象
        CaptchaDTO dto = new CaptchaDTO();
        dto.setCaptchaId(captchaId);
        dto.setCaptchaImage(imageBase64);
        dto.setExpireTime(System.currentTimeMillis() + CaptchaConstants.CAPTCHA_EXPIRE_MINUTES * 60 * 1000);
        dto.setCaptchaEnabled(true);

        log.info("生成验证码成功: captchaId={}", captchaId);
        return dto;
    }

    /**
     * 校验验证码：
     * 1. 从 Redis 获取验证码
     * 2. 比较用户输入和存储值
     * 3. 校验通过后立即删除验证码（防重放攻击）
     */
    @Override
    public boolean validateCaptcha(String captchaId, String userInput) {

        // 1. 防御性校验
        if (StringUtils.isBlank(captchaId) || StringUtils.isBlank(userInput)) {
            return false;
        }

        String key = CaptchaConstants.CAPTCHA_PREFIX + captchaId;

        // 2. 从 Redis 获取验证码
        String storedCode = redisService.getValue(key);
        if (StringUtils.isBlank(storedCode)) {
            log.warn("验证码不存在或已过期: id={}", captchaId);
            return false;
        }

        // 3. 比较验证码
        boolean valid = storedCode.trim().equalsIgnoreCase(userInput.trim());
        if (valid) {
            // 验证成功后立即删除（防重放）
            redisService.deleteKey(key);
            log.info("验证码校验通过: id={}", captchaId);
        } else {
            log.warn("验证码校验失败: id={}, input={}, stored={}", captchaId, userInput, storedCode);
        }

        return valid;
    }

    /**
     * 删除验证码
     *
     * <p>
     * 用于主动失效验证码（如刷新、异常处理）
     * Redis 层已统一封装，不关心具体删除数量
     * </p>
     *
     * @param captchaId 验证码 ID
     * @return true = 删除成功，false = 删除失败或 key 不存在
     */
    @Override
    public boolean deleteCaptchaReturnBoolean(String captchaId) {

        if (StringUtils.isBlank(captchaId)) {
            return false;
        }

        String key = CaptchaConstants.CAPTCHA_PREFIX + captchaId;

        boolean success = redisService.deleteKey(key);

        log.info("删除验证码: id={}, success={}", captchaId, success);
        return success;
    }

}
