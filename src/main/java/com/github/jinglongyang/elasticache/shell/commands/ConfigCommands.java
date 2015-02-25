package com.github.jinglongyang.elasticache.shell.commands;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by jinglongyang on 2/24/15.
 */
@Component
public class ConfigCommands implements CommandMarker {
    @CliAvailabilityIndicator({"config", "env"})
    public boolean isAvailable() {
        return true;
    }

    @CliCommand(value = "config", help = "Config shortcut for ElastiCache server")
    public String config(@CliOption(key = {"name", "n"}, mandatory = true, help = "The short name of ElastiCache host") final String name,
                         @CliOption(key = {"host", "h"}, mandatory = true, help = "The host of ElastiCache") final String host) {
        String path = new File(".").getAbsoluteFile().getParent();
        File file = new File(String.format("%s%selasticache-cli-config.properties", path, File.separator));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return ExceptionUtils.getMessage(e);
            }
        }
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            return ExceptionUtils.getMessage(e);
        }
        String value = properties.getProperty(name);
        if (value == null || !value.equals(host)) {
            properties.setProperty(name, host);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                properties.store(fileOutputStream, "Add a new config");
            } catch (IOException e) {
                return ExceptionUtils.getMessage(e);
            }
            return "Config successfully";
        } else {
            return "The config already exists.";
        }
    }

    @CliCommand(value = "env", help = "Get env config of ElastiCache command line tool")
    public String getEnv() {
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
        if (properties.isEmpty()) {
            return "There is no env config";
        }
        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> entry : entrySet) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
