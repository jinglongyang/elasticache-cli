package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by jinglongyang on 2/23/15.
 */
public class ElastiCacheClientBuilder {
    public static final String LIB_KETAMA_HASH = "LIB_KETAMA_HASH";

    private String address;
    private long operationTimeout;
    private HashAlgorithm hashAlgorithm;

    public ElastiCacheClientBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public ElastiCacheClientBuilder withOperationTimeout(long operationTimeout) {
        this.operationTimeout = operationTimeout;
        return this;
    }

    public ElastiCacheClientBuilder withHashAlgorithm(HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
        return this;
    }

    public MemcachedClient build() throws IOException {
        Properties systemProperties = System.getProperties();
        systemProperties.put("net.spy.log.LoggerImpl", "com.github.jinglongyang.elasticache.shell.elasticache.SLF4JLogger");
        System.setProperties(systemProperties);

        ConnectionFactoryBuilder builder = new ElastiCacheConnectionFactoryBuilder()
                .setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT)
                .setOpTimeout(operationTimeout)
                .setShouldOptimize(true)
                .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY);
        if (hashAlgorithm != null) {
            builder.setHashAlg(hashAlgorithm);
        }
        return new MemcachedClient(builder.build(), AddrUtil.getAddresses(address));
    }
}
