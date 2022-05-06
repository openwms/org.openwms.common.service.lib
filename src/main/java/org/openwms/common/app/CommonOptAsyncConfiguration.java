/*
 * Copyright 2005-2022 the original author or authors.
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
package org.openwms.common.app;

import org.ameba.amqp.RabbitTemplateConfigurable;
import org.openwms.core.SpringProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Objects;

import static org.ameba.LoggingCategories.BOOT;

/**
 * A CommonOptAsyncConfiguration contains the modules asynchronous configuration that is
 * only active when Springs ASYNCHRONOUS profile is set.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Configuration
@RefreshScope
@EnableRabbit
class CommonOptAsyncConfiguration {

    private static final Logger BOOT_LOGGER = LoggerFactory.getLogger(BOOT);
    private static final String POISON_MESSAGE = "poison-message";

    @ConditionalOnExpression("'${owms.common.serialization}'=='json'")
    @Bean
    MessageConverter messageConverter() {
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        BOOT_LOGGER.info("Using JSON serialization over AMQP");
        return messageConverter;
    }

    @ConditionalOnExpression("'${owms.common.serialization}'=='barray'")
    @Bean
    MessageConverter serializerMessageConverter() {
        SerializerMessageConverter messageConverter = new SerializerMessageConverter();
        BOOT_LOGGER.info("Using byte array serialization over AMQP");
        return messageConverter;
    }

    @Primary
    @Bean(name = "amqpTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            ObjectProvider<MessageConverter> messageConverter,
            @Autowired(required = false) RabbitTemplateConfigurable rabbitTemplateConfigurable) {
        var rabbitTemplate = new RabbitTemplate(connectionFactory);
        var backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setMultiplier(2);
        backOffPolicy.setMaxInterval(15000);
        backOffPolicy.setInitialInterval(500);
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        rabbitTemplate.setRetryTemplate(retryTemplate);
        rabbitTemplate.setMessageConverter(Objects.requireNonNull(messageConverter.getIfUnique()));
        if (rabbitTemplateConfigurable != null) {
            rabbitTemplateConfigurable.configure(rabbitTemplate);
        }
        return rabbitTemplate;
    }

    /*~ --------------- Exchanges --------------- */
    @RefreshScope
    @Bean
    TopicExchange commonTuExchange(@Value("${owms.events.common.tu.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }
    @RefreshScope
    @Bean
    TopicExchange commonTuTExchange(@Value("${owms.events.common.tut.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    /*~ ------------ Commands ------------- */
    @RefreshScope
    @Bean
    Queue commandsQueue(@Value("${owms.commands.common.tu.queue-name}") String queueName,
            @Value("${owms.common.dead-letter.exchange-name}") String exchangeName) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", exchangeName)
                .withArgument("x-dead-letter-routing-key", POISON_MESSAGE)
                .build();
    }

    @RefreshScope
    @Bean
    TopicExchange commonTuCommandsExchange(@Value("${owms.commands.common.tu.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @RefreshScope
    @Bean
    Binding commandsBinding(
            @Qualifier("commonTuCommandsExchange") TopicExchange commonTuCommandsExchange,
            @Qualifier("commandsQueue") Queue commandsQueue,
            @Value("${owms.commands.common.tu.routing-key}") String routingKey
    ) {
        return BindingBuilder
                .bind(commandsQueue)
                .to(commonTuCommandsExchange)
                .with(routingKey);
    }

    @RefreshScope
    @Bean
    Queue commonLocCommandsQueue(@Value("${owms.commands.common.loc.queue-name}") String queueName,
                        @Value("${owms.common.dead-letter.exchange-name}") String exchangeName) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", exchangeName)
                .withArgument("x-dead-letter-routing-key", POISON_MESSAGE)
                .build();
    }

    @RefreshScope
    @Bean
    TopicExchange commonLocCommandsExchange(@Value("${owms.commands.common.loc.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }
    @RefreshScope
    @Bean
    Binding locCommandsBinding(
            @Qualifier("commonLocCommandsExchange") TopicExchange commonLocCommandsExchange,
            @Qualifier("commonLocCommandsQueue") Queue commonLocCommandsQueue,
            @Value("${owms.commands.common.loc.routing-key}") String routingKey
    ) {
        return BindingBuilder
                .bind(commonLocCommandsQueue)
                .to(commonLocCommandsExchange)
                .with(routingKey);
    }

    /* Dead Letter */
    @RefreshScope
    @Bean
    DirectExchange dlExchange(@Value("${owms.common.dead-letter.exchange-name}") String exchangeName) {
        return new DirectExchange(exchangeName);
    }

    @RefreshScope
    @Bean
    Queue dlQueue(@Value("${owms.common.dead-letter.queue-name}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @RefreshScope
    @Bean Binding dlBinding(
            @Value("${owms.common.dead-letter.queue-name}") String queueName,
            @Value("${owms.common.dead-letter.exchange-name}") String exchangeName) {
        return BindingBuilder
                .bind(dlQueue(queueName))
                .to(dlExchange(exchangeName))
                .with(POISON_MESSAGE);
    }
}
