package com.springboot.admin.service.redis;



import java.util.Optional;
import java.util.Set;

/**
 * Redis Set 服务接口
 *
 * <p>
 * 负责 Set 类型操作，常用于去重、黑名单、集合缓存等场景。
 * </p>
 *
 * <p>
 * 设计目标：
 * <ul>
 *   <li>统一返回数量，便于监控</li>
 *   <li>Optional 返回值避免 null</li>
 * </ul>
 * </p>
 */
public interface IRedisSetService {

    long sAdd(String key, Object... values);

    long sRemove(String key, Object... values);

    boolean sIsMember(String key, Object value);

    Set<Object> sMembers(String key);

    long sSize(String key);

    Optional<Object> sPop(String key);
}

