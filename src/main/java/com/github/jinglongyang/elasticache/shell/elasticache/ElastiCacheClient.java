package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.config.NodeEndPoint;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.transcoders.SerializingTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author: jinglongyang
 */
public class ElastiCacheClient {
    private static final Logger logger = LoggerFactory.getLogger(ElastiCacheClient.class);
    private static final StringTranscoder stringTranscoder = new StringTranscoder();
    private SerializingTranscoder serializingTranscoder = new SerializingTranscoder();
    private static final int MAX_EXPIRE_TIME = 2592000;

    private MemcachedClient memcachedClient;
    private long operationTimeout;

    public ElastiCacheClient(MemcachedClient memcachedClient, long operationTimeout) {
        this.memcachedClient = memcachedClient;
        this.operationTimeout = operationTimeout;
    }

    public String getStringValue(String key) {
        try {
            return memcachedClient.get(key, stringTranscoder);
        } catch (Exception e) {
            logger.warn(String.format("Fail to get data by key [%s] from ElastiCache.", key), e);
        }
        return null;
    }

    public <T> T get(String key, Class<T> clazz) {
        try {
            return memcachedClient.get(key, new JacksonTranscoder<>(clazz));
        } catch (Exception e) {
            logger.warn(String.format("Fail to get data by key %s from Elasticache.", key), e);
        }
        return null;
    }

    public Object get(String key) {
        try {
            return memcachedClient.get(key);
        } catch (Exception e) {
            logger.warn(String.format("Fail to get data by key [%s] from ElastiCache.", key), e);
        }
        return null;
    }

//    public Long getLongValue(String key) {
//        try {
//            return memcachedClient.get(key, longTranscoder);
//        } catch (Exception e) {
//            logger.warn(String.format("Fail to get data by key [%s] from ElastiCache.", key), e);
//        }
//        return null;
//    }

//    public boolean setStringValue(String key, String value, int expireTime) {
//        return getResult(memcachedClient.set(key, getExpireTime(expireTime), value, stringTranscoder), String.format("Fail to save data [%s] to ElastiCache.", value));
//    }

    public boolean setValue(String key, Object value, int expireTime) {
        return getResult(memcachedClient.set(key, getExpireTime(expireTime), value, serializingTranscoder), String.format("Fail to save data [%s] to ElastiCache.", value));
    }

//    public boolean setLongValue(String key, long value, int expireTime) {
//        return getResult(memcachedClient.set(key, getExpireTime(expireTime), value, longTranscoder), String.format("Fail to save data [%s] to ElastiCache.", value));
//    }

    public long incr(String key, int by) {
        try {
            return memcachedClient.incr(key, by);
        } catch (Exception e) {
            logger.warn(String.format("Fail to increase value by key [%s] from ElastiCache.", key), e);
        }
        return 0;
    }

    private boolean getResult(OperationFuture<Boolean> future, String message) {
        Boolean result = null;
        try {
            result = future.get(operationTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            cancel(future);
            logger.warn(message, e);
        } catch (TimeoutException e) {
            logger.warn(message, e);
        }
        return result == null ? false : result.booleanValue();
    }

    private int getExpireTime(int expireTime) {
        if (expireTime > MAX_EXPIRE_TIME) {
            return ((int) (System.currentTimeMillis() / 1000)) + expireTime;
        }
        return expireTime;
    }

    private void cancel(final Future<?> f) {
        if (f != null) {
            f.cancel(true);
        }
    }

    public boolean delete(String key) {
        return getResult(memcachedClient.delete(key), String.format("Fail to delete data by key [%s] from ElastiCache.", key));
    }

    public String getServerNodes() {
        Collection<NodeEndPoint> endPoints = memcachedClient.getAllNodeEndPoints();
        StringBuilder sb = new StringBuilder();
        for (NodeEndPoint endPoint : endPoints) {
            sb.append(endPoint.getHostName()).append(":").append(endPoint.getPort()).append("\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public void shutdown() {
        memcachedClient.shutdown();
    }
}