package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.HashAlgorithm;

/**
 * When using java spymemached client communicate with nodejs memcache client via memcache server, there is some issues
 * in nodejs library, the <a href="https://github.com/3rd-Eden/node-memcached">node-memcached</a> has incompatibility lib ketama
 * has implementation, so implement a new hash class same with node-memcached
 *
 * @author: jinglongyang
 */
public class LibKetamaHash implements HashAlgorithm {
    private LibKetamaNodeLocatorMethod libKetamaNodeLocatorMethod;

    public LibKetamaHash(LibKetamaNodeLocatorMethod libKetamaNodeLocatorMethod) {
        this.libKetamaNodeLocatorMethod = libKetamaNodeLocatorMethod;
    }

    @Override
    public long hash(String k) {
        return hash(k, 0);
    }

    public static long hash(String k, int h) {
        byte[] keys = DefaultHashAlgorithm.computeMd5(k);
        StringBuffer hexString = new StringBuffer();
        for (int j = 0; j < keys.length; j++) {
            String hex = Integer.toHexString(0xff & keys[j]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        int[] digest = new int[hexString.length()];
        for (int j = 0; j < hexString.length(); j++)
            digest[j] = hexString.charAt(j);
        return ((long) (digest[3 + h * 4] & 0xFF) << 24)
                | ((long) (digest[2 + h * 4] & 0xFF) << 16)
                | ((long) (digest[1 + h * 4] & 0xFF) << 8)
                | (digest[h * 4] & 0xFF);
    }

    public LibKetamaNodeLocatorMethod getLibKetamaNodeLocatorMethod() {
        return libKetamaNodeLocatorMethod;
    }
}
