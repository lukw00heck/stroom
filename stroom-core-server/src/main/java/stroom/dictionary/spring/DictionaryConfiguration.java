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

package stroom.dictionary.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import stroom.dictionary.server.DictionaryStore;
import stroom.dictionary.shared.DictionaryDoc;
import stroom.explorer.server.ExplorerActionHandlers;
import stroom.importexport.server.ImportExportActionHandlers;

import javax.inject.Inject;


/**
 * Exclude other configurations that might be found accidentally during a
 * component scan as configurations should be specified explicitly.
 */
@Configuration
@ComponentScan(basePackages = {"stroom.dictionary.server"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class),})
public class DictionaryConfiguration {
    @Inject
    public DictionaryConfiguration(final ExplorerActionHandlers explorerActionHandlers,
                                   final ImportExportActionHandlers importExportActionHandlers,
                                   final DictionaryStore dictionaryStore) {
        explorerActionHandlers.add(9, DictionaryDoc.ENTITY_TYPE, DictionaryDoc.ENTITY_TYPE, dictionaryStore);
        importExportActionHandlers.add(DictionaryDoc.ENTITY_TYPE, dictionaryStore);
    }
}
