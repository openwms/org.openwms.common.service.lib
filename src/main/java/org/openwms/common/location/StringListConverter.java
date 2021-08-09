/*
 * Copyright 2005-2021 the original author or authors.
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
package org.openwms.common.location;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

/**
 * A StringListConverter is a JPA {@link AttributeConverter} that is able to convert a String into a List of Strings and vice-versa.
 *
 * @author Heiko Scherrer
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    /** Default length of a {@code StringList}. */
    public static final int STRING_LIST_LENGTH = 1024;
    /** Default separator sign of a {@code StringList}. */
    public static final String SEPARATOR = ",";

    /**
     * Return the maximum possible list length, default is {@value}.
     *
     * @return The maximum list length
     */
    protected int getListLength() {
        return STRING_LIST_LENGTH;
    }

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) {
            return null;
        }
        String join = String.join(SEPARATOR, attribute);
        if (join.length() > getListLength()) {
            throw new PersistenceException(format("Length of column is exceeded, actual length is [%s]", join.length()));
        }
        return join;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || "".equals(dbData)) {
            return Collections.emptyList();
        }
        return Arrays.asList(dbData.split(SEPARATOR));
    }
}