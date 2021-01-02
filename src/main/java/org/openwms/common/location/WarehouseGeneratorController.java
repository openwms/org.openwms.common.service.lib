package org.openwms.common.location;

import org.openwms.core.http.AbstractWebController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest interface for warehouse generation with {@link WarehouseGeneratorService}.
 * Use GET for easier execution in browser.
 *
 * @author vidompteur
 */
@RestController
public class WarehouseGeneratorController extends AbstractWebController {

   private WarehouseGeneratorService service;

   public WarehouseGeneratorController(WarehouseGeneratorService service) {
      this.service = service;
   }

   @GetMapping(value = "/warehouse/generatePalletStorage")
   public ResponseEntity<Integer> generateManualPalletStorage() {
      int locationCount = service.generateManualPalletStorage();
      return ResponseEntity.ok(locationCount);
   }
}
