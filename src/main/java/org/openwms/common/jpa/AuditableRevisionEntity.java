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
package org.openwms.common.jpa;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * A AuditableRevisionEntity.
 *
 * @author Heiko Scherrer
 */
@Configurable
@Entity
@Table(name = "COM_SRV_REVISION")
@RevisionEntity(AuditableEntityListener.class)
class AuditableRevisionEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revSequence")
    @SequenceGenerator(name = "revSequence", sequenceName = "SEQ_REVISION_INFO")
    @RevisionNumber
    @Column(name = "C_PK")
    private Long pk;

    @Column(name = "C_TIMESTAMP")
    @RevisionTimestamp
    private long timestamp;

    @Column(name = "C_USER")
    private String userName;

    public void setUserName(String userName) {
        this.userName = userName;
    }
}