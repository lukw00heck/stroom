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

package stroom.task.server;

import org.springframework.stereotype.Component;
import stroom.security.SecurityContext;
import stroom.util.shared.Task;
import stroom.util.shared.ThreadPool;
import stroom.util.task.ServerTask;

import javax.inject.Inject;
import java.util.concurrent.Executor;

@Component
public class ExecutorProviderImpl implements ExecutorProvider {
    private final TaskManager taskManager;
    private final SecurityContext securityContext;

    @Inject
    public ExecutorProviderImpl(final TaskManager taskManager, final SecurityContext securityContext) {
        this.taskManager = taskManager;
        this.securityContext = securityContext;
    }

    @Override
    public Executor getExecutor() {
        return command -> {
            final Task<?> parentTask = CurrentTaskState.currentTask();
            final GenericServerTask genericServerTask = GenericServerTask.create(parentTask, getUserToken(parentTask), getTaskName(parentTask, "Generic Task"), null);
            genericServerTask.setRunnable(command);
            taskManager.execAsync(genericServerTask);
        };
    }

    @Override
    public Executor getExecutor(final ThreadPool threadPool) {
        return command -> {
            final Task<?> parentTask = CurrentTaskState.currentTask();
            final GenericServerTask genericServerTask = GenericServerTask.create(parentTask, getUserToken(parentTask), getTaskName(parentTask, threadPool.getName()), null);
            genericServerTask.setRunnable(command);
            taskManager.execAsync(genericServerTask, threadPool);
        };
    }

    private String getTaskName(final Task<?> parentTask, final String defaultName) {
        if (parentTask != null && parentTask.getTaskName() != null) {
            return parentTask.getTaskName();
        }

        return defaultName;
    }

    private String getUserToken(final Task<?> parentTask) {
        if (parentTask != null && parentTask.getUserToken() != null) {
            return parentTask.getUserToken();
        }

        return ServerTask.INTERNAL_PROCESSING_USER_TOKEN;
    }
}