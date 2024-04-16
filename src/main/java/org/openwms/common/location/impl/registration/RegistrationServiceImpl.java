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
package org.openwms.common.location.impl.registration;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.openwms.common.location.api.commands.LocationReplicaRegistration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A RegistrationServiceImpl.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class RegistrationServiceImpl implements RegistrationService {

    private final ReplicaRegistryRepository repository;

    RegistrationServiceImpl(ReplicaRegistryRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(@NotNull LocationReplicaRegistration registration) {
        var replicaOpt = repository.findByApplicationName(registration.applicationName());
        ReplicaRegistry eo;
        if (replicaOpt.isEmpty()) {

            // create new entry
            eo = new ReplicaRegistry();
            eo.setApplicationName(registration.applicationName());
        } else {

            // update existing one
            eo = replicaOpt.get();
        }
        eo.setRequestRemovalEndpoint(registration.requestRemovalEndpoint());
        eo.setRemovalEndpoint(registration.removeEndpoint());
        eo.setRegisteredAt(LocalDateTime.now());
        eo.setState("REGISTERED");
        repository.save(eo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void unregister(@NotNull LocationReplicaRegistration registration) {
        var replicaOpt = repository.findByApplicationName(registration.applicationName());
        ReplicaRegistry eo;
        if (replicaOpt.isEmpty()) {

            // create new entry
            eo = new ReplicaRegistry();
            eo.setApplicationName(registration.applicationName());
        } else {

            // update existing one
            eo = replicaOpt.get();
        }
        eo.setUnRegisteredAt(LocalDateTime.now());
        eo.setState("UNREGISTERED");
        repository.save(eo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull List<ReplicaRegistry> getAllRegistered() {
        return repository.findActiveOnes();
    }
}
