package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.config.NodeEndPoint;

/**
 * @author: jinglongyang
 */
public class LibKetamaHostNameNodeLocatorConfiguration extends LibKetamaNodeLocatorConfiguration {

    @Override
    String getSocketAddressForNode(NodeEndPoint nodeEndPoint) {
        return String.format("%s:%d", nodeEndPoint.getHostName(), nodeEndPoint.getPort());
    }
}
