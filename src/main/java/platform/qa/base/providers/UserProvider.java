/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.base.providers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import platform.qa.configuration.MasterConfig;
import platform.qa.configuration.RegistryConfig;
import platform.qa.entities.User;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Provide users for registry
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProvider {
    private static UserProvider instance;
    private final RegistryConfig registryConfig = MasterConfig.getInstance().getRegistryConfig();

    private AtomicReference<User> dataUser = new AtomicReference<>();
    private AtomicReference<User> registryUser = new AtomicReference<>();


    public User getDataUser() {
        return dataUser.updateAndGet((user) -> {
            User currentUser = registryConfig.initUser(user, "auto-user-data");
            return registryConfig.refreshUserToken(currentUser);
        });
    }

    public User getUserByName(String userName) {
        return registryUser.updateAndGet((user) -> {
            User currentUser = registryConfig.initUser(user, userName);
            return registryConfig.refreshUserToken(currentUser);
        });
    }

    public static UserProvider getInstance() {
        if (instance == null) {
            instance = new UserProvider();
        }
        return instance;
    }


}
