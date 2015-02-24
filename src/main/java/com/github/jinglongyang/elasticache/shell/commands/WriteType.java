package com.github.jinglongyang.elasticache.shell.commands;

import com.github.jinglongyang.elasticache.shell.util.EnumUtils;

/**
* Created by jinglongyang on 2/24/15.
*/
public enum WriteType {
    String("string") {
        @Override
        public Object parse(String value) {
            return value;
        }
    }, Long("long") {
        @Override
        public Object parse(String value) {
            return java.lang.Long.valueOf(value);
        }
    }, Integer("int") {
        @Override
        public Object parse(String value) {
            return java.lang.Integer.valueOf(value);
        }
    }, Float("float") {
        @Override
        public Object parse(String value) {
            return java.lang.Float.valueOf(value);
        }
    }, Double("double") {
        @Override
        public Object parse(String value) {
            return java.lang.Double.valueOf(value);
        }
    }, Boolean("boolean") {
        @Override
        public Object parse(String value) {
            return java.lang.Boolean.valueOf(value);
        }
    };

    private String value;

    private WriteType(String value) {
        this.value = value;
    }

    public static WriteType fromValue(String value) {
        if (value == null) {
            return WriteType.String;
        }
        return EnumUtils.getEnumFromString(WriteType.class, value);
    }

    @Override
    public String toString() {
        return value;
    }

    abstract public Object parse(String value);
}
