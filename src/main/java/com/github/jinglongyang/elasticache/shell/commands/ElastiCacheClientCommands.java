package com.github.jinglongyang.elasticache.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

    @CliAvailabilityIndicator({"get", "delete", "disconnect", "list"})
    public boolean isElastiCacheCommandAvailable() {
        return elastiCacheClient == null ? false : true;
    }

    @CliCommand(value = "connect", help = "Connect ElastiCache")
    public String connect(@CliOption(key = {"host", "h"}, mandatory = true, help = "ElastiCache server host") final String host,
                          @CliOption(key = {"timeout", "t"}, mandatory = false, help = "", unspecifiedDefaultValue = "10000") final long timeout,
                          @CliOption(key = {"algorithm", "a"}, mandatory = false, help = "") final String algorithm) {
        ElastiCacheClientBuilder elastiCacheClientBuilder = new ElastiCacheClientBuilder()
                .withAddress(host)
                .withOperationTimeout(timeout);
        if (StringUtils.isNotBlank(algorithm)) {
            String tmp = algorithm.trim().toUpperCase();
            HashAlgorithm hashAlgorithm;
            if (ElastiCacheClientBuilder.LIB_KETAMA_HASH.equals(tmp)) {
                hashAlgorithm = new LibKetamaHash(LibKetamaNodeLocatorMethod.hostname);
            } else {
                try {
                    hashAlgorithm = DefaultHashAlgorithm.valueOf(tmp);
                } catch (IllegalArgumentException e) {
                    return "Hash algorithm only can be one of NATIVE_HASH, CRC_HASH, FNV1_64_HASH, FNV1A_64_HASH, FNV1_32_HASH, FNV1A_32_HASH, KETAMA_HASH, LIB_KETAMA_HASH";
                }
            }
            if (hashAlgorithm != null) {
                elastiCacheClientBuilder.withHashAlgorithm(hashAlgorithm);
            }
        }
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
    public String get(@CliOption(key = {"", "key"}, mandatory = true, help = "The key of getting from ElastiCache") final String key,
                      @CliOption(key = {"type"}, mandatory = false, help = "The type of value.(string, json, primitive)", unspecifiedDefaultValue = "string") final String type) {
        Type valueType = Type.fromValue(type);
        if (valueType == null) {
            return "Type can only be string, json, primitive";
        }
        if (Type.String == valueType) {
            return elastiCacheClient.getStringValue(key);
        }
        if (Type.Primitive == valueType) {
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
        } catch (JsonProcessingException e) {
            return value;
        }
    }

    @CliCommand(value = "delete", help = "Delete a key from ElastiCache")
    public String delete(@CliOption(key = {"", "key"}, mandatory = true, help = "The key of deletion from ElastiCache") final String key) {
        boolean success = elastiCacheClient.delete(key);
        return success ? "Delete successfully" : "Delete failed";
    }

    @CliCommand(value = "list", help = "List ElastiCache Servers")
    public String list() {
        return elastiCacheClient.getAllNodeEndPoints();
    }

    @CliCommand(value = "disconnect", help = "Disconnect ElastiCache after connect")
    public ExitShellRequest disconnect() {
        elastiCacheClient.shutdown();
        return ExitShellRequest.NORMAL_EXIT;
    }

    //    @CliCommand(value = "set", help = "Set key value to ElastiCache")
    public String set(@CliOption(key = {"type"}, mandatory = true, help = "") final String type,
                      @CliOption(key = {"expire"}, mandatory = true, help = "") final int expireTime,
                      @CliOption(key = {"key"}, mandatory = true, help = "") final String key,
                      @CliOption(key = {"value"}, mandatory = true, help = "") final String value) {
        Type valueType = Type.fromValue(type);
        if (valueType == null) {
            return "";
        }
        boolean success;
        if (Type.String == valueType) {
            success = elastiCacheClient.setStringValue(key, value, expireTime);
        } else {
            long tmp;
            try {
                tmp = Long.parseLong(value);
            } catch (NumberFormatException e) {
                return "The value must be long";
            }
            success = elastiCacheClient.setLongValue(key, tmp, expireTime);
        }
        return success ? "Set successfully" : "Set failed";
    }


    public enum Type {
        String("string"), JSON("json"), Primitive("primitive");

        private String value;

        private Type(String value) {
            this.value = value;
        }

        public static Type fromValue(String value) {
            if (value == null) {
                return Type.String;
            }
            String tmp = value.trim().toLowerCase();
            if (Type.String.value.equals(tmp)) {
                return Type.String;
            }
            if (Type.JSON.value.equals(tmp)) {
                return Type.JSON;
            }
            if (Type.Primitive.value.equals(tmp)) {
                return Type.Primitive;
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
