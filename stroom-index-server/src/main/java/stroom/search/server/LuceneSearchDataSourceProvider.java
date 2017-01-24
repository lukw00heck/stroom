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

package stroom.search.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import stroom.dictionary.shared.DictionaryService;
import stroom.index.server.LuceneVersionUtil;
import stroom.index.shared.Index;
import stroom.index.shared.IndexService;
import stroom.node.server.NodeCache;
import stroom.node.shared.ClientProperties;
import stroom.node.shared.Node;
import stroom.query.CoprocessorMap;
import stroom.query.QueryKey;
import stroom.query.SearchDataSourceProvider;
import stroom.query.SearchResultCollector;
import stroom.query.SearchResultHandler;
import stroom.query.api.ResultRequest;
import stroom.query.api.ExpressionOperator;
import stroom.query.api.Query;
import stroom.query.api.SearchRequest;
import stroom.query.api.TableSettings;
import stroom.search.server.SearchExpressionQueryBuilder.SearchExpressionQuery;
import stroom.task.cluster.ClusterResultCollectorCache;
import stroom.task.server.TaskManager;
import stroom.util.config.StroomProperties;
import stroom.util.logging.StroomLogger;
import stroom.util.shared.ModelStringUtil;
import stroom.util.spring.StroomScope;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Scope(StroomScope.PROTOTYPE)
public class LuceneSearchDataSourceProvider implements SearchDataSourceProvider {
    public static final String ENTITY_TYPE = Index.ENTITY_TYPE;
    private static final StroomLogger LOGGER = StroomLogger.getLogger(LuceneSearchDataSourceProvider.class);
    private static final int SEND_INTERACTIVE_SEARCH_RESULT_FREQUENCY = 500;

    private static final int DEFAULT_MAX_BOOLEAN_CLAUSE_COUNT = 1024;

    private final IndexService indexService;
    private final DictionaryService dictionaryService;
    private final NodeCache nodeCache;
    private final TaskManager taskManager;
    private final ClusterResultCollectorCache clusterResultCollectorCache;

    @Inject
    public LuceneSearchDataSourceProvider(final IndexService indexService, final DictionaryService dictionaryService,
                                          final NodeCache nodeCache, final TaskManager taskManager,
                                          final ClusterResultCollectorCache clusterResultCollectorCache) {
        this.indexService = indexService;
        this.dictionaryService = dictionaryService;
        this.nodeCache = nodeCache;
        this.taskManager = taskManager;
        this.clusterResultCollectorCache = clusterResultCollectorCache;
    }

    @Override
    public SearchResultCollector createCollector(final String sessionId, final String userName, final QueryKey queryKey,
                                                 final SearchRequest searchRequest) {
        // Get the current time in millis since epoch.
        final long nowEpochMilli = System.currentTimeMillis();

        // Get the search.
        final Query query = searchRequest.getQuery();

        // Load the index.
        final Index index = indexService.loadByUuid(query.getDataSource().getUuid());

        // Extract highlights.
        final Set<String> highlights = getHighlights(index, query.getExpression(), nowEpochMilli);

        // This is a new search so begin a new asynchronous search.
        final Node node = nodeCache.getDefaultNode();

        // Create a coprocessor map.
        final Map<String, TableSettings> settingsMap = new HashMap<>();
        for (final ResultRequest resultRequest : searchRequest.getResultRequests()) {
            settingsMap.put(resultRequest.getComponentId(), resultRequest.getTableSettings());
        }

        final CoprocessorMap coprocessorMap = new CoprocessorMap(settingsMap);

        // Create an asynchronous search task.
        final String searchName = "Search '" + queryKey.toString() + "'";
        final AsyncSearchTask asyncSearchTask = new AsyncSearchTask(sessionId, userName, searchName, query, node,
                SEND_INTERACTIVE_SEARCH_RESULT_FREQUENCY, coprocessorMap.getMap(), nowEpochMilli);

        // Create a handler for search results.
        final SearchResultHandler resultHandler = new SearchResultHandler(coprocessorMap, getDefaultTrimSizes());

        // Create the search result collector.
        final ClusterSearchResultCollector searchResultCollector = ClusterSearchResultCollector.create(taskManager,
                asyncSearchTask, node, highlights, clusterResultCollectorCache, resultHandler);

        // Tell the task where results will be collected.
        asyncSearchTask.setResultCollector(searchResultCollector);

        return searchResultCollector;
    }

    private Integer[] getDefaultTrimSizes() {
        try {
            final String value = StroomProperties.getProperty(ClientProperties.MAX_RESULTS);
            if (value != null) {
                final String[] parts = value.split(",");
                final Integer[] arr = new Integer[parts.length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = Integer.valueOf(parts[i].trim());
                }
                return arr;
            }
        } catch (final Exception e) {
            LOGGER.warn(e.getMessage());
        }

        return null;
    }

    /**
     * Compiles the query, extracts terms and then returns them for use in hit
     * highlighting.
     */
    private Set<String> getHighlights(final Index index, final ExpressionOperator expression, final long nowEpochMilli) {
        Set<String> highlights = Collections.emptySet();

        try {
            // Create a map of index fields keyed by name.
            final IndexFieldsMap indexFieldsMap = new IndexFieldsMap(index.getIndexFieldsObject());
            // Parse the query.
            final SearchExpressionQueryBuilder searchExpressionQueryBuilder = new SearchExpressionQueryBuilder(
                    dictionaryService, indexFieldsMap, getMaxBooleanClauseCount(), nowEpochMilli);
            final SearchExpressionQuery query = searchExpressionQueryBuilder
                    .buildQuery(LuceneVersionUtil.CURRENT_LUCENE_VERSION, expression);

            highlights = query.getTerms();
        } catch (final Exception e) {
            LOGGER.debug(e.getMessage(), e);
        }

        return highlights;
    }

    @Override
    public String getEntityType() {
        return ENTITY_TYPE;
    }

    public int getMaxBooleanClauseCount() {
        return StroomProperties.getIntProperty("stroom.search.maxBooleanClauseCount", DEFAULT_MAX_BOOLEAN_CLAUSE_COUNT);
    }
}
