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

package stroom.feed;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class MetaMapFactory {
    public static MetaMap cloneAllowable(final MetaMap in) {
        final MetaMap metaMap = new MetaMap();
        metaMap.putAll(in);
        metaMap.removeAll(StroomHeaderArguments.HEADER_CLONE_EXCLUDE_SET);
        return metaMap;
    }

    public static MetaMap create(final HttpServletRequest httpServletRequest) {
        MetaMap metaMap = new MetaMap();
        addAllHeaders(httpServletRequest, metaMap);
        addAllQueryString(httpServletRequest, metaMap);

        return metaMap;
    }

    @SuppressWarnings("unchecked")
    private static void addAllHeaders(HttpServletRequest httpServletRequest, MetaMap metaMap) {
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            metaMap.put(header, httpServletRequest.getHeader(header));
        }
    }

    private static void addAllQueryString(HttpServletRequest httpServletRequest, MetaMap metaMap) {
        String queryString = httpServletRequest.getQueryString();
        if (queryString != null) {
            StringTokenizer st = new StringTokenizer(httpServletRequest.getQueryString(), "&");
            while (st.hasMoreTokens()) {
                String pair = st.nextToken();
                int pos = pair.indexOf('=');
                if (pos != -1) {
                    String key = pair.substring(0, pos);
                    String val = pair.substring(pos + 1, pair.length());

                    metaMap.put(key, val);
                }
            }
        }
    }
}
