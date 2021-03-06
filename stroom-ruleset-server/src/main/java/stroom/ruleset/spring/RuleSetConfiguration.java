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
 *
 */

package stroom.ruleset.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import stroom.explorer.server.ExplorerActionHandlers;
import stroom.importexport.server.ImportExportActionHandlers;
import stroom.ruleset.server.RuleSetService;
import stroom.ruleset.shared.RuleSet;

import javax.inject.Inject;

/**
 * Exclude other configurations that might be found accidentally during a
 * component scan as configurations should be specified explicitly.
 */
@Configuration
@ComponentScan(basePackages = {"stroom.ruleset.server"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class),})
public class RuleSetConfiguration {
    @Inject
    public RuleSetConfiguration(final ExplorerActionHandlers explorerActionHandlers,
                                final ImportExportActionHandlers importExportActionHandlers,
                                final RuleSetService ruleSetService) {
        explorerActionHandlers.add(100, RuleSet.DOCUMENT_TYPE, "Rule Set", ruleSetService);
        importExportActionHandlers.add(RuleSet.DOCUMENT_TYPE, ruleSetService);
    }
}
