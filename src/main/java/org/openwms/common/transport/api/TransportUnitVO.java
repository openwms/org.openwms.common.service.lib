/*
 * Copyright 2018 Heiko Scherrer
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
package org.openwms.common.transport.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.openwms.common.ValidationGroups;
import org.openwms.common.location.api.LocationVO;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

/**
 * A TransportUnitVO.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class TransportUnitVO implements Serializable {

    @NotEmpty
    private String barcode;
    private LocationVO actualLocation;
    private String target;
    @NotEmpty(groups = ValidationGroups.TransportUnit.WithTuT.class)
    private String transportUnitType;
    private Integer length;
    private Integer width;
    private Integer height;
    private String actualPlcCode;
    private Date actualLocationDate;

    /*~-------------------- constructors --------------------*/
    @JsonCreator
    protected TransportUnitVO() {
    }

    public TransportUnitVO(@NotEmpty String barcode) {
        this.barcode = barcode;
    }

    public TransportUnitVO(@NotEmpty String barcode, @NotEmpty String transportUnitType) {
        this.barcode = barcode;
        this.transportUnitType = transportUnitType;
    }

    /*~-------------------- accessors --------------------*/
    public void setActualLocationDate(Date actualLocationDate) {
        this.actualLocationDate = actualLocationDate;
    }
}
