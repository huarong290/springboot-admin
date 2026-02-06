package com.springboot.admin.enums;
/**
 * 锁失败原因枚举
 * */
public enum LockFailReasonEnum {

    LOCKED_BY_OTHER,
    TIMEOUT,
    ERROR,
    NOT_FOUND,
    VALUE_MISMATCH
}
