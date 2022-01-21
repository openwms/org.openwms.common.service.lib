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
package org.openwms.common.jpa;

import org.ameba.http.identity.IdentityContextHolder;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * An AuditableEntityListener is a Hibernate Envers extension that resolves the current User from Spring Security Context and passes it as
 * user information down to the {@link AuditableRevisionEntity}, that is mapped to the Envers Revision table.
 *
 * @author Heiko Scherrer
 */
public class AuditableEntityListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        var rev = (AuditableRevisionEntity) revisionEntity;
        if (IdentityContextHolder.getCurrentIdentity() != null) {
            rev.setUserName(IdentityContextHolder.getCurrentIdentity());
        } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
            rev.setUserName(SecurityContextHolder.getContext().getAuthentication().getName());
        } else {
            rev.setUserName("N/A");
        }
    }
}