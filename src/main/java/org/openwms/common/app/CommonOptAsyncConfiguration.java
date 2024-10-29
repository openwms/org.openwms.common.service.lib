/*
 * Copyright 2005-2024 the original author or authors.
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
import static org.openwms.common.CommonProfiles.SHIPPING_SUPPORT;

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
    public static final String POISON_MESSAGE = "poison-message";
    public static final String DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    @ConditionalOnExpression("'${owms.common.serialization}'=='json'")
    @Bean
    MessageConverter messageConverter() {
        var messageConverter = new Jackson2JsonMessageConverter();
        BOOT_LOGGER.info("Using JSON serialization over AMQP");
        return messageConverter;
    }

    @ConditionalOnExpression("'${owms.common.serialization}'=='barray'")
    @Bean
    MessageConverter serializerMessageConverter() {
        var messageConverter = new SerializerMessageConverter();
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
        var retryTemplate = new RetryTemplate();
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
    @Bean DirectExchange dlExchange(
            @Value("${spring.application.name}") String applicationName,
            @Value("${owms.dead-letter.exchange-name:}") String exchangeName
    ) {
        if (exchangeName.isEmpty()) {
            BOOT_LOGGER.warn("No dead letter exchange configured, falling back to: [{}]", applicationName);
            return new DirectExchange("dle." + applicationName);
        }
        return new DirectExchange(exchangeName);
    }
    @RefreshScope
    @Bean TopicExchange commonTuExchange(@Value("${owms.events.common.tu.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }
    @RefreshScope
    @Bean TopicExchange commonTuTExchange(@Value("${owms.events.common.tut.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }
    @RefreshScope
    @Bean TopicExchange commonTuCommandsExchange(@Value("${owms.commands.common.tu.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }
    @RefreshScope
    @Bean TopicExchange commonLocCommandsExchange(@Value("${owms.commands.common.loc.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }
    @RefreshScope
    @Bean TopicExchange commonLocRegistrationCommandsExchange(@Value("${owms.commands.common.registration.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }
    @Profile(SHIPPING_SUPPORT)
    @RefreshScope
    @Bean TopicExchange shippingExchange(@Value("${owms.events.shipping.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    /*~ ------------ Queues ------------- */
    @RefreshScope
    @Bean Queue dlQueue(
            @Value("${spring.application.name}") String applicationName,
            @Value("${owms.dead-letter.queue-name:}") String queueName
    ) {
        if (queueName.isEmpty()) {
            return QueueBuilder.durable(applicationName + "-dl-queue").build();
        }
        return QueueBuilder.durable(queueName).build();
    }
    @RefreshScope
    @Bean Queue commandsQueue(@Value("${owms.commands.common.tu.queue-name}") String queueName, DirectExchange dlExchange) {
        return QueueBuilder.durable(queueName)
                .withArgument(DEAD_LETTER_EXCHANGE, dlExchange.getName())
                .withArgument(DEAD_LETTER_ROUTING_KEY, POISON_MESSAGE)
                .build();
    }
    @RefreshScope
    @Bean Queue commonLocCommandsQueue(@Value("${owms.commands.common.loc.queue-name}") String queueName, DirectExchange dlExchange) {
        return QueueBuilder.durable(queueName)
                .withArgument(DEAD_LETTER_EXCHANGE, dlExchange.getName())
                .withArgument(DEAD_LETTER_ROUTING_KEY, POISON_MESSAGE)
                .build();
    }
    @RefreshScope
    @Bean Queue commonLocRegistrationCommandsQueue(@Value("${owms.commands.common.registration.queue-name}") String queueName, DirectExchange dlExchange) {
        return QueueBuilder.durable(queueName)
                .withArgument(DEAD_LETTER_EXCHANGE, dlExchange.getName())
                .withArgument(DEAD_LETTER_ROUTING_KEY, POISON_MESSAGE)
                .build();
    }
    @Profile(SHIPPING_SUPPORT)
    @RefreshScope
    @Bean Queue shippingSplitQueue(
            @Value("${owms.events.shipping.split.queue-name}") String queueName,
            @Value("${owms.dead-letter.exchange-name}") String exchangeName
    ) {
        return QueueBuilder.durable(queueName)
                .withArgument(DEAD_LETTER_EXCHANGE, exchangeName)
                .withArgument(DEAD_LETTER_ROUTING_KEY, POISON_MESSAGE)
                .build();
    }

    /*~ ------------ Bindings ------------- */
    @RefreshScope
    @Bean Binding dlBinding(
            @Value("${spring.application.name}") String applicationName,
            @Value("${owms.dead-letter.queue-name:}") String queueName,
            @Value("${owms.dead-letter.exchange-name:}") String exchangeName
    ) {
        return BindingBuilder
                .bind(dlQueue(applicationName, queueName))
                .to(dlExchange(applicationName, exchangeName))
                .with(POISON_MESSAGE);
    }
    @RefreshScope
    @Bean Binding commandsBinding(
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
    @Bean Binding locCommandsBinding(
            @Qualifier("commonLocCommandsExchange") TopicExchange commonLocCommandsExchange,
            @Qualifier("commonLocCommandsQueue") Queue commonLocCommandsQueue,
            @Value("${owms.commands.common.loc.routing-key}") String routingKey
    ) {
        return BindingBuilder
                .bind(commonLocCommandsQueue)
                .to(commonLocCommandsExchange)
                .with(routingKey);
    }
    @RefreshScope
    @Bean Binding locRegistrationCommandsBinding(
            @Qualifier("commonLocRegistrationCommandsExchange") TopicExchange commonLocRegistrationCommandsExchange,
            @Qualifier("commonLocRegistrationCommandsQueue") Queue commonLocRegistrationCommandsQueue,
            @Value("${owms.commands.common.registration.routing-key}") String routingKey
    ) {
        return BindingBuilder
                .bind(commonLocRegistrationCommandsQueue)
                .to(commonLocRegistrationCommandsExchange)
                .with(routingKey);
    }
    @Profile(SHIPPING_SUPPORT)
    @RefreshScope
    @Bean Binding splitBinding(
            @Qualifier("shippingExchange") TopicExchange shippingExchange,
            @Qualifier("shippingSplitQueue") Queue shippingSplitQueue,
            @Value("${owms.events.shipping.split.routing-key}") String routingKey
    ) {
        return BindingBuilder
                .bind(shippingSplitQueue)
                .to(shippingExchange)
                .with(routingKey);
    }
}
