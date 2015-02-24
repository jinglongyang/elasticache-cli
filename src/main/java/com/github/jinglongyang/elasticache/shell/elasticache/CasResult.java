package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.CASResponse;

/**
 * @author: jinglongyang
 */
public enum CasResult {
    OK, NOT_FOUND, EXISTS, FAILED;


    public static CasResult from(CASResponse response) {
        if (CASResponse.OK == response)
            return OK;
        if (CASResponse.NOT_FOUND == response)
            return NOT_FOUND;
        return EXISTS;
    }
}
