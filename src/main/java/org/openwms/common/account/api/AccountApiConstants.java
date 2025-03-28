/*
 * Copyright 2005-2025 the original author or authors.
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
package org.openwms.common.account.api;

/**
 * A AccountApiConstants.
 *
 * @author Heiko Scherrer
 */
public final class AccountApiConstants {

    /** API version. */
    public static final String API_VERSION = "v1";
    /** API root to hit Accounts (plural). */
    public static final String API_ACCOUNTS = "/" + API_VERSION + "/accounts";
    /** The ISO format for DateTimes with nine-digits nanoseconds in UTC timezone. */
    public static final String DATETIME_FORMAT_ZULU = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'";

    private AccountApiConstants() {
    }
}
