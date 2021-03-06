/*
 * Copyright 2017 Crown Copyright
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import stroom.explorer.server.ExplorerActionHandlers;
import stroom.feed.server.FeedService;
import stroom.feed.shared.Feed;
import stroom.importexport.server.ImportExportActionHandlers;
import stroom.util.spring.StroomBeanStore;

import javax.inject.Inject;

/**
 * Defines the application context configuration for the server module.
 */
@Configuration
@EnableAspectJAutoProxy
public class ServerConfiguration {
    @Inject
    public ServerConfiguration(final ExplorerActionHandlers explorerActionHandlers,
                               final ImportExportActionHandlers importExportActionHandlers,
                               final FeedService feedService) {
        explorerActionHandlers.add(3, Feed.ENTITY_TYPE, Feed.ENTITY_TYPE, feedService);
        importExportActionHandlers.add(Feed.ENTITY_TYPE, feedService);
    }

    @Bean
    public StroomBeanStore stroomBeanStore() {
        return new StroomBeanStore();
    }
}
