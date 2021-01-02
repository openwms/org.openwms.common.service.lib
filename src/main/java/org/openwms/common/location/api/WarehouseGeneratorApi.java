package org.openwms.common.location.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * API for REST interface to trigger warehouse generation.
 *
 * @author vidompteur
 */
@FeignClient(name = "common-service", qualifier = "WarehouseGeneratorApi")
public interface WarehouseGeneratorApi {
   @GetMapping(value = "/warehouse/generatePalletStorage")
   ResponseEntity<Integer> generateManualPalletStorage();
}
