package com.springboot.admin.utils;

import com.springboot.admin.model.vo.PageResult;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.BiFunction;
/**
 * R2DBC 分页工具类
 *
 * 封装分页查询逻辑，避免每次都写两条 SQL。
 * 使用方式类似 MyBatis-Plus 的分页。
 */
public class R2dbcPageHelperUtil {


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
        String dataSql = "SELECT u.* " + baseSql + " LIMIT :size OFFSET :offset";

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
