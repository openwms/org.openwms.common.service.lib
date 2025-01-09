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
package org.openwms.common.transport.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.ameba.integration.jpa.ApplicationEntity;
import org.ameba.integration.jpa.BaseEntity;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.openwms.common.transport.TransportUnit;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

import static org.openwms.core.time.TimeProvider.DATE_TIME_WITH_TIMEZONE_FORMAT;

/**
 * A TransportUnitReservation.
 *
 * @author Heiko Scherrer
 */
@Audited
@AuditOverride(forClass = ApplicationEntity.class)
@AuditOverride(forClass = BaseEntity.class)
@Entity
@Table(name = "COM_TU_RESERVATION")
public class TransportUnitReservation extends ApplicationEntity implements Serializable {

    /** An arbitrary field to store User, PickOrderPositionSplit etc. */
    @Column(name = "C_RESERVED_BY")
    private String reservedBy;

    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE_FORMAT)
    @Column(name = "C_RESERVED_AT", columnDefinition = "timestamp(0)")
    private ZonedDateTime reservedAt;

    /** The {@code TransportUnit} instance, the {@code Reservation} belongs to. */
    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "C_TRANSPORT_UNIT_PK", referencedColumnName = "C_PK", nullable = false, foreignKey = @ForeignKey(name = "FK_TURESERVATION_PK"))
    private TransportUnit transportUnit;

    /** Dear JPA... */
    protected TransportUnitReservation() { }

    public TransportUnitReservation(TransportUnit transportUnit, String reservedBy) {
        this.reservedBy = reservedBy;
        this.reservedAt = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        this.transportUnit = transportUnit;
    }

    public TransportUnit getTransportUnit() {
        return transportUnit;
    }

    public void setTransportUnit(TransportUnit transportUnit) {
        this.transportUnit = transportUnit;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
        if (reservedBy != null) {
            this.reservedAt = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        }
    }

    public ZonedDateTime getReservedAt() {
        return reservedAt;
    }

    @Override
    public String toString() {
        return "TransportUnitReservation{transportUnit=" + transportUnit+"}";
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TransportUnitReservation that = (TransportUnitReservation) o;
        return Objects.equals(reservedBy, that.reservedBy) && Objects.equals(reservedAt, that.reservedAt) && Objects.equals(transportUnit, that.transportUnit);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reservedBy, reservedAt, transportUnit);
    }
}
