package com.springboot.admin.model.dto.redis;

import com.springboot.admin.enums.LockFailReasonEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分布式锁操作结果 DTO
 */
@Data
@AllArgsConstructor
public class LockResultDTO {
    /**
     * 是否成功
     */
    private final boolean success;
    /**
     * 当前锁持有者（UUID）
     */
    private final String owner;
    /**
     * 失败原因
     */
    private final LockFailReasonEnum reason;
    /**
     * 剩余 TTL（毫秒），null 表示无过期或锁不存在
     */
    private final Long ttlRemainingMillis;



    // 静态工厂方法
    public static LockResultDTO success(String owner, Long ttlRemainingMillis) {
        return new LockResultDTO(true, owner, null, ttlRemainingMillis);
    }

    public static LockResultDTO fail(LockFailReasonEnum reason) {
        return new LockResultDTO(false, null, reason, null);
    }


}
