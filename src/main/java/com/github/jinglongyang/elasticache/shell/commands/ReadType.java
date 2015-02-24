package com.github.jinglongyang.elasticache.shell.commands;

import com.github.jinglongyang.elasticache.shell.util.EnumUtils;

/**
* Created by jinglongyang on 2/24/15.
*/
public enum ReadType {
    String("string"), JSON("json"), Primitive("primitive");

    private String value;

    private ReadType(String value) {
        this.value = value;
    }

    public static ReadType fromValue(String value) {
        if (value == null) {
            return ReadType.String;
        }
        return EnumUtils.getEnumFromString(ReadType.class, value);
    }

    @Override
    public String toString() {
        return value;
    }
}
