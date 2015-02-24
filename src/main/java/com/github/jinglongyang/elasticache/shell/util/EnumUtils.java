package com.github.jinglongyang.elasticache.shell.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by jinglongyang on 2/24/15.
 */
public class EnumUtils {
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String value) {
        String trimValue = StringUtils.trimToNull(value);
        if (trimValue == null) {
            return null;
        }
        T[] values = c.getEnumConstants();
        for (T temp : values) {
            if (trimValue.toLowerCase().equals(temp.toString().toLowerCase())) {
                return temp;
            }
        }
        return null;
    }
}
