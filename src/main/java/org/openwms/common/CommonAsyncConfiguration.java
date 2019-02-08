/*
 * Copyright 2018 Heiko Scherrer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common;

import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * A CommonAsyncConfiguration contains the modules asynchronous configuration that is
 * always active.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Configuration
@EnableRabbit
class CommonAsyncConfiguration {

    @Bean
    TopicExchange lgExchange(@Value("${owms.events.common.lg.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    Queue lgQueue(@Value("${owms.events.common.lg.queue-name}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    Binding lgBinding(TopicExchange lgExchange, Queue lgQueue, @Value("${owms.events.common.lg.routing-key}") String routingKey) {
        return BindingBuilder
                .bind(lgQueue)
                .to(lgExchange)
                .with(routingKey);
    }
}
