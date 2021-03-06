/*
 * Copyright 2016 Crown Copyright
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

package stroom.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Defines the component scanning required for the server module.
 * <p>
 * Defined separately from the main configuration so it can be easily
 * overridden.
 */
@Configuration
@ComponentScan(basePackages = {
        "stroom.cache",
        "stroom.apiclients",
        "stroom.cluster",
        "stroom.datafeed",
        "stroom.datasource",
        "stroom.db",
        "stroom.dispatch",
        "stroom.document.server",
        "stroom.docstore.server",
        "stroom.entity",
        "stroom.feed.server",
        "stroom.folder",
        "stroom.importexport",
        "stroom.internalstatistics",
        "stroom.io",
        "stroom.jobsystem",
        "stroom.connectors",
        "stroom.connectors.kafka",
        "stroom.lifecycle",
        "stroom.logging",
        "stroom.node",
        "stroom.pipeline",
        "stroom.policy",
        "stroom.pool",
        "stroom.process",
        "stroom.proxy",
        "stroom.query",
        "stroom.resource",
        "stroom.servicediscovery",
        "stroom.servlet",
        "stroom.spring",
        "stroom.streamstore",
        "stroom.streamtask",
        "stroom.task",
        "stroom.test",
        "stroom.upgrade",
        "stroom.util",
        "stroom.volume",
        "stroom.xmlschema"
}, excludeFilters = {
        // Exclude other configurations that might be found accidentally during
        // a component scan as configurations should be specified explicitly.
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class),})
public class ServerComponentScanConfiguration {
}
