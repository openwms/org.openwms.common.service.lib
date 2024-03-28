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
import org.openwms.common.location.LocationRemovalManager;
import org.openwms.common.location.api.commands.RevokeLocationRemoveCommand;
import org.openwms.common.location.api.events.LocationEvent;
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
    public void delete(@NotBlank String pKey) {
        var locationOpt = repository.findBypKey(pKey);
        if (locationOpt.isEmpty()) {
            LOGGER.warn("Location with pKey [{}] shall be deleted but it does not exist", pKey);
            throw new NotFoundException(translator, LOCATION_NOT_FOUND_BY_PKEY, new String[]{pKey}, pKey);
        }

        var registeredServices = registrationService.getAllRegistered();

        // first ask all services and call the requestRemovalEndpoint
        // might throw RemovalNotAllowedException and exit
        for (var srv : registeredServices) {
            var si = dc.getInstances(srv.getApplicationName());
            askForRemoval(si, srv, pKey);
        }

        // if all are fine then call the removeEndpoint to mark the Location as deleted and not be visible in the foreign service
        try {
            for (var srv : registeredServices) {
                var si = dc.getInstances(srv.getApplicationName());
               removeLocation(si, srv, pKey);
            }
            // if ALL still agree, then send a persistent async message to commit the deletion after the transaction
            eventPublisher.publishEvent(LocationEvent.of(locationOpt.get(), LocationEvent.LocationEventType.DELETED));
            // and finally delete the Location
            repository.delete(locationOpt.get());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            // if any failure occurs, send a persistent async message to release the deletion for everyone
            eventPublisher.publishEvent(new RevokeLocationRemoveCommand(pKey));
        }
    }

    private void askForRemoval(List<ServiceInstance> sis, ReplicaRegistry srv, String pKey) {
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
                        Map.of("pKey", pKey)
                );
                result = response.getBody() != null && response.getBody();
                if (result) {
                    LOGGER.info("Service [{}] allows to remove the Location with pKey [{}]", si.getServiceId(), pKey);
                } else {
                    LOGGER.info("Service [{}] does not allow to remove the Location with pKey [{}]", si.getServiceId(), pKey);
                }
            } catch (Exception e) {
                throw new RemovalNotAllowedException(format("Exception. Removal of Location with pKey [%s] is declined by service [%s]", pKey, si.getServiceId()));
            }
            if (!result) {
                throw new RemovalNotAllowedException(format("Removal of Location with pKey [%s] has been declined by service [%s]", pKey, si.getServiceId()));
            }
        }
    }

    private void removeLocation(List<ServiceInstance> sis, ReplicaRegistry srv, String pKey) {
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
                        Map.of("pKey", pKey)
                );
                LOGGER.info("Service [{}] removed Location with pKey [{}]", si.getServiceId(), pKey);
            } catch (Exception e) {
                throw new RemovalNotAllowedException(format("Exception. Removal of Location with pKey [%s] is declined by service [%s]", pKey, si.getServiceId()));
            }
        }
    }
}
