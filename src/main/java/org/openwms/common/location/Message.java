/*
 * Copyright 2005-2019 the original author or authors.
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
package org.openwms.common.location;

import org.ameba.integration.jpa.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Message can be used to store useful information about errors or events.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "COR_MESSAGE")
public class Message extends BaseEntity implements Serializable {

    /** String used to separate messageNo and messageText in toString. */
    public static final String SEPARATOR = " :: ";
    /** Message number. */
    @Column(name = "C_MESSAGE_NO")
    private int messageNo;

    /** Message description text. */
    @Column(name = "C_MESSAGE_TEXT")
    private String messageText;

    /*~ ----------------------------- constructors ------------------- */

    /**
     * Dear JPA...
     */
    protected Message() {
    }

    /**
     * Create a new {@code Message} with message number and message text.
     *
     * @param messageNo The message number
     * @param messageText The message text
     */
    public Message(int messageNo, String messageText) {
        this.messageNo = messageNo;
        this.messageText = messageText;
    }

    private Message(Builder builder) {
        messageNo = builder.messageNo;
        messageText = builder.messageText;
    }

    /**
     * Create a new builder instance to create messages from.
     *
     * @return The builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /*~ ----------------------------- methods ------------------- */

    /**
     * Return the message number.
     *
     * @return The message number
     */
    public int getMessageNo() {
        return messageNo;
    }

    /**
     * Return the message text.
     *
     * @return The message text
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return messageNo == message.messageNo &&
                Objects.equals(messageText, message.messageText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(messageNo, messageText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return messageNo + SEPARATOR + messageText;
    }


    /**
     * {@code Message} builder static inner class.
     */
    public static final class Builder {

        private int messageNo;
        private String messageText;

        private Builder() {
        }

        /**
         * Sets the {@code messageNo} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code messageNo} to set
         * @return a reference to this Builder
         */
        public Builder messageNo(int val) {
            messageNo = val;
            return this;
        }

        /**
         * Sets the {@code messageText} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code messageText} to set
         * @return a reference to this Builder
         */
        public Builder messageText(String val) {
            messageText = val;
            return this;
        }

        /**
         * Returns a {@code Message} built from the parameters previously set.
         *
         * @return a {@code Message} built with parameters of this {@code Message.Builder}
         */
        public Message build() {
            return new Message(this);
        }
    }
}