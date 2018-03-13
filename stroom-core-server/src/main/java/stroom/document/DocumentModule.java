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

package stroom.document;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import stroom.entity.EntityServiceBeanRegistry;
import stroom.logging.DocumentEventLog;
import stroom.task.TaskHandler;
import stroom.util.spring.StroomScope;

public class DocumentModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DocumentService.class).to(DocumentServiceImpl.class);

        final Multibinder<TaskHandler> taskHandlerBinder = Multibinder.newSetBinder(binder(), TaskHandler.class);
        taskHandlerBinder.addBinding().to(stroom.document.DocumentServiceReadHandler.class);
        taskHandlerBinder.addBinding().to(stroom.document.DocumentServiceWriteHandler.class);
    }

//    @Bean
//    @Scope(value = StroomScope.TASK)
//    public DocumentServiceReadHandler documentServiceReadHandler(final DocumentService documentService,
//                                                                 final DocumentEventLog documentEventLog) {
//        return new DocumentServiceReadHandler(documentService, documentEventLog);
//    }
//
//    @Bean
//    @Scope(value = StroomScope.TASK)
//    public DocumentServiceWriteHandler documentServiceWriteHandler(final DocumentService documentService,
//                                                                   final DocumentEventLog documentEventLog) {
//        return new DocumentServiceWriteHandler(documentService, documentEventLog);
//    }
}