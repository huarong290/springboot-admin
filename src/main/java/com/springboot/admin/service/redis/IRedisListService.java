package com.springboot.admin.service.redis;


import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Redis List 服务接口
 *
 * <p>负责队列操作：</p>
 * <ul>
 *   <li>lPush / rPush：插入元素</li>
 *   <li>lPop / rPop：弹出元素</li>
 *   <li>lRange：区间查询</li>
 *   <li>lLen：获取长度</li>
 *   <li>lTrim：裁剪列表</li>
 *   <li>lIndex：获取指定索引元素</li>
 *   <li>blPop / brPop：阻塞弹出</li>
 * </ul>
 */
public interface IRedisListService {

    long lPush(String key, Object value);

    long rPush(String key, Object value);

    Optional<Object> lPop(String key);

    Optional<Object> rPop(String key);

    List<Object> lRange(String key, long start, long end);

    long lLen(String key);

    Optional<Object> lIndex(String key, long index);

    void lTrim(String key, long start, long end);

    Optional<Object> blPop(String key, Duration timeout);

    Optional<Object> brPop(String key, Duration timeout);
}
