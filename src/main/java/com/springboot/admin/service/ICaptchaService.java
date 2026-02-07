package com.springboot.admin.service;

import com.springboot.admin.common.CaptchaValidationResult;
import com.springboot.admin.enums.CaptchaType;
import com.springboot.admin.model.dto.CaptchaDTO;

/**
 * 验证码业务通用接口
 * * <p>功能范围：支持图片、短信、邮件验证码的统一生成、下发、校验与状态管理。</p>
 * <p>安全策略：采用 <b>Scene(场景) + Target(目标)</b> 双重绑定机制，防止验证码跨通道或跨用户滥用。</p>
 *
 * @author YourName
 * @since 2024-05-20
 */
public interface ICaptchaService {

    /**
     * 生成并发送验证码
     * * <p>逻辑说明：
     * 1. 根据 type 路由到具体实现（如图片生成或短信下发）。
     * 2. 内部应包含频率控制（Rate Limiting），如同一 target 在 60 秒内不可重复获取。
     * 3. 验证码内容需加密或哈希后存入缓存（建议 Redis），Key 由 captchaId 构成。</p>
     *
     * @param type   验证码类型 {@link CaptchaType} (IMAGE-图片, SMS-短信, EMAIL-邮件)
     * @param scene  业务场景标识 (如: "LOGIN", "FORGET_PWD", "BIND_PHONE")，用于逻辑隔离
     * @param target 发送目标 (图片验证码可传空，短信传手机号，邮件传邮箱地址)
     * @return {@link CaptchaDTO} 包含验证码唯一标识、Base64内容(图片类)、过期时间等
     * @throws BusinessException 如果发送频率过快或目标格式不正确时抛出
     */
    CaptchaDTO generate(CaptchaType type, String scene, String target);

    /**
     * 校验验证码
     * * <p>安全规则：
     * 1. <b>严格匹配</b>：校验时会比对存储中该 ID 对应的 scene 和 target，任何一项不匹配均视为非法。
     * 2. <b>即用即毁</b>：校验成功后，必须立即物理删除缓存中的验证码。
     * 3. <b>尝试限次</b>：如果单次校验失败，建议增加计数，达到错误阈值（如5次）后强制失效。</p>
     *
     * @param captchaId 验证码唯一标识（由生成接口返回）
     * @param code      用户输入的明文验证码
     * @param scene     当前业务场景（必须与申请时一致）
     * @param target    当前操作的目标对象（必须与申请时绑定对象一致）
     * @return {@link CaptchaValidationResult} 校验结果枚举（SUCCESS, INVALID, EXPIRED, MISMATCH 等）
     */
    CaptchaValidationResult validate(String captchaId, String code, String scene, String target);

    /**
     * 主动使验证码失效
     * * <p>典型场景：
     * 1. 发现异常风控行为，强制废弃当前验证码。
     * 2. 后台管理员手动清理。</p>
     *
     * @param captchaId 验证码唯一标识
     * @return 是否失效成功
     */
    boolean invalidate(String captchaId);

    /**
     * 检查验证码是否存在且未过期
     * * <p>注意：此方法仅用于 UI 状态预判断或内部前置检查，<b>不应</b>触发删除逻辑或状态变更。</p>
     *
     * @param captchaId 验证码唯一标识
     * @return true 代表验证码尚在有效期内且可进行校验
     */
    boolean exists(String captchaId);
}