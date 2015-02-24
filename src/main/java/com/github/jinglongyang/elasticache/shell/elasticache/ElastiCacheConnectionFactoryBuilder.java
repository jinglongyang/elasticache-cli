package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.*;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.ops.Operation;
import net.spy.memcached.transcoders.Transcoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author: jinglongyang
 */
public class ElastiCacheConnectionFactoryBuilder extends ConnectionFactoryBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElastiCacheConnectionFactoryBuilder.class);

    public ConnectionFactory build() {
        return new DefaultConnectionFactory() {

            @Override
            public ClientMode getClientMode() {
                return clientMode == null ? super.getClientMode() : clientMode;
            }

            @Override
            public BlockingQueue<Operation> createOperationQueue() {
                return opQueueFactory == null ? super.createOperationQueue() : opQueueFactory.create();
            }

            @Override
            public BlockingQueue<Operation> createReadOperationQueue() {
                return readQueueFactory == null ? super.createReadOperationQueue() : readQueueFactory.create();
            }

            @Override
            public BlockingQueue<Operation> createWriteOperationQueue() {
                return writeQueueFactory == null ? super.createReadOperationQueue() : writeQueueFactory.create();
            }

            @Override
            public NodeLocator createLocator(List<MemcachedNode> nodes) {
                switch (locator) {
                    case ARRAY_MOD:
                        return new ArrayModNodeLocator(nodes, getHashAlg());
                    case CONSISTENT: {
                        LibKetamaNodeLocatorConfiguration ketamaNodeLocatorConfiguration = null;
                        HashAlgorithm hashAlgorithm = getHashAlg();
                        String hashAlgorithmName = hashAlgorithm == null ? null : hashAlgorithm.getClass().getSimpleName();
                        if (hashAlgorithm instanceof LibKetamaHash) {
                            LibKetamaHash libKetamaHash = (LibKetamaHash) getHashAlg();
                            LibKetamaNodeLocatorMethod libKetamaNodeLocatorMethod = libKetamaHash.getLibKetamaNodeLocatorMethod();
                            if (LibKetamaNodeLocatorMethod.hostname == libKetamaNodeLocatorMethod) {
                                ketamaNodeLocatorConfiguration = new LibKetamaHostNameNodeLocatorConfiguration();
                            }
                        }
                        if (ketamaNodeLocatorConfiguration == null) {
                            ketamaNodeLocatorConfiguration = new LibKetamaIpAddressNodeLocatorConfiguration();
                            LOGGER.info("Elasticache is using ip node locator for {}", hashAlgorithmName);
                        } else {
                            LOGGER.info("Elasticache is using hostname node locator for {}", hashAlgorithmName);
                        }
                        return new LibKetamaNodeLocator(nodes, getHashAlg(), ketamaNodeLocatorConfiguration);
                    }
                    default:
                        throw new IllegalStateException("Unhandled locator type: " + locator);
                }
            }

            @Override
            public Transcoder<Object> getDefaultTranscoder() {
                return transcoder == null ? super.getDefaultTranscoder() : transcoder;
            }

            @Override
            public FailureMode getFailureMode() {
                return failureMode == null ? super.getFailureMode() : failureMode;
            }

            @Override
            public HashAlgorithm getHashAlg() {
                return hashAlg == null ? super.getHashAlg() : hashAlg;
            }

            public Collection<ConnectionObserver> getInitialObservers() {
                return initialObservers;
            }

            @Override
            public OperationFactory getOperationFactory() {
                return opFact == null ? super.getOperationFactory() : opFact;
            }

            @Override
            public long getOperationTimeout() {
                return opTimeout == -1 ? super.getOperationTimeout() : opTimeout;
            }

            @Override
            public int getReadBufSize() {
                return readBufSize == -1 ? super.getReadBufSize() : readBufSize;
            }

            @Override
            public boolean isDaemon() {
                return isDaemon;
            }

            @Override
            public boolean shouldOptimize() {
                return shouldOptimize;
            }

            @Override
            public boolean useNagleAlgorithm() {
                return useNagle;
            }

            @Override
            public long getMaxReconnectDelay() {
                return maxReconnectDelay;
            }

            @Override
            public AuthDescriptor getAuthDescriptor() {
                return authDescriptor;
            }

            @Override
            public long getOpQueueMaxBlockTime() {
                return opQueueMaxBlockTime > -1 ? opQueueMaxBlockTime : super.getOpQueueMaxBlockTime();
            }

            @Override
            public int getTimeoutExceptionThreshold() {
                return timeoutExceptionThreshold;
            }
        };
    }
}
