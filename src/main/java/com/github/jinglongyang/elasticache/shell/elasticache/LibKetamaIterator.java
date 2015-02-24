package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.compat.SpyObject;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author: jinglongyang
 */
public class LibKetamaIterator extends SpyObject implements Iterator<MemcachedNode> {
    private final String key;
    private long hashVal;
    private int remainingTries;
    private int numTries = 0;
    private final HashAlgorithm hashAlg;
    private final TreeMap<Long, MemcachedNode> ketamaNodes;

    /**
     * Create a new KetamaIterator to be used by a client for an operation.
     *
     * @param k           the key to iterate for
     * @param t           the number of tries until giving up
     * @param ketamaNodes the continuum in the form of a TreeMap to be used when
     *                    selecting a node
     * @param hashAlg     the hash algorithm to use when selecting within the
     *                    continuumq
     */
    protected LibKetamaIterator(final String k, final int t, TreeMap<Long, MemcachedNode> ketamaNodes, final HashAlgorithm hashAlg) {
        super();
        this.ketamaNodes = ketamaNodes;
        this.hashAlg = hashAlg;
        hashVal = hashAlg.hash(k);
        remainingTries = t;
        key = k;
    }

    private void nextHash() {
        // this.calculateHash(Integer.toString(tries)+key).hashCode();
        long tmpKey = hashAlg.hash((numTries++) + key);
        // This echos the implementation of Long.hashCode()
        hashVal += (int) (tmpKey ^ (tmpKey >>> 32));
        hashVal &= 0xffffffffL; /* truncate to 32-bits */
        remainingTries--;
    }

    public boolean hasNext() {
        return remainingTries > 0;
    }

    public MemcachedNode next() {
        try {
            return getNodeForKey(hashVal);
        } finally {
            nextHash();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("remove not supported");
    }

    private MemcachedNode getNodeForKey(long hash) {
        if (!ketamaNodes.containsKey(hash)) {
            Map.Entry<Long, MemcachedNode> entry = ketamaNodes.ceilingEntry(hash);
            if (entry != null) return entry.getValue();

            return ketamaNodes.firstEntry().getValue();
        }
        return ketamaNodes.get(hash);
    }
}
