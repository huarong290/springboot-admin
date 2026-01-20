package com.springboot.admin.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum DataScopeTypeEnum {

    /** 全部数据 */
    ALL(1, "ALL", "全部数据"),

    /** 本组织 */
    ORG(2, "ORG", "本组织"),

    /** 本组织及下级 */
    ORG_AND_CHILD(3, "ORG_AND_CHILD", "本组织及下级"),

    /** 仅本部门 */
    DEPT(4, "DEPT", "仅本部门"),

    /** 自定义组织/部门范围 */
    CUSTOM(5, "CUSTOM", "自定义范围");

    /** 数据库存储值 */
    private final long value;

    /** 英文唯一编码，前端可直接使用 */
    private final String code;

    /** 中文描述 */
    private final String description;

    DataScopeTypeEnum(long value, String code, String description) {
        this.value = value;
        this.code = code;
        this.description = description;
    }

    /** 根据 value 获取枚举 */
    public static DataScopeTypeEnum ofValue(long value) {
        for (DataScopeTypeEnum type : values()) {
            if (type.value == value) return type;
        }
        log.info("未知数据权限范围类型 value: " + value);
        return DEPT; // 默认最小权限
    }

    /** 根据 code 获取枚举 */
    public static DataScopeTypeEnum ofCode(String code) {
        for (DataScopeTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(code)) return type;
        }
        log.info("未知数据权限范围类型 code: " + code);
        return DEPT; // 默认最小权限
    }

    public static DataScopeTypeEnum of(Object key) {
        if (key instanceof Integer) {
            return ofValue(((Integer) key).longValue());
        } else if (key instanceof Long) {
            return ofValue((Long) key);
        } else if (key instanceof String) {
            return ofCode((String) key);
        }
        // 未知类型，返回最小权限 DEPT
        log.warn("未知数据权限范围类型 key: {}，使用默认最小权限 DEPT", key);
        return DEPT;
    }


    @Override
    public String toString() {
        return String.format("%s(%d) - %s", code, value, description);
    }

}
