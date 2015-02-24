/*
 * Copyright 2011-2012 the original author or authors.
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

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

/**
 * @author jinglongyang
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ElastiCacheCommandPromptProvider extends DefaultPromptProvider {

    @Override
    public String getPrompt() {
        return "elasticache-client-shell>";
    }


    @Override
    public String getProviderName() {
        return "ElastiCache client prompt provider";
    }

}
