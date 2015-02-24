package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.MemcachedNode;
import net.spy.memcached.config.NodeEndPoint;
import net.spy.memcached.util.DefaultKetamaNodeLocatorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: jinglongyang
 */
abstract public class LibKetamaNodeLocatorConfiguration extends DefaultKetamaNodeLocatorConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibKetamaNodeLocatorConfiguration.class);

    protected String getSocketAddressForNode(MemcachedNode node) {
        String result = socketAddresses.get(node);
        if (result != null) return result;
        result = getSocketAddressForNode(node.getNodeEndPoint());
        LOGGER.debug("The Elasticache socket address is [{}]", result);
        socketAddresses.put(node, result);
        return result;
    }

    abstract String getSocketAddressForNode(NodeEndPoint nodeEndPoint);

    public String getKeyForNode(MemcachedNode node, int repetition) {
        return String.format("%s-%d", getSocketAddressForNode(node), repetition);
    }
}
