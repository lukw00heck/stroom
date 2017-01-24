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

package stroom.query;

import stroom.dashboard.expression.Generator;
import stroom.query.api.Field;
import stroom.query.api.OffsetRange;
import stroom.query.api.Result;
import stroom.query.api.ResultRequest;
import stroom.query.api.Row;
import stroom.query.api.TableResult;
import stroom.query.api.TableResultRequest;
import stroom.query.format.FieldFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TableComponentResultCreator implements ComponentResultCreator {
    private final FieldFormatter fieldFormatter;
    private volatile Field[] latestFields;

    public TableComponentResultCreator(final FieldFormatter fieldFormatter) {
        this.fieldFormatter = fieldFormatter;
    }

    @Override
    public Result create(final ResultStore resultStore, final ResultRequest componentResultRequest) {
        final TableResultRequest resultRequest = (TableResultRequest) componentResultRequest;
        final List<Row> resultList = new ArrayList<>();
        int offset = 0;
        int length = 0;
        int totalResults = 0;
        String error = null;

        try {
            final OffsetRange range = resultRequest.getRequestedRange();

            Set<String> openGroups = Collections.emptySet();
            if (resultRequest.getOpenGroups() != null) {
                openGroups = Arrays.stream(resultRequest.getOpenGroups()).collect(Collectors.toSet());
            }

            offset = range.getOffset().intValue();
            length = range.getLength().intValue();
            latestFields = resultRequest.getTableSettings().getFields();
            totalResults = addTableResults(resultStore, latestFields, offset, length, openGroups, resultList, null, 0,
                    0);
        } catch (final Exception e) {
            error = e.getMessage();
        }

        final TableResult tableResult = new TableResult();
        tableResult.setRows((Row[]) resultList.toArray());
        tableResult.setResultRange(new OffsetRange(offset, resultList.size()));
        tableResult.setTotalResults(totalResults);
        tableResult.setError(error);

        return tableResult;
    }

    private int addTableResults(final ResultStore resultStore, final Field[] fields, final int offset,
                                final int length, final Set<String> openGroups, final List<Row> resultList, final String parentKey,
                                final int depth, final int position) {
        int pos = position;
        // Get top level items.
        final Items<Item> items = resultStore.getChildMap().get(parentKey);
        if (items != null) {
            for (final Item item : items) {
                if (pos >= offset && resultList.size() < length) {
                    // Convert all list into fully resolved objects evaluating
                    // functions where necessary.
                    final String[] values = new String[item.getValues().length];
                    for (int i = 0; i < fields.length; i++) {
                        final Field field = fields[i];

                        if (item.getValues().length > i) {
                            final Object o = item.getValues()[i];
                            if (o != null) {
                                // Convert all list into fully resolved
                                // objects evaluating functions where necessary.
                                Object val = o;
                                if (o instanceof Generator) {
                                    final Generator generator = (Generator) o;
                                    val = generator.eval();
                                }

                                if (val != null) {
                                    final String formatted = fieldFormatter.format(field, val);
                                    values[i] = formatted;
                                }
                            }
                        }
                    }

                    resultList.add(new Row(item.getGroupKey(), values, item.getDepth()));
                }

                // Increment the position.
                pos++;

                // Add child results if a node is open.
                if (item.getGroupKey() != null && openGroups != null && openGroups.contains(item.getGroupKey())) {
                    pos = addTableResults(resultStore, fields, offset, length, openGroups, resultList,
                            item.getGroupKey(), depth + 1, pos);
                }
            }
        }
        return pos;
    }

    public Field[] getFields() {
        return latestFields;
    }
}
