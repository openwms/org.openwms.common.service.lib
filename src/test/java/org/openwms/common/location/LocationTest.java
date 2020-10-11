/*
 * Copyright 2005-2020 the original author or authors.
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A LocationTest.
 *
 * @author Heiko Scherrer
 */
class LocationTest {

    private static final LocationPK ID1 = new LocationPK.Builder().area("area").aisle("aisle").x("x").y("y").z("z").build();

    @Nested
    @DisplayName("Creation")
    class CreationTests {

        @Test
        void test_jpa_constructor_exists() {
            assertThat(new Location()).isNotNull();
        }

        @Test
        void shall_fail_with_null() {
            assertThatThrownBy(
                    () -> new Location(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shall_fail_with_null_in_factory() {
            assertThatThrownBy(
                    () -> Location.create(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void test_factory() {
            Location location = Location.create(ID1);
            assertThat(location).isNotNull();
        }

        @Test
        void shall_return_defaults() {
            Location l = new Location(ID1);
            assertThat(l.getLocationId()).isEqualTo(ID1);
            assertThat(l.getNoMaxTransportUnits()).isEqualTo(Location.DEF_MAX_TU);
            assertThat(l.isCountingActive()).isFalse();
            assertThat(l.getCheckState()).isEqualTo(Location.DEF_CHECK_STATE);
            assertThat(l.isLocationGroupCountingActive()).isEqualTo(Location.DEF_LG_COUNTING_ACTIVE);
            assertThat(l.isInfeedActive()).isEqualTo(Location.DEF_INCOMING_ACTIVE);
            assertThat(l.isInfeedBlocked()).isNotEqualTo(Location.DEF_INCOMING_ACTIVE);
            assertThat(l.isOutfeedActive()).isEqualTo(Location.DEF_OUTGOING_ACTIVE);
            assertThat(l.isOutfeedBlocked()).isNotEqualTo(Location.DEF_OUTGOING_ACTIVE);
            assertThat(l.isDirectBookingAllowed()).isEqualTo(true);
            assertThat(l.getPlcState()).isEqualTo(Location.DEF_PLC_STATE);
            assertThat(l.isConsideredInAllocation()).isEqualTo(Location.DEF_CONSIDERED_IN_ALLOCATION);
        }
    }

    @Nested
    @DisplayName("Messages")
    class MessageTests {
        @Test void shall_fail_when_add_message_null() {
            Location l = new Location(ID1);
            assertThatThrownBy(
                    () -> l.addMessage(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test void testMessageHandling() {
            Location l = new Location(ID1);
            Message m = Message.newBuilder().messageNo(1).messageText("First message").build();

            l.addMessage(m);
            assertThat(l.getMessages()).hasSize(1);
            assertThat(l.getMessages()).contains(m);

            l.removeMessages(m);
            assertThat(l.getMessages()).hasSize(0);
        }

        @Test void shall_fail_when_remove_message_null() {
            Location l = new Location(ID1);
            assertThatThrownBy(
                    () -> l.removeMessages(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test void test_infeed() {
        Location l = new Location(ID1);
        l.setInfeed(false);
        assertThat(l.isInfeedActive()).isFalse();
    }

    @Test void test_outfeed() {
        Location l = new Location(ID1);
        l.setOutfeed(false);
        assertThat(l.isOutfeedActive()).isFalse();
    }

    @Test void shall_fail_with_LocationGroup_Null() {
        Location l = new Location(ID1);
        assertThatThrownBy(
                () -> l.setLocationGroup(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void testSetLocationGroup() {
        Location l = new Location(ID1);
        assertThat(l.getLocationGroup()).isNull();
        LocationGroup lg1 = new LocationGroup("error zone");
        LocationGroup lg2 = new LocationGroup("picking");
        lg2.setLocationGroupCountingActive(false);

        l.setLocationGroup(lg1);
        assertThat(l.getLocationGroup()).isEqualTo(lg1);
        assertThat(l.isLocationGroupCountingActive()).isEqualTo(lg1.isLocationGroupCountingActive());
        l.setLocationGroup(lg2);
        assertThat(l.getLocationGroup()).isEqualTo(lg2);
        assertThat(l.isLocationGroupCountingActive()).isEqualTo(lg2.isLocationGroupCountingActive());
    }

    /**
     * Test adding messages to the location. Test cascading persistence.
     @Test public final void testAddingErrorMessages() {
     LocationPK pk = new LocationPK("OWN", "OWN", "OWN", "OWN", "OWN");
     Location location = new Location(pk);
     locationRepository.save(location);

     location.addMessage(new Message(1, "Errormessage 1"));
     location.addMessage(new Message(2, "Errormessage 2"));

     locationRepository.save(location);

     Assert.assertEquals("Expected 2 persisted Messages added to the location", 2, location.getMessages().size());
     Query query = entityManager.createQuery("select count(m) from Message m");
     Long cnt = (Long) query.getSingleResult();
     Assert.assertEquals("Expected 2 persisted Messages in MESSAGE table", 2, cnt.intValue());
     }
     */
}
