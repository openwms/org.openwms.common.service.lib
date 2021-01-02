package org.openwms.common.location;

import java.math.BigDecimal;

import org.ameba.annotation.TxService;
import org.openwms.common.account.Account;
import org.openwms.common.account.impl.AccountRepository;
import org.openwms.common.transport.TransportUnitType;
import org.openwms.common.transport.TransportUnitTypeService;
import org.openwms.common.transport.TypePlacingRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Generate warehouse for pallet storage with all locations and related entities to database.
 *
 * @author vidompteur
 */
@TxService
public class WarehouseGeneratorService {

   private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseGeneratorService.class);

   private int locationCounter = 0;

   @Autowired
   AccountRepository accountRepository;

   @Autowired
   LocationGroupService locationGroupService;

   @Autowired
   LocationService locationService;

   @Autowired
   LocationTypeService locationTypeService;

   @Autowired
   TransportUnitTypeService transportUnitTypeService;

   /**
    * Create pallet storage for manual operation of receiving and picking.
    *
    * @return number of storage locations created.
    */
   public int generateManualPalletStorage() {

      LOGGER.info("Starting warehouse generation for pallet storage...");

      // create account
      Account account = new Account();
      account.setIdentifier("Maimai");
      account.setDefaultAccount(true);
      account.setName("Maimai Food IMPORT/EXPORT");
      accountRepository.save(account);

      // create storage with 2 aisles and 90 bins each
      LocationType palletBin = new LocationType("PALMWH");
      palletBin.setDescription("Locations for pallet goods.");
      palletBin.setHeight(1940);
      palletBin.setLength(3600);
      palletBin.setWidth(1100);
      locationTypeService.save(palletBin);
      LocationType trapoint = new LocationType("TRAPOINT");
      trapoint.setDescription("Transport point location");
      locationTypeService.save(trapoint);

      LocationGroup palletStorage = new LocationGroup("PAL");
      palletStorage.setDescription("Pallet rack storage area");
      palletStorage.setAccount(account);
      palletStorage.setGroupType("AREA");
      locationGroupService.save(palletStorage);
      palletStorage.addLocationGroup(generateAisleWithLocations("PAISLE1", "Aisle 1", 9, 5, palletBin, palletStorage));
      palletStorage.addLocationGroup(generateAisleWithLocations("PAISLE2", "Aisle 2", 9, 5, palletBin, palletStorage));

      // create receiving and  picking workstations
      LocationGroup workArea = new LocationGroup("WRK");
      workArea.setDescription("Receiving and Shipping area");
      workArea.setAccount(account);
      locationGroupService.save(workArea);
      createLocation(LocationPK.newBuilder().area(workArea.getName()).aisle("WRCV").x("1")
            .y("1").z("1").build(), account, "Receiving I-Point location", trapoint, calculatePlcCode("WRCV", 1, 1, 1));
      createLocation(LocationPK.newBuilder().area(workArea.getName()).aisle("WPIC").x("1")
            .y("1").z("1").build(), account, "Picking outbound workstation", trapoint, calculatePlcCode("WPIC", 1, 1, 1));

      // create transport unit type and assign to location type
      TransportUnitType transportUnitType = new TransportUnitType("PALLET");
      transportUnitType.setDescription("Euro pallet");
      // dimensions in mm
      transportUnitType.setHeight(1800);
      transportUnitType.setWidth(800);
      transportUnitType.setLength(1200);
      // weight in grams
      transportUnitType.setWeightTare(BigDecimal.valueOf(25000));
      transportUnitType.setWeightMax(BigDecimal.valueOf(1000000));
      // allow placing of euro pallets into pallet bins
      TypePlacingRule typePlacingRule = new TypePlacingRule(transportUnitType, palletBin, 1);
      transportUnitType.addTypePlacingRule(typePlacingRule);
      transportUnitTypeService.save(transportUnitType);

      LOGGER.info("Successfully generated {} locations and {} aisles.", locationCounter, palletStorage.getLocationGroups().size());
      return locationCounter;
   }

   private LocationGroup generateAisleWithLocations(String id, String aisleDescription, int maxX, int maxY,
                                                    LocationType locationType,
                                                    LocationGroup palletStorage) {
      LocationGroup aisle = new LocationGroup(id);
      aisle.setDescription(aisleDescription);
      aisle.setAccount(palletStorage.getAccount());
      locationGroupService.save(aisle);

      // add the storage locations
      for (int x = 1; x <= maxX; x++) {
         for (int y = 1; y <= maxY; y++) {
            Location loc =
                  createLocation(LocationPK.newBuilder().area(palletStorage.getName()).aisle(aisle.getName()).x(String.valueOf(x))
                              .y(String.valueOf(y)).z("1").build(), aisle.getAccount(), "Pallet storage rack location",
                        locationType, calculatePlcCode(aisle.getName(), x, y, 1));
            // this also sets aisle on the location
            aisle.addLocation(loc);
            locationCounter++;
         }
      }
      return aisle;
   }

   private Location createLocation(LocationPK locationPK, Account account, String description,
                                   LocationType locationType, String plcCode) {
      Location loc = Location.create(locationPK);
      loc.setAccount(account);
      loc.setLocationType(locationType);
      loc.setDescription(description);
      loc.setPlcCode(plcCode);
      // TODO: the model does not support aisle-sides (left and right), so maybe a LocationGroup is not intended to represent an
      //  aisle. TBC
      locationService.save(loc);
      return loc;
   }

   public String calculatePlcCode(String aisleId, int x, int y, int z) {
      return aisleId + "#" + x + "#" + y + "#" + z;
   }
}
