/*
 * Copyright 2005-2024 the original author or authors.
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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

/**
 * A LocationRemovalManager.
 *
 * @author Heiko Scherrer
 */
public interface LocationRemovalManager {

    /**
     * Try to delete a {@link Location}.
     *
     * @param pKey The persistent key of the Location to be deleted.
     */
    void tryDelete(@NotBlank String pKey);

    /**
     * Delete {@link Location}s.
     *
     * @param locations The Locations to be deleted.
     */
    void deleteAll(@NotNull Collection<Location> locations);

    /**
     * Checks if the {@link Location}s with the given {@code pKey}s are allowed to be deleted.
     *
     * @param pKeys The persistent keys of the Locations to be checked.
     * @return {@literal true} if the Locations are allowed to be deleted, otherwise {@literal false}.
     */
    boolean allowedToDelete(@NotNull Collection<String> pKeys);

    /**
     * Mark {@link Location}s for upcoming deletion.
     *
     * @param pKeys The persistent keys of the Locations to be marked for deletion.
     */
    void markForDeletion(@NotNull Collection<String> pKeys);
}
