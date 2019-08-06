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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A MessageTest.
 *
 * @author Heiko Scherrer
 */
public class MessageTest {

    @Test final void testConstruction() {
        Message m = new Message(4711, "Test message");
        assertThat(m.getMessageText()).isEqualTo("Test message");
        assertThat(m.getMessageNo()).isEqualTo(4711);
        assertThat(m).isEqualTo((new Message(4711, "Test message")));
    }

    @Test void testConstructionWithBuilder() {
        Message m = Message.newBuilder().messageNo(4711).messageText("Test message").build();
        assertThat(m.getMessageText()).isEqualTo("Test message");
        assertThat(m.getMessageNo()).isEqualTo(4711);
        assertThat(m).isEqualTo((new Message(4711, "Test message")));
    }

    @Test void testEqualityLight() {
        Message m1 = Message.newBuilder().messageNo(4711).messageText("Test message").build();
        Message m2 = Message.newBuilder().messageNo(4711).messageText("Test message").build();
        Message m3 = Message.newBuilder().messageNo(9999).messageText("Test message").build();
        Message m4 = Message.newBuilder().messageNo(4711).messageText("Error message").build();

        assertThat(m1).isEqualTo(m2);
        assertThat(m2).isEqualTo(m1);

        assertThat(m1).isNotEqualTo(m3);
        assertThat(m1).isNotEqualTo(m4);

        assertThat(m3).isNotEqualTo(m4);
        assertThat(m4).isNotEqualTo(m3);
    }

    @Test void testProperOutcomeOfToString() {
        Message m1 = Message.newBuilder().messageNo(4711).messageText("Test message").build();
        assertThat(m1.toString()).isEqualTo("4711" + Message.SEPARATOR + "Test message");
    }
}
