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

package stroom.dashboard.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import stroom.dashboard.shared.FindQueryCriteria;
import stroom.dashboard.shared.QueryEntity;
import stroom.entity.server.AutoMarshal;
import stroom.entity.server.DocumentEntityServiceImpl;
import stroom.entity.server.QueryAppender;
import stroom.entity.server.util.FieldMap;
import stroom.entity.server.util.HqlBuilder;
import stroom.entity.server.util.SqlBuilder;
import stroom.entity.server.util.StroomEntityManager;
import stroom.importexport.server.ImportExportHelper;
import stroom.logging.DocumentEventLog;
import stroom.security.SecurityContext;
import stroom.util.spring.StroomSpringProfiles;

import javax.inject.Inject;
import java.util.List;

@Profile(StroomSpringProfiles.PROD)
@Component("queryService")
@Transactional
@AutoMarshal
public class QueryServiceImpl extends DocumentEntityServiceImpl<QueryEntity, FindQueryCriteria> implements QueryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryServiceImpl.class);
    private final StroomEntityManager entityManager;
    private final SecurityContext securityContext;

    @Inject
    QueryServiceImpl(final StroomEntityManager entityManager,
                     final ImportExportHelper importExportHelper,
                     final SecurityContext securityContext) {
        super(entityManager, importExportHelper, securityContext);
        this.entityManager = entityManager;
        this.securityContext = securityContext;
    }

    @Override
    public Class<QueryEntity> getEntityClass() {
        return QueryEntity.class;
    }

    @Override
    public FindQueryCriteria createCriteria() {
        return new FindQueryCriteria();
    }

    @Override
    public QueryEntity create(final String name) throws RuntimeException {
        final QueryEntity entity = super.create(name);

        // Create the initial user permissions for this new document.
        securityContext.addDocumentPermissions(null, null, entity.getType(), entity.getUuid(), true);

        return entity;
    }

    @Override
    public void clean(final String user, final boolean favourite, final Integer oldestId, final long oldestCrtMs) {
        try {
            LOGGER.debug("Deleting old rows");

            final SqlBuilder sql = new SqlBuilder();
            sql.append("DELETE");
            sql.append(" FROM ");
            sql.append(QueryEntity.TABLE_NAME);
            sql.append(" WHERE ");
            sql.append(QueryEntity.CREATE_USER);
            sql.append(" = ");
            sql.arg(user);
            sql.append(" AND ");
            sql.append(QueryEntity.FAVOURITE);
            sql.append(" = ");
            sql.arg(favourite);
            sql.append(" AND ");

            if (oldestId != null) {
                sql.append("(");
                sql.append(QueryEntity.ID);
                sql.append(" <= ");
                sql.arg(oldestId);
                sql.append(" OR ");
                sql.append(QueryEntity.CREATE_TIME);
                sql.append(" < ");
                sql.arg(oldestCrtMs);
                sql.append(")");
            } else {
                sql.append(QueryEntity.CREATE_TIME);
                sql.append(" < ");
                sql.arg(oldestCrtMs);
            }

            final long rows = entityManager.executeNativeUpdate(sql);
            LOGGER.debug("Deleted " + rows + " rows");

        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getUsers(final boolean favourite) {
        final SqlBuilder sql = new SqlBuilder();
        sql.append("SELECT ");
        sql.append(QueryEntity.CREATE_USER);
        sql.append(" FROM ");
        sql.append(QueryEntity.TABLE_NAME);
        sql.append(" WHERE ");
        sql.append(QueryEntity.FAVOURITE);
        sql.append(" = ");
        sql.arg(favourite);
        sql.append(" GROUP BY ");
        sql.append(QueryEntity.CREATE_USER);
        sql.append(" ORDER BY ");
        sql.append(QueryEntity.CREATE_USER);

        @SuppressWarnings("unchecked") final List<String> list = entityManager.executeNativeQueryResultList(sql);

        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getOldestId(final String user, final boolean favourite, final int retain) {
        final SqlBuilder sql = new SqlBuilder();
        sql.append("SELECT");
        sql.append(" ID");
        sql.append(" FROM ");
        sql.append(QueryEntity.TABLE_NAME);
        sql.append(" WHERE ");
        sql.append(QueryEntity.CREATE_USER);
        sql.append(" = ");
        sql.arg(user);
        sql.append(" AND ");
        sql.append(QueryEntity.FAVOURITE);
        sql.append(" = ");
        sql.arg(favourite);
        sql.append(" ORDER BY ID DESC LIMIT 1 OFFSET ");
        sql.arg(retain);

        @SuppressWarnings("unchecked") final List<Integer> list = entityManager.executeNativeQueryResultList(sql);

        if (list.size() == 1) {
            return list.get(0);
        }

        return null;
    }

    @Override
    protected void checkUpdatePermission(final QueryEntity entity) {
        // Ignore.
    }

//    @Override
//    public void appendCriteria(final List<BaseAdvancedQueryItem> items, final FindQueryCriteria criteria) {
//        CriteriaLoggingUtil.appendLongTerm(items, "dashboardId", criteria.getDashboardId());
//        CriteriaLoggingUtil.appendStringTerm(items, "queryId", criteria.getQueryId());
//        super.appendCriteria(items, criteria);
//    }

    @Override
    protected QueryAppender<QueryEntity, FindQueryCriteria> createQueryAppender(final StroomEntityManager entityManager) {
        return new QueryQueryAppender(entityManager);
    }

    @Override
    public String getNamePattern() {
        // Unnamed queries are valid.
        return null;
    }

    @Override
    protected FieldMap createFieldMap() {
        return super.createFieldMap()
                .add(FindQueryCriteria.FIELD_TIME, QueryEntity.CREATE_TIME, "createTime");
    }

    private static class QueryQueryAppender extends QueryAppender<QueryEntity, FindQueryCriteria> {
        private final QueryEntityMarshaller marshaller;

        QueryQueryAppender(final StroomEntityManager entityManager) {
            super(entityManager);
            marshaller = new QueryEntityMarshaller();
        }

        @Override
        protected void appendBasicCriteria(final HqlBuilder sql, final String alias, final FindQueryCriteria criteria) {
            super.appendBasicCriteria(sql, alias, criteria);

            if (criteria.getFavourite() != null) {
                sql.appendValueQuery(alias + ".favourite", criteria.getFavourite());
            }

            sql.appendValueQuery(alias + ".dashboardId", criteria.getDashboardId());
            sql.appendValueQuery(alias + ".queryId", criteria.getQueryId());
        }

        @Override
        protected void preSave(final QueryEntity entity) {
            super.preSave(entity);
            marshaller.marshal(entity);
        }

        @Override
        protected void postLoad(final QueryEntity entity) {
            marshaller.unmarshal(entity);
            super.postLoad(entity);
        }
    }
}
