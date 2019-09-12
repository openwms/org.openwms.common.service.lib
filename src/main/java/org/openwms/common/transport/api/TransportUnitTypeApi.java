package org.openwms.common.transport.api;

import org.openwms.common.CommonConstants;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "common-service", qualifier = "transportUnitTypeApi")
public interface TransportUnitTypeApi {

    /**
     * Find and return a {@code TransportUnitType} identified by its {@code type}.
     *
     * @param type The unique identifier
     * @return The instance or the implementation may return a 404-Not Found
     */
    @GetMapping(value = CommonConstants.API_TRANSPORT_UNIT_TYPES, params = {"type"})
    @ResponseBody
    @Cacheable("transportUnitTypes")
    TransportUnitTypeVO findTransportUnitType(
            @RequestParam("type") String type
    );

    /**
     * Find and return all {@code TransportUnitType}s.
     *
     * @return All instances or an empty list, never {@literal null}
     */
    @GetMapping(value = CommonConstants.API_TRANSPORT_UNIT_TYPES)
    @ResponseBody
    @Cacheable("transportUnitTypes")
    List<TransportUnitTypeVO> findTransportUnitTypes();

}
