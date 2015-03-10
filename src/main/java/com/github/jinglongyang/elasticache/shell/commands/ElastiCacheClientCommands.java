package com.github.jinglongyang.elasticache.shell.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jinglongyang.elasticache.shell.elasticache.ElastiCacheClient;
import com.github.jinglongyang.elasticache.shell.elasticache.ElastiCacheClientBuilder;
import com.github.jinglongyang.elasticache.shell.elasticache.LibKetamaHash;
import com.github.jinglongyang.elasticache.shell.elasticache.LibKetamaNodeLocatorMethod;
import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author jinglongyang
 */
@Component
public class ElastiCacheClientCommands implements CommandMarker {
    private ElastiCacheClient elastiCacheClient;
    private ObjectMapper mapper = new ObjectMapper();

    @CliAvailabilityIndicator({"connect"})
    public boolean isConnectAvailable() {
        return elastiCacheClient == null ? true : false;
    }

    @CliAvailabilityIndicator({"set", "get", "delete", "disconnect", "list", "incr"})
    public boolean isElastiCacheCommandAvailable() {
        return elastiCacheClient == null ? false : true;
    }

    @CliCommand(value = "connect", help = "Connect ElastiCache")
    public String connect(@CliOption(key = {"host", "h"}, mandatory = false, help = "ElastiCache server host") String host,
                          @CliOption(key = {"env", "e"}, mandatory = false, help = "ElastiCache server short name in config") final String env,
                          @CliOption(key = {"timeout", "t"}, mandatory = false, help = "", unspecifiedDefaultValue = "10000") final long timeout,
                          @CliOption(key = {"algorithm", "a"}, mandatory = false, help = "") final String algorithm) {

        if (StringUtils.isBlank(host) && StringUtils.isBlank(env)) {
            return "Host or env must have one.";
        }
        if (StringUtils.isBlank(host)) {
            String path = new File(".").getAbsoluteFile().getParent();
            File file = new File(String.format("%s%selasticache-cli-config.properties", path, File.separator));
            if (!file.exists()) {
                return "There is no env config";
            }
            Properties properties = new Properties();
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                properties.load(fileInputStream);
            } catch (IOException e) {
                return ExceptionUtils.getMessage(e);
            }
            if (!properties.containsKey(env)) {
                return String.format("The config [%s] does not exist", env);
            }
            host = properties.getProperty(env);
        }

        ElastiCacheClientBuilder elastiCacheClientBuilder = new ElastiCacheClientBuilder()
                .withAddress(host)
                .withOperationTimeout(timeout);
        HashAlgorithm hashAlgorithm = null;
        if (StringUtils.isNotBlank(algorithm)) {
            String tmp = algorithm.trim().toUpperCase();
            if (ElastiCacheClientBuilder.LIB_KETAMA_HASH.equals(tmp)) {
                hashAlgorithm = new LibKetamaHash(LibKetamaNodeLocatorMethod.hostname);
            } else {
                try {
                    hashAlgorithm = DefaultHashAlgorithm.valueOf(tmp);
                } catch (IllegalArgumentException e) {
                    return "Hash algorithm only can be one of NATIVE_HASH, CRC_HASH, FNV1_64_HASH, FNV1A_64_HASH, FNV1_32_HASH, FNV1A_32_HASH, KETAMA_HASH, LIB_KETAMA_HASH";
                }
            }
        }
        if (hashAlgorithm == null) {
            hashAlgorithm = new LibKetamaHash(LibKetamaNodeLocatorMethod.hostname);
        }
        elastiCacheClientBuilder.withHashAlgorithm(hashAlgorithm);
        try {
            MemcachedClient memcachedClient = elastiCacheClientBuilder.build();
            elastiCacheClient = new ElastiCacheClient(memcachedClient, timeout);
            return "Connected successfully";
        } catch (IOException e) {
            String message = ExceptionUtils.getMessage(e);
            elastiCacheClient = null;
            return message;
        }
    }

    @CliCommand(value = "get", help = "Get key from ElastiCache")
    public String get(@CliOption(key = {"", "key", "k"}, mandatory = true, help = "The key of getting from ElastiCache") final String key,
                      @CliOption(key = {"type", "t"}, mandatory = false, help = "The type of value.(string, json, primitive)", unspecifiedDefaultValue = "string") final String type) {
        ReadType valueReadType = ReadType.fromValue(type);
        if (valueReadType == null) {
            return "Type can only be string, json, primitive";
        }
        if (ReadType.String == valueReadType) {
            return elastiCacheClient.getStringValue(key);
        }
        if (ReadType.Primitive == valueReadType) {
            return String.valueOf(elastiCacheClient.get(key));
        }
        String value = elastiCacheClient.getStringValue(key);
        if (value == null) {
            return "Key does not exist.";
        }
        Object json = null;
        try {
            json = mapper.readValue(value, Object.class);
        } catch (IOException e) {
        }
        if (json == null) {
            return value;
        }
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (IOException e) {
            return value;
        }
    }

    @CliCommand(value = "delete", help = "Delete a key from ElastiCache")
    public String delete(@CliOption(key = {"", "key", "k"}, mandatory = true, help = "The key of deletion from ElastiCache") final String key) {
        boolean success = elastiCacheClient.delete(key);
        return success ? "Delete successfully" : "Delete failed";
    }

    @CliCommand(value = "incr", help = "Increase a key from ElastiCache")
    public String incr(@CliOption(key = {"", "key", "k"}, mandatory = true, help = "The key of increased from ElastiCache") final String key,
                       @CliOption(key = {"value", "v"}, mandatory = true, help = "") final int value) {
        return String.valueOf(elastiCacheClient.incr(key, value));
    }

    @CliCommand(value = "list", help = "List ElastiCache Servers")
    public String list() {
        return elastiCacheClient.getServerNodes();
    }

    @CliCommand(value = "disconnect", help = "Disconnect ElastiCache after connect")
    public String disconnect() {
        elastiCacheClient.shutdown();
        elastiCacheClient = null;
        return "Disconnected";
    }

    @CliCommand(value = "set", help = "Set key value pairs to ElastiCache")
    public String set(@CliOption(key = {"key", "k"}, mandatory = true, help = "The key will be used when save to ElastiCache") final String key,
                      @CliOption(key = {"value", "v"}, mandatory = true, help = "The value will be saved to ElastiCache") final String value,
                      @CliOption(key = {"type", "t"}, mandatory = false, help = "The type of the value(string, long, int, float, double, boolean)") final String type,
                      @CliOption(key = {"expire", "e"}, mandatory = true, help = "Expire time of the key") final int expireTime) {
        WriteType valueReadType = WriteType.fromValue(type);
        if (valueReadType == null) {
            return "Type can only be string, long, int, float, double, boolean";
        }
        Object tmp = valueReadType.parse(value);
        boolean success = elastiCacheClient.setValue(key, tmp, expireTime);
        return success ? "Set successfully" : "Set failed";
    }
}
