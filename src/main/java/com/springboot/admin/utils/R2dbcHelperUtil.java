package com.springboot.admin.utils;

import com.springboot.admin.model.vo.PageResult;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.BiFunction;

/**
 * R2DBC 通用工具类
 *
 * 功能：
 * 1. 动态 INSERT（单条/批量）
 * 2. 动态 UPDATE（单条/批量）
 * 3. 分页查询
 *
 * 特点：
 * - 只插入/更新非空字段
 * - 使用命名参数绑定，避免索引混乱
 * - 分页查询封装，避免重复写 count + data SQL
 */
public class R2dbcHelperUtil {

    // ==========================
    // 插入相关
    // ==========================

    /**
     * 构建 INSERT SQL
     * 示例：INSERT INTO sys_user (username, email) VALUES (:username, :email)
     */
    public static String buildInsertSql(String tableName, Map<String, Object> fieldMap) {
        String columns = String.join(", ", fieldMap.keySet());
        String values = fieldMap.keySet().stream()
                .map(k -> ":" + k)
                .collect(Collectors.joining(", "));
        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
    }

    /**
     * 插入单条记录并返回主键 ID (Long)
     */
    public static Mono<Long> insertAndReturnId(DatabaseClient client, String tableName, Map<String, Object> fieldMap) {
        String sql = buildInsertSql(tableName, fieldMap);

        DatabaseClient.GenericExecuteSpec spec = client.sql(sql);
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
        }

        return spec.filter(statement -> statement.returnGeneratedValues("id"))
                .fetch()
                .first()
                .map(row -> (Long) row.get("id"));
    }

    /**
     * 批量插入（返回总影响行数）
     */
    public static Mono<Long> batchInsert(DatabaseClient client, String tableName, List<Map<String, Object>> fieldMaps) {
        if (fieldMaps.isEmpty()) {
            return Mono.just(0L);
        }

        String sql = buildInsertSql(tableName, fieldMaps.get(0));

        return Flux.fromIterable(fieldMaps)
                .flatMap(fieldMap -> {
                    DatabaseClient.GenericExecuteSpec spec = client.sql(sql);
                    for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
                        spec = spec.bind(entry.getKey(), entry.getValue());
                    }
                    return spec.fetch().rowsUpdated();
                })
                .reduce(Long::sum);
    }

    // ==========================
    // 更新相关
    // ==========================

    /**
     * 构建 UPDATE SQL
     * 示例：UPDATE sys_user SET username = :username WHERE id = :id
     */
    public static String buildUpdateSql(String tableName, Map<String, Object> fieldMap, String idColumn) {
        String setClause = fieldMap.keySet().stream()
                .map(k -> k + " = :" + k)
                .collect(Collectors.joining(", "));
        return "UPDATE " + tableName + " SET " + setClause + " WHERE " + idColumn + " = :" + idColumn;
    }

    /**
     * 更新单条记录
     */
    public static Mono<Long> update(DatabaseClient client, String tableName, Map<String, Object> fieldMap, String idColumn, Object idValue) {
        fieldMap.put(idColumn, idValue);

        String sql = buildUpdateSql(tableName, fieldMap, idColumn);

        DatabaseClient.GenericExecuteSpec spec = client.sql(sql);
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
        }

        return spec.fetch().rowsUpdated();
    }

    /**
     * 批量更新（返回总影响行数）
     */
    public static Mono<Long> batchUpdate(DatabaseClient client, String tableName, List<Map<String, Object>> fieldMaps, String idColumn) {
        if (fieldMaps.isEmpty()) {
            return Mono.just(0L);
        }

        return Flux.fromIterable(fieldMaps)
                .flatMap(fieldMap -> {
                    Object idValue = fieldMap.get(idColumn);
                    if (idValue == null) {
                        return Mono.error(new IllegalArgumentException("缺少主键字段: " + idColumn));
                    }

                    String sql = buildUpdateSql(tableName, fieldMap, idColumn);

                    DatabaseClient.GenericExecuteSpec spec = client.sql(sql);
                    for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
                        spec = spec.bind(entry.getKey(), entry.getValue());
                    }

                    return spec.fetch().rowsUpdated();
                })
                .reduce(Long::sum);
    }

    // ==========================
    // 分页相关
    // ==========================

    /**
     * 执行分页查询
     *
     * @param client DatabaseClient
     * @param baseSql 基础 SQL（不包含 select 和 limit）
     * @param params 参数绑定 Map，例如 {"username": "%admin%"}
     * @param page 当前页码
     * @param size 每页大小
     * @param mapper 行映射函数 (row, meta) -> T
     * @param <T> 返回对象类型（通常是 VO 或 Entity）
     * @return Mono<PageResult<T>> 分页结果
     */
    public static <T> Mono<PageResult<T>> queryPage(
            DatabaseClient client,
            String baseSql,
            Map<String, Object> params,
            int page,
            int size,
            BiFunction<Row, RowMetadata, T> mapper
    ) {
        int offset = (page - 1) * size;

        // 数据查询 SQL
        String dataSql = "SELECT t.* " + baseSql + " LIMIT :size OFFSET :offset";

        DatabaseClient.GenericExecuteSpec dataSpec = client.sql(dataSql)
                .bind("size", size)
                .bind("offset", offset);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            dataSpec = dataSpec.bind(entry.getKey(), entry.getValue());
        }

        Flux<T> listFlux = dataSpec.map(mapper).all();

        // 总数查询 SQL
        String countSql = "SELECT COUNT(*) " + baseSql;

        DatabaseClient.GenericExecuteSpec countSpec = client.sql(countSql);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            countSpec = countSpec.bind(entry.getKey(), entry.getValue());
        }

        Mono<Long> countMono = countSpec.map(row -> row.get(0, Long.class)).one();

        // 合并结果
        return listFlux.collectList()
                .zipWith(countMono)
                .map(tuple -> new PageResult<>(tuple.getT1(), tuple.getT2(), page, size));
    }
}
