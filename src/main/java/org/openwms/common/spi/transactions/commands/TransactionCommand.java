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
package org.openwms.common.spi.transactions.commands;

import org.openwms.common.spi.transactions.TransactionVO;

import java.beans.ConstructorProperties;

/**
 * A TransactionCommand.
 *
 * @author Heiko Scherrer
 */
public class TransactionCommand {

    private Type type;
    private TransactionVO transaction;

    public enum Type {
        CREATE
    }

    @ConstructorProperties({"type", "transaction"})
    protected TransactionCommand(Type type, TransactionVO transaction) {
        this.type = type;
        this.transaction = transaction;
    }

    public static TransactionCommand of(Type type, TransactionVO transaction) {
        return new TransactionCommand(type, transaction);
    }

    public Type getType() {
        return type;
    }

    public TransactionVO getTransaction() {
        return transaction;
    }
}
