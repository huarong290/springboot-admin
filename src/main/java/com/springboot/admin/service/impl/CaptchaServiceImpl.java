package com.springboot.admin.service.impl;

import com.springboot.admin.common.BusinessResultCode;
import com.springboot.admin.common.CaptchaValidationResult;
import com.springboot.admin.config.properties.CaptchaProperties;
import com.springboot.admin.constants.captcha.CaptchaConstants;
import com.springboot.admin.enums.CaptchaType;
import com.springboot.admin.exception.BusinessException;
import com.springboot.admin.model.dto.CaptchaDTO;
import com.springboot.admin.model.dto.captcha.CaptchaStoreDTO;
import com.springboot.admin.service.ICaptchaService;
import com.springboot.admin.service.handler.CaptchaHandler;
import com.springboot.admin.service.redis.biz.IRedisRateLimitService;
import com.springboot.admin.service.redis.core.IRedisAtomicService;
import com.springboot.admin.service.redis.core.IRedisKVService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class CaptchaServiceImpl implements ICaptchaService {

    @Autowired
    private IRedisKVService kvService;
    @Autowired
    private IRedisAtomicService atomicService;
    @Autowired
    private IRedisRateLimitService rateLimitService;
    @Autowired
    private CaptchaProperties properties;
    @Autowired
    private List<CaptchaHandler> handlers;

    @Override
    public CaptchaDTO generate(CaptchaType type, String scene, String target) {
        // 1. 频率控制
        String limitKey = String.format(CaptchaConstants.CAPTCHA_LIMIT_KEY, scene, target);
        // 注意：此处需要确保 rateLimitService 内部使用的是 Duration
        boolean isAllowed = rateLimitService.allowRequestSlidingWindow(
                limitKey, target, 1, Duration.ofSeconds(properties.getRateLimitSeconds())).allowed;

        if (!isAllowed) {
            log.warn("验证码请求过于频繁: scene={}, target={}", scene, target);
            throw new BusinessException(BusinessResultCode.CAPTCHA_RATE_LIMITED);
        }

        // 2. 匹配处理器
        CaptchaHandler handler = handlers.stream()
                .filter(h -> h.type() == type)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("未找到对应的验证码处理器: type={}", type);
                    return new BusinessException(BusinessResultCode.PARAM_INVALID.getCode(), "不支持的验证码类型: " + type);
                });

        // 3. 生成原始验证码并加密存储
        String rawCode = cn.hutool.core.util.RandomUtil.randomNumbers(properties.getImage().getLength());
        String captchaId = cn.hutool.core.util.IdUtil.fastSimpleUUID();

        // HMAC 加密存储
        String hashedCode = cn.hutool.crypto.SecureUtil.hmacMd5(properties.getHmacSecret()).digestHex(rawCode);
        CaptchaStoreDTO store = new CaptchaStoreDTO(hashedCode, scene, target);

        // 存储到 Redis (解决 long 转 Duration 问题)
        String storeKey = String.format(CaptchaConstants.CAPTCHA_STORE_KEY, captchaId);
        kvService.set(storeKey, store, Duration.ofSeconds(properties.getTtlSeconds()));

        // 4. 执行渲染逻辑
        handler.executeSend(target, rawCode, scene);
        String content = handler.getCaptchaContent(rawCode);

        // 5. 组装返回
        CaptchaDTO dto = new CaptchaDTO();
        dto.setCaptchaId(captchaId);
        dto.setCaptchaContent(content);
        dto.setExpireTime(System.currentTimeMillis() + (properties.getTtlSeconds() * 1000));
        dto.setCaptchaType(type.name());
        dto.setCaptchaScene(scene);
        log.info("生成验证码成功: id={}, scene={}, target={}", captchaId, scene, target);
        return dto;
    }

    @Override
    public CaptchaValidationResult validate(String captchaId, String code, String scene, String target) {
        String storeKey = String.format(CaptchaConstants.CAPTCHA_STORE_KEY, captchaId);
        String attemptKey = String.format(CaptchaConstants.CAPTCHA_ATTEMPT_KEY, captchaId);

        // 1. 获取存储对象 (Optional 处理)
        CaptchaStoreDTO store = kvService.get(storeKey, CaptchaStoreDTO.class).orElse(null);
        if (store == null) {
            return CaptchaValidationResult.NOT_FOUND;
        }

        // 2. 尝试次数检查 (利用 Atomic 服务的 INCR + EXPIRE 原子操作)
        // 第一次调用时会设置 TTL，后续调用只加不重置 TTL
        long attempts = atomicService.increment(attemptKey, Duration.ofSeconds(properties.getTtlSeconds()));
        if (attempts > properties.getMaxAttempts()) {
            kvService.delete(storeKey);   // 销毁验证码
            kvService.delete(attemptKey); // 清理计数
            return CaptchaValidationResult.TOO_MANY_ATTEMPTS;
        }

        // 3. 严格匹配：场景(Scene) + 目标(Target)
        if (!store.getScene().equals(scene) || !store.getTarget().equals(target)) {
            return CaptchaValidationResult.INVALID_CONTEXT;
        }

        // 4. 内容匹配：对比密文 (HMAC)
        String inputHash = cn.hutool.crypto.SecureUtil.hmacMd5(properties.getHmacSecret()).digestHex(code);
        if (!store.getCodeHash().equalsIgnoreCase(inputHash)) {
            return CaptchaValidationResult.MISMATCH;
        }

        // 5. 校验通过，清理现场 (即用即毁)
        kvService.delete(storeKey);
        kvService.delete(attemptKey);

        return CaptchaValidationResult.SUCCESS;
    }

    @Override
    public boolean invalidate(String captchaId) {
        String storeKey = String.format(CaptchaConstants.CAPTCHA_STORE_KEY, captchaId);
        String attemptKey = String.format(CaptchaConstants.CAPTCHA_ATTEMPT_KEY, captchaId);
        // 即使 attemptKey 不存在也不影响
        kvService.delete(attemptKey);
        return kvService.delete(storeKey);
    }

    @Override
    public boolean exists(String captchaId) {
        return kvService.exists(String.format(CaptchaConstants.CAPTCHA_STORE_KEY, captchaId));
    }
}