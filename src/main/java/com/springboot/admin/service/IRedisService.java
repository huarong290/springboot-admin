package com.springboot.admin.service;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 全能服务接口
 *
 * <p>
 * 封装 Redis 五大核心数据结构及常用命令。
 * 屏蔽底层异常，提供 Fail-Secure (故障安全) 返回值，确保缓存挂掉不影响主业务。
 * </p>
 *
 * <h3>📚 数据结构与场景指南：</h3>
 * <ul>
 * <li><b>String (字符串):</b> 缓存对象(JSON)、计数器、分布式锁、验证码。</li>
 * <li><b>Hash (哈希表):</b> 存储对象(如用户信息)，可修改单个字段而不必序列化整个对象。</li>
 * <li><b>List (列表):</b> 简单消息队列、最新 N 条通知、时间轴数据。</li>
 * <li><b>Set (集合):</b> 点赞用户、标签(Tag)、抽奖名单 (无序、去重、支持交并差集)。</li>
 * <li><b>ZSet (有序集合):</b> 排行榜、热搜榜、延迟队列 (带 Score 权重)。</li>
 * </ul>
 *
 * @author YourName
 */
public interface IRedisService {

    // =================================================
    // 1. 通用 Key 管理 (Key Operations)
    // =================================================

    /**
     * 设置 Key 的过期时间
     * @param key Redis Key
     * @param timeout 时间长度
     * @param unit 时间单位 (秒/分/小时/天)
     * @return true=设置成功, false=Key不存在或失败
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 获取 Key 的剩余有效时间
     * @param key Redis Key
     * @param unit 返回的时间单位
     * @return 剩余时间 (-1:永不过期, -2:Key不存在)
     */
    long getExpire(String key, TimeUnit unit);

    /**
     * 判断 Key 是否存在
     */
    boolean hasKey(String key);

    /**
     * 删除单个 Key
     */
    boolean deleteKey(String key);

    /**
     * 批量删除 Key
     * @param keys Key 集合
     * @return true=删除成功(至少删除了一个)
     */
    boolean deleteKeys(Collection<String> keys);

    // =================================================
    // 2. String (字符串) 操作
    // =================================================

    /**
     * 普通缓存放入
     */
    boolean setValue(String key, String value);

    /**
     * 普通缓存放入并设置时间
     * @param timeout 过期时间
     */
    boolean setValue(String key, String value, long timeout, TimeUnit unit);

    /**
     * 普通缓存获取
     */
    String getValue(String key);

    /**
     * 批量获取 (MGET)
     * <p>🔥 高性能：一次网络请求获取多个值，大幅减少 RTT (往返时间)。</p>
     * @param keys Key 列表
     * @return 值列表 (顺序与 keys 一致，不存在的为 null)
     */
    List<String> multiGet(Collection<String> keys);

    /**
     * 递增 (Incr)
     * <p>场景：阅读数、点赞数、全局 ID 生成。</p>
     * @return 增加后的值
     */
    Long increment(String key);

    /**
     * 递增并设置首次过期时间 (Lua 原子操作)
     * <p>场景：限流 (如 1 分钟内限制访问 10 次)。</p>
     */
    Long increment(String key, long timeout, TimeUnit unit);

    /**
     * 获取并删除 (GetDel) - 原子操作
     * <p>场景：一次性 Token、验证码校验。</p>
     */
    String getAndDelete(String key);

    // =================================================
    // 3. Hash (哈希表) 操作
    // =================================================

    /**
     * HashSet: 向一张 Hash 表中放入数据
     * @param key Redis Key (表名)
     * @param hashKey 项 (字段名)
     * @param value 值
     */
    boolean hSet(String key, String hashKey, String value);

    /**
     * HashSet 并设置整个 Hash 的过期时间
     * <p>⚠️ 注意：Redis 不支持给 Hash 内部的单个字段设置过期时间。</p>
     */
    boolean hSet(String key, String hashKey, String value, long timeout, TimeUnit unit);

    /**
     * HMSet: 批量放入 Hash
     * @param map 多个键值对
     */
    boolean hMSet(String key, Map<String, String> map);
    boolean hMSet(String key, Map<String, String> map, long timeout, TimeUnit unit);

    /**
     * HashGet: 获取 Hash 中的数据
     */
    String hGet(String key, String hashKey);

