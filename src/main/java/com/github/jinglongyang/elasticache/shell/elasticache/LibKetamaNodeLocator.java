package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.NodeLocator;
import net.spy.memcached.compat.SpyObject;
import net.spy.memcached.util.DefaultKetamaNodeLocatorConfiguration;
import net.spy.memcached.util.KetamaNodeLocatorConfiguration;

import java.util.*;

/**
 * @author: jinglongyang
 */
public class LibKetamaNodeLocator extends SpyObject implements NodeLocator {
    private volatile TreeMap<Long, MemcachedNode> ketamaNodes;
    private Collection<MemcachedNode> allNodes;

    private final HashAlgorithm hashAlg;
    private final KetamaNodeLocatorConfiguration config;

    /**
     * Create a new KetamaNodeLocator using specified nodes and the specified hash
     * algorithm.
     *
     * @param nodes The List of nodes to use in the Ketama consistent hash
     *              continuum
     * @param alg   The hash algorithm to use when choosing a node in the Ketama
     *              consistent hash continuum
     */
    public LibKetamaNodeLocator(List<MemcachedNode> nodes, HashAlgorithm alg) {
        this(nodes, alg, new DefaultKetamaNodeLocatorConfiguration());
    }

    /**
     * Create a new KetamaNodeLocator using specified nodes and the specified hash
     * algorithm and configuration.
     *
     * @param nodes The List of nodes to use in the Ketama consistent hash
     *              continuum
     * @param alg   The hash algorithm to use when choosing a node in the Ketama
     *              consistent hash continuum
     * @param conf
     */
    public LibKetamaNodeLocator(List<MemcachedNode> nodes, HashAlgorithm alg, KetamaNodeLocatorConfiguration conf) {
        allNodes = nodes;
        hashAlg = alg;
        config = conf;
        setKetamaNodes(nodes);
    }

    private LibKetamaNodeLocator(TreeMap<Long, MemcachedNode> smn, Collection<MemcachedNode> an, HashAlgorithm alg, KetamaNodeLocatorConfiguration conf) {
        ketamaNodes = smn;
        allNodes = an;
        hashAlg = alg;
        config = conf;
    }

    public Collection<MemcachedNode> getAll() {
        return allNodes;
    }

    public MemcachedNode getPrimary(final String k) {
        MemcachedNode rv = getNodeForKey(hashAlg.hash(k));
        assert rv != null : "Found no node for key " + k;
        return rv;
    }

    public MemcachedNode getNodeForKey(long hash) {
        if (!ketamaNodes.containsKey(hash)) {
            Map.Entry<Long, MemcachedNode> entry = getKetamaNodes().ceilingEntry(hash);
            if (entry != null) return entry.getValue();

            return getKetamaNodes().firstEntry().getValue();
        }
        return getKetamaNodes().get(hash);
    }

    public Iterator<MemcachedNode> getSequence(String k) {
        // Seven searches gives us a 1 in 2^7 chance of hitting the
        // same dead node all of the time.
        return new LibKetamaIterator(k, 7, getKetamaNodes(), hashAlg);
    }

    public NodeLocator getReadonlyCopy() {
        TreeMap<Long, MemcachedNode> smn = new TreeMap<>(getKetamaNodes());
        Collection<MemcachedNode> an = new ArrayList<>(allNodes.size());

        // Rewrite the values a copy of the map.
        for (Map.Entry<Long, MemcachedNode> me : smn.entrySet()) {
            me.setValue(new MemcachedNodeImpl(me.getValue()));
        }

        // Copy the allNodes collection.
        for (MemcachedNode n : allNodes) {
            an.add(new MemcachedNodeImpl(n));
        }

        return new LibKetamaNodeLocator(smn, an, hashAlg, config);
    }

    @Override
    public void updateLocator(List<MemcachedNode> nodes) {
        setKetamaNodes(nodes);
        allNodes = nodes;
    }

    /**
     * @return the ketamaNodes
     */
    protected TreeMap<Long, MemcachedNode> getKetamaNodes() {
        return ketamaNodes;
    }

    /**
     * Setup the KetamaNodeLocator with the list of nodes it should use.
     *
     * @param nodes a List of MemcachedNodes for this KetamaNodeLocator to use in
     *              its continuum
     */
    protected void setKetamaNodes(List<MemcachedNode> nodes) {
        TreeMap<Long, MemcachedNode> newNodeMap = new TreeMap<>();
        int numReps = config.getNodeRepetitions();
        for (MemcachedNode node : nodes) {
            // Ketama does some special work with md5 where it reuses chunks.
            if (hashAlg instanceof LibKetamaHash) {
                for (int i = 0; i < numReps / 4; i++) {
                    String key = config.getKeyForNode(node, i);
                    for (int h = 0; h < 3; h++) {
                        newNodeMap.put(LibKetamaHash.hash(key, h), node);
                    }
                }
            } else if (DefaultHashAlgorithm.KETAMA_HASH == hashAlg) {
                for (int i = 0; i < numReps / 4; i++) {
                    byte[] digest = DefaultHashAlgorithm.computeMd5(config.getKeyForNode(node, i));
                    for (int h = 0; h < 4; h++) {
                        Long k = ((long) (digest[3 + h * 4] & 0xFF) << 24)
                                | ((long) (digest[2 + h * 4] & 0xFF) << 16)
                                | ((long) (digest[1 + h * 4] & 0xFF) << 8)
                                | (digest[h * 4] & 0xFF);
                        newNodeMap.put(k, node);
                    }
                }
            } else {
                for (int i = 0; i < numReps; i++) {
                    newNodeMap.put(hashAlg.hash(config.getKeyForNode(node, i)), node);
                }
            }
        }
        ketamaNodes = newNodeMap;
    }
}
