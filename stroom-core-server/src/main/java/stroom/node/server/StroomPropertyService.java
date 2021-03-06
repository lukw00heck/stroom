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

package stroom.node.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A service that can be injected with spring that caches and delegates property
 * lookups to StroomProperties.
 */
public interface StroomPropertyService {
    /**
     * @param name The property name or key
     * @return The property value which could be null or an empty string
     */
    String getProperty(String name);

    String getProperty(String name, String defaultValue);

    int getIntProperty(String propertyName, int defaultValue);

    long getLongProperty(String propertyName, long defaultValue);

    boolean getBooleanProperty(String propertyName, boolean defaultValue);

    default List<String> getCsvProperty(final String listProp) {
        final List<String> values = new ArrayList<>();

        final String keyList = getProperty(listProp);
        if (null != keyList) {
            final String[] keys = keyList.split(",");
            values.addAll(Arrays.asList(keys));
        }

        return values;
    }

    default Map<String, String> getLookupTable(final String listProp, final String base) {
        final Map<String, String> result = new HashMap<>();

        getCsvProperty(listProp).forEach(key -> {
            final String value = getProperty(base + key);
            result.put(key, value);
        });

        return result;
    }
}
