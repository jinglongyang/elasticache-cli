package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @author: jinglongyang
 */
public class StringTranscoder implements Transcoder<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringTranscoder.class);
    private static final int JSON_SERIALIZED = 2;

    @Override
    public boolean asyncDecode(CachedData d) {
        return false;
    }

    @Override
    public CachedData encode(String o) {
        if (o == null)
            throw new NullPointerException("cache value is null.");
        return new CachedData(JSON_SERIALIZED, o.getBytes(Charset.forName("UTF-8")), getMaxSize());
    }

    @Override
    public String decode(CachedData data) {
        if ((data.getFlags() & JSON_SERIALIZED) == 0) {
            LOGGER.warn("Cannot decode cached data {} using json transcoder", data);
            throw new RuntimeException("Cannot decode cached data using json transcoder");
        }
        return new String(data.getData(), Charset.forName("UTF-8"));
    }

    @Override
    public int getMaxSize() {
        return 2 * CachedData.MAX_SIZE;
    }
}
