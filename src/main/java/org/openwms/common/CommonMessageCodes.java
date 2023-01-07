/*
 * Copyright 2005-2023 the original author or authors.
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
package org.openwms.common;

/**
 * A CommonMessageCodes.
 *
 * @author Heiko Scherrer
 */
public final class CommonMessageCodes {

    public static final String ACCOUNT_NOT_FOUND = "owms.common.common.account.notFound";
    public static final String ACCOUNT_NOT_FOUND_BY_PKEY = "owms.common.common.account.notFoundByPKey";
    public static final String ACCOUNT_NOT_FOUND_BY_ID = "owms.common.common.account.notFoundByID";
    public static final String ACCOUNT_NO_DEFAULT = "owms.common.common.account.noDefaultAccount";

    public static final String LOCATION_GROUP_NOT_FOUND_BY_PKEY = "owms.common.common.lg.notFoundByPKey";
    public static final String LOCATION_GROUP_NOT_FOUND = "owms.common.common.lg.notFoundByName";

    public static final String LOCATION_TYPE_NOT_FOUND = "owms.common.common.lt.notFoundByType";
    public static final String LOCATION_TYPE_NOT_FOUND_BY_PKEY = "owms.common.common.lt.notFoundByPKey";

    public static final String LOCATION_ID_INVALID = "owms.common.common.loc.invalidName";
    public static final String LOCATION_ID_EXISTS = "owms.common.common.loc.idExists";
    public static final String LOCATION_NOT_FOUND_BY_ID = "owms.common.common.loc.notFoundById";
    public static final String LOCATION_NOT_FOUND_BY_PKEY = "owms.common.common.loc.notFoundByPKey";
    public static final String LOCATION_NOT_FOUND_BY_ERP_CODE = "owms.common.common.loc.notFoundByErpCode";
    public static final String LOCATION_NOT_FOUND_BY_PLC_CODE = "owms.common.common.loc.notFoundByPlcCode";
    public static final String LOCK_MODE_UNSUPPORTED = "owms.common.common.loc.lockModeUnsupported";
    public static final String LOCK_TYPE_UNSUPPORTED = "owms.common.common.loc.lockTypeUnsupported";

    public static final String TARGET_NOT_SUPPORTED = "owms.common.common.target.unsupportedType";

    public static final String TU_BARCODE_NOT_FOUND = "owms.common.common.tu.notFoundByBK";
    public static final String TU_BARCODE_MISSING = "owms.common.common.tu.bkMissing";
    public static final String TU_EXISTS = "owms.common.common.tu.alreadyExists";

    public static final String TRANSPORT_UNIT_TYPE_NOT_FOUND = "owms.common.common.tut.notFoundByName";
    public static final String TRANSPORT_UNIT_TYPE_NOT_FOUND_BY_PKEY = "owms.common.common.tut.notFoundByPKey";

    public static final String MSG_TU_MOVED = "owms.common.common.msg.tu.moved";

    private CommonMessageCodes() {
    }
}
