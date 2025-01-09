/*
 * Copyright 2005-2025 the original author or authors.
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
package org.openwms.common.spi.transactions;

import java.util.Map;

/**
 * A TransactionBuilder.
 *
 * @author Heiko Scherrer
 */
public final class TransactionBuilder {

    private TransactionVO transactionVO;

    private TransactionBuilder() {
        transactionVO = new TransactionVO();
    }

    public static TransactionBuilder aTransactionVO() {
        return new TransactionBuilder();
    }

    public TransactionBuilder withDescription(String description) {
        transactionVO.setDescription(description);
        return this;
    }

    public TransactionBuilder withType(String type) {
        transactionVO.setType(type);
        return this;
    }

    public TransactionBuilder withCreatedByUser(String createdByUser) {
        transactionVO.setCreatedByUser(createdByUser);
        return this;
    }

    public TransactionBuilder withSender(String sender) {
        transactionVO.setSender(sender);
        return this;
    }

    public TransactionBuilder withDetail(String key, String value) {
        transactionVO.addDetail(key, value);
        return this;
    }

    public TransactionBuilder withDetails(Map<String, String> details) {
        transactionVO.setDetails(details);
        return this;
    }

    public TransactionBuilder but() {
        return aTransactionVO().withDescription(transactionVO.getDescription()).withType(transactionVO.getType()).withCreatedByUser(transactionVO.getCreatedByUser()).withSender(transactionVO.getSender()).withDetails(transactionVO.getDetails());
    }

    public TransactionVO build() {
        return transactionVO;
    }
}
