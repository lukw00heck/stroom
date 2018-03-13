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

package stroom.entity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stroom.entity.EntityServiceImplTestTransactionHelper;
import stroom.feed.FeedService;

import javax.inject.Named;

@Configuration
public class EntityTestSpringConfig {
    @Bean
    public EntityServiceImplTestTransactionHelper entityServiceImplTestTransactionHelper(final FeedService feedService, @Named("cachedFeedService") final FeedService cachedFeedService) {
        return new EntityServiceImplTestTransactionHelper(feedService, cachedFeedService);
    }
}