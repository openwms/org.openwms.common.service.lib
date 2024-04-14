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
package org.openwms.common.location.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationRemovalManager;
import org.openwms.common.location.api.events.LocationEvent;
import org.openwms.common.location.events.DeletionFailedEvent;
import org.openwms.common.location.impl.registration.RegistrationService;
import org.openwms.common.location.impl.registration.ReplicaRegistry;
import org.openwms.core.listener.RemovalNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.openwms.common.CommonMessageCodes.LOCATION_NOT_FOUND_BY_PKEY;

/**
 * A LocationRemovalManagerImpl.
 *
 * @author Heiko Scherrer
 */
@TxService
class LocationRemovalManagerImpl implements LocationRemovalManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationRemovalManagerImpl.class);
    private final ApplicationEventPublisher eventPublisher;
    private final Translator translator;
    private final RestTemplate aLoadBalanced;
    private final LocationRepository repository;
    private final RegistrationService registrationService;
    private final DiscoveryClient dc;

    LocationRemovalManagerImpl(ApplicationEventPublisher eventPublisher, Translator translator, RestTemplate aLoadBalanced, LocationRepository repository, RegistrationService registrationService, DiscoveryClient dc) {
        this.eventPublisher = eventPublisher;
        this.translator = translator;
        this.aLoadBalanced = aLoadBalanced;
        this.repository = repository;
        this.registrationService = registrationService;
        this.dc = dc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void tryDelete(@NotBlank String pKey) {
        var locationOpt = repository.findBypKey(pKey);
        if (locationOpt.isEmpty()) {
            LOGGER.warn("Location with pKey [{}] shall be deleted but it does not exist", pKey);
            throw new NotFoundException(translator, LOCATION_NOT_FOUND_BY_PKEY, new String[]{pKey}, pKey);
        }
        deleteInternal(locationOpt.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void deleteAll(@NotNull Collection<Location> locations) {
        repository.deleteAll(locations);
    }

    private void deleteInternal(Location location) {
        verifyNoTransportUnitsOn(location);
        var registeredServices = registrationService.getAllRegistered();

        // first ask all services and call the requestRemovalEndpoint
        // might throw RemovalNotAllowedException and exit
        for (var srv : registeredServices) {
            var si = dc.getInstances(srv.getApplicationName());
            askForRemoval(si, srv, List.of(location.getPersistentKey()));
        }

        // if all are fine then call the removeEndpoint to mark the Location as deleted and not be visible in the foreign service
        try {
            for (var srv : registeredServices) {
                var si = dc.getInstances(srv.getApplicationName());
                removeLocation(si, srv, List.of(location.getPersistentKey()));
            }
            deleteLocation(location);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            // if any failure occurs, send a persistent async message to release the deletion for everyone
            eventPublisher.publishEvent(new DeletionFailedEvent(location.getPersistentKey()));
        }
    }

    private void verifyNoTransportUnitsOn(Location location) {
        if (repository.doesTUonLocationExists(location.getPk())) {
            throw new RemovalNotAllowedException("TransportUnit exist on Location [%s]".formatted(location.getLocationId()));
        }
    }

    private void verifyNoTransportUnitsOn(Collection<String> pKeys) {
        if (repository.doesTUonLocationExists(pKeys)) {
            throw new RemovalNotAllowedException("TransportUnit exist on one or more Locations");
        }
    }

    private void deleteLocation(Location location) {
        verifyNoTransportUnitsOn(location);
        // if ALL still agree, then send a persistent async message to commit the deletion after the transaction
        eventPublisher.publishEvent(LocationEvent.of(location, LocationEvent.LocationEventType.DELETED));
        // and finally delete the Location
        repository.delete(location);

    }

    /**
     * {@inheritDoc}
     *
     * At first the implementation checks if any {@code TransportUnit} is booked onto one of the {@link Location}s and as second all parties
     * of interest are asked if deletion in the current moment is okay for them, too.
     */
    @Override
    @Measured
    public boolean allowedToDelete(@NotNull Collection<String> pKeys) {
        verifyNoTransportUnitsOn(pKeys);
        var registeredServices = registrationService.getAllRegistered();
        try {
            // first ask all services and call the requestRemovalEndpoint
            // might throw RemovalNotAllowedException and exit
            for (var srv : registeredServices) {
                var si = dc.getInstances(srv.getApplicationName());
                askForRemoval(si, srv, pKeys);
            }
            LOGGER.debug("It's allowed to delete the given Locations");
            return true;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            LOGGER.debug("It's NOT allowed to delete the given Locations");
            return false;
        }
   }

    /**
     * {@inheritDoc}
     *
     * Call interesing parties to mark the {@link Location}s for deletion.
     */
    @Override
    @Measured
    public void markForDeletion(@NotNull Collection<String> pKeys) {
        var registeredServices = registrationService.getAllRegistered();
        try {
            for (var srv : registeredServices) {
                removeLocation(dc.getInstances(srv.getApplicationName()), srv, pKeys);
            }
            LOGGER.debug("All Locations marked for deletion");
        } catch (Exception e) {
            // if any failure occurs, send a persistent async message to release the deletion for everyone
            LOGGER.debug("Location cannot be marked for deletion, sending events to rollback");
            for (var pKey : pKeys) {
                eventPublisher.publishEvent(new DeletionFailedEvent(pKey));
            }
            throw e;
        }
    }

    private void askForRemoval(List<ServiceInstance> sis, ReplicaRegistry srv, Collection<String> pKeys) {
        for (var si : sis) {
            boolean result;
            var endpoint = si.getMetadata().get("protocol") + "://" + si.getServiceId() + srv.getRequestRemovalEndpoint();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Request for removal to the service with API [{}]", endpoint);
            }
            try {
                var response = aLoadBalanced.exchange(
                        endpoint,
                        HttpMethod.GET,
                        null,
                        Boolean.class,
                        Map.of("pKey", pKeys)
                );
                result = response.getBody() != null && response.getBody();
                if (result) {
                    LOGGER.info("Service [{}] allows to remove all Locations", si.getServiceId());
                } else {
                    LOGGER.info("Service [{}] does not allow to remove Locations", si.getServiceId());
                }
            } catch (Exception e) {
                throw new RemovalNotAllowedException("Exception. Removal of Locations is declined by service [%s]".formatted(si.getServiceId()));
            }
            if (!result) {
                throw new RemovalNotAllowedException("Removal of Locations has been declined by service [%s]".formatted(si.getServiceId()));
            }
        }
    }

    private void removeLocation(List<ServiceInstance> sis, ReplicaRegistry srv, Collection<String> pKeys) {
        for (var si : sis) {
            var endpoint = si.getMetadata().get("protocol") + "://" + si.getServiceId() + srv.getRemovalEndpoint();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Ask for removal the service with API [{}]", endpoint);
            }
            try {
                aLoadBalanced.exchange(
                        endpoint,
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        Map.of("pKey", pKeys)
                );
                LOGGER.info("Service [{}] removed Locations", si.getServiceId());
            } catch (Exception e) {
                throw new RemovalNotAllowedException(format("Exception. Removal of Locations is declined by service [%s]", si.getServiceId()));
            }
        }
    }
}
