package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.CASValue;

/**
 * @author: jinglongyang
 */
public class CasValueWrapper<T> {
    private final CASValue<T> casValue;

    public CasValueWrapper(CASValue<T> casValue) {
        this.casValue = casValue;
    }

    /**
     * Get the CAS identifier.
     */
    public long getCas() {
        return casValue.getCas();
    }

    /**
     * Get the object value.
     */
    public T getValue() {
        return casValue.getValue();
    }

    public CASValue<T> getCasvalue() {
        return casValue;
    }
}
