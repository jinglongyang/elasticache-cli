/*
 * Copyright 2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jinglongyang.elasticache.shell.commands;

import org.junit.Test;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;

import static org.junit.Assert.assertEquals;

public class ElastiCacheClientCommandTests {

    @Test
    public void testGet() {
        Bootstrap bootstrap = new Bootstrap();

        JLineShellComponent shell = bootstrap.getJLineShellComponent();

        CommandResult cr = shell.executeCommand("connect --h rpcloud-cache-prod.epxzmn.cfg.usw2.cache.amazonaws.com:11211");
        assertEquals(true, cr.isSuccess());
        assertEquals("Connected successfully", cr.getResult());

        cr = shell.executeCommand("get facebook_app_access_token_4_login");
        assertEquals(true, cr.isSuccess());
        assertEquals("Key does not exist", cr.getResult());
    }
}
