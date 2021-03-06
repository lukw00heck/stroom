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

package stroom.dashboard.client;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import stroom.core.client.ContentManager;
import stroom.dashboard.client.main.DashboardPresenter;
import stroom.dashboard.shared.Dashboard;
import stroom.dispatch.client.ClientDispatchAsync;
import stroom.document.client.DocumentPluginEventManager;
import stroom.entity.client.EntityPlugin;
import stroom.entity.client.presenter.DocumentEditPresenter;

public class DashboardPlugin extends EntityPlugin<Dashboard> {
    private final Provider<DashboardPresenter> editorProvider;

    @Inject
    public DashboardPlugin(final EventBus eventBus,
                           final Provider<DashboardPresenter> editorProvider,
                           final ClientDispatchAsync dispatcher,
                           final ContentManager contentManager,
                           final DocumentPluginEventManager entityPluginEventManager) {
        super(eventBus, dispatcher, contentManager, entityPluginEventManager);
        this.editorProvider = editorProvider;
    }

    @Override
    protected DocumentEditPresenter<?, ?> createEditor() {
        return editorProvider.get();
    }

    @Override
    public String getType() {
        return Dashboard.ENTITY_TYPE;
    }
}
