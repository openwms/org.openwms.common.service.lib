/*
 * Copyright 2005-2020 the original author or authors.
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
package org.openwms.common.transport.impl;

import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.dozer.DozerConverter;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.transport.TransportUnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * A TransportUnitTypeConverter.
 *
 * @author Heiko Scherrer
 */
@Configurable
public class TransportUnitTypeConverter extends DozerConverter<String, TransportUnitType> {

    @Autowired
    private TransportUnitTypeRepository repository;
    @Autowired
    private Translator translator;

    public TransportUnitTypeConverter() {
        super(String.class, TransportUnitType.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransportUnitType convertTo(String source, TransportUnitType destination) {
        return repository.findByType(source).orElseThrow(() -> new NotFoundException(translator, CommonMessageCodes.TRANSPORT_UNIT_TYPE_NOT_FOUND, new String[]{source}, source));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertFrom(TransportUnitType source, String destination) {
        return source.getType();
    }
}
