package com.github.jinglongyang.elasticache.shell.elasticache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author: jinglongyang
 */
public class JacksonTranscoder<T> implements Transcoder<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonTranscoder.class);
    private static final int JSON_SERIALIZED = 2;
    private final ObjectMapper mapper;
    private final Class<T> clazz;

    public JacksonTranscoder(Class<T> clazz) {
        this.mapper = new ObjectMapper();
        this.clazz = clazz;
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public boolean asyncDecode(CachedData d) {
        return false;
    }

    @Override
    public CachedData encode(T o) {
        if (o == null)
            return null;
        try {
            return new CachedData(JSON_SERIALIZED, mapper.writeValueAsBytes(o), getMaxSize());
        } catch (IOException e) {
            LOGGER.warn(String.format("Error serializing object %s", o.toString()), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public T decode(final CachedData data) {
        if ((data.getFlags() & JSON_SERIALIZED) == 0) {
            LOGGER.warn("Cannot decode cached data {} using json transcoder", data);
            throw new RuntimeException("Cannot decode cached data using json transcoder");
        }
        try {
            return mapper.readValue(data.getData(), clazz);
        } catch (IOException e) {
            LOGGER.warn(String.format("Error deserializing cached data %s", data.toString()), e);
            return null;
        }
    }

    @Override
    public int getMaxSize() {
        return 2 * CachedData.MAX_SIZE;
    }
}
