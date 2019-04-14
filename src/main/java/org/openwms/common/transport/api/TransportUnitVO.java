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

import org.openwms.common.location.api.LocationVO;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A TransportUnitVO.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class TransportUnitVO implements Serializable {

    private String barcode;
    private LocationVO actualLocation;
    private String target;
    private String transportUnitType;
    private String length;
    private String width;
    private String height;
    private String actualPlcCode;
    private Date actualLocationDate;
    private Map<Date, UnitErrorVO> errors = new HashMap<>();

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public LocationVO getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(LocationVO actualLocation) {
        this.actualLocation = actualLocation;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTransportUnitType() {
        return transportUnitType;
    }

    public void setTransportUnitType(String transportUnitType) {
        this.transportUnitType = transportUnitType;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getActualPlcCode() {
        return actualPlcCode;
    }

    public void setActualPlcCode(String actualPlcCode) {
        this.actualPlcCode = actualPlcCode;
    }

    public Date getActualLocationDate() {
        return actualLocationDate;
    }

    public void setActualLocationDate(Date actualLocationDate) {
        this.actualLocationDate = actualLocationDate;
    }

    public Map<Date, UnitErrorVO> getErrors() {
        return errors;
    }

    public void setErrors(Map<Date, UnitErrorVO> errors) {
        this.errors = errors;
    }
}
