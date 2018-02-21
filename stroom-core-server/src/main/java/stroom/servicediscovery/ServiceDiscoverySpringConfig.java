/*
 * Copyright 2018 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.servicediscovery;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stroom.properties.StroomPropertyService;

import javax.inject.Singleton;

@Configuration
public class ServiceDiscoverySpringConfig {
    @Bean
    public ServiceDiscoverer serviceDiscoverer(final ServiceDiscoveryManager serviceDiscoveryManager) {
        return new ServiceDiscovererImpl(serviceDiscoveryManager);
    }

    @Bean
    @Singleton
    public ServiceDiscoveryManager serviceDiscoveryManager(final StroomPropertyService stroomPropertyService) {
        return new ServiceDiscoveryManager(stroomPropertyService);
    }

    @Bean
    public ServiceDiscoveryRegistrar serviceDiscoveryRegistrar(final ServiceDiscoveryManager serviceDiscoveryManager,
                                                               final StroomPropertyService stroomPropertyService) {
        return new ServiceDiscoveryRegistrar(serviceDiscoveryManager, stroomPropertyService);
    }
}