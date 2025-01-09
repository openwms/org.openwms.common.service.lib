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
package org.openwms.common.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.io.Serializable;

/**
 * A AuditableRevisionEntity is mapped onto Hibernate Envers Revision table and extended about the current user.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "COM_SRV_REVISION")
@RevisionEntity(AuditableEntityListener.class)
class AuditableRevisionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
    @RevisionNumber
    @Column(name = "C_PK")
    private Long pk;

    @Column(name = "C_TIMESTAMP")
    @RevisionTimestamp
    private long timestamp;

    @Column(name = "C_USER")
    private String userName;

    @Column(name = "C_TRACE_ID")
    private String traceId;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}