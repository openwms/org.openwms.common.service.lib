/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2014 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.location;

import java.util.List;
import java.util.stream.Stream;

import org.ameba.LoggingCategories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

/**
 * A LocationConfig is a Spring managed configuration class the defines a bean to load a few Locations upfront.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Configuration
class LocationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCategories.BOOT);

    @Profile("H2")
    @Bean
    @DependsOn("locationGroupRunner")
    CommandLineRunner locationRunner(LocationRepository lr, LocationGroupRepository lgr) {
        return args -> {
            lr.deleteAll();
            List<LocationGroup> lgs = lgr.findAll();
            if (lgs == null || lgs.isEmpty()) {
                LOGGER.warn("No LocationGroups exist, therefore no Locations will be inserted !!");
                return;
            }
            Stream.of("INIT/0000/0000/0000/0000,ERR_/0000/0000/0000/0000,EXT_/0000/0000/0000/0000,AKL_/0001/0000/0000/0000".split(","))
                    .forEach(x -> {
                        Location l = new Location(LocationPK.fromString(x));
                        l.setLocationGroup(lgs.get(0));
                        lr.save(l);
                    });
        };
    }
}