    /**
     * HMGet: 获取 Hash 中所有键值对
     * <p>⚠️ 警告：如果 Hash 特别大(如几万个字段)，会导致 Redis 阻塞，请慎用。</p>
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * HDel: 删除 Hash 中的一个或多个字段
     */
    boolean hDel(String key, Object... hashKeys);

    /**
     * HExists: 判断 Hash 中是否有该项
     */
    boolean hHasKey(String key, String hashKey);

    /**
     * HIncr: Hash 递增
     * <p>场景：购物车商品数量 +1</p>
     */
    Long hIncr(String key, String hashKey, long delta);

    // =================================================
    // 4. List (列表) 操作
    // =================================================

    /**
     * LPush: 从列表左边推入 (头部)
     * <p>结构：Stack (栈, LPush + LPop) 或 Queue (队列, LPush + RPop)</p>
     */
    boolean lPush(String key, String value);

    /**
     * LPushAll: 批量左入
     */
    boolean lPushAll(String key, List<String> values);

    /**
     * RPush: 从列表右边推入 (尾部)
     */
    boolean rPush(String key, String value);
    boolean rPushAll(String key, List<String> values);

    /**
     * LPop: 从左边弹出 (头部出队)
     */
    String lPop(String key);

    /**
     * RPop: 从右边弹出 (尾部出队)
     */
    String rPop(String key);

    /**
     * LRange: 获取列表指定范围的元素
     * @param start 开始位置 (0)
     * @param end 结束位置 (-1 代表所有)
     */
    List<String> lRange(String key, long start, long end);

    /**
     * LLen: 获取列表长度
     */
    long lLen(String key);

    // =================================================
    // 5. Set (集合) 操作 (无序、去重)
    // =================================================

    /**
     * SAdd: 放入集合
     * @return 成功添加的个数
     */
    boolean sAdd(String key, String... values);

    /**
     * SAdd: 放入集合并设置过期时间
     */
    boolean sAddWithExpire(String key, long timeout, TimeUnit unit, String... values);

    /**
     * SIsMember: 判断是否存在
     * <p>🔥 高性能：O(1) 复杂度，适合黑名单校验。</p>
     */
    boolean sIsMember(String key, String value);

    /**
     * SSize: 获取集合大小
     */
    long sSize(String key);

    /**
     * SRemove: 移除元素
     */
    long sRemove(String key, Object... values);

    /**
     * SMembers: 获取集合所有元素
     */
    Set<String> sMembers(String key);

    // =================================================
    // 6. ZSet (有序集合) 操作 (带权重)
    // =================================================

    /**
     * ZAdd: 添加元素
     * @param score 分数 (排序权重，越小越靠前)
     */
    boolean zAdd(String key, String value, double score);

    /**
     * ZIncrBy: 增加元素的分数
     * <p>场景：给帖子点赞，热度 +1</p>
     */
    Double zIncrScore(String key, String value, double delta);

    /**
     * ZRank: 获取排名 (从小到大)
     * @return 排名，0 是第一名
     */
    Long zRank(String key, String value);

    /**
     * ZRevRank: 获取排名 (从大到小)
     * <p>场景：积分榜，分数高的排前面</p>
     */
    Long zReverseRank(String key, String value);

    /**
     * ZRevRange: 获取排名区间内的元素 (从大到小)
     * <p>场景：获取 Top 10</p>
     */
    Set<String> zReverseRange(String key, long start, long end);

    /**
     * ZRevRangeWithScores: 获取排名区间内的元素和分数 (Tuple)
     */
    Set<ZSetOperations.TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end);

    /**
     * ZScore: 获取指定元素的分数
     */
    Double zScore(String key, String value);

    /**
     * ZRem: 删除元素
     */
    long zRemove(String key, Object... values);

    // =================================================
    // 7. 高级与运维操作
    // =================================================

    /**
     * 扫描 Key (Non-blocking)
     * <p>替代 `KEYS` 命令，安全地进行模糊查询。</p>
     * @param pattern 匹配模式 (如 "user:Token:*")
     */
    Set<String> scan(String pattern);


    // ✅ 新增：对象操作接口定义 (对应 ServiceImpl 中的实现)
    boolean setObject(String key, Object value, long timeout, TimeUnit unit);
    <T> T getObject(String key, Class<T> clazz);
}