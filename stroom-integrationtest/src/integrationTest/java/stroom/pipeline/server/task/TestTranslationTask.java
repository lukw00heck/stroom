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

package stroom.pipeline.server.task;

import org.junit.Assert;
import org.junit.Test;
import stroom.streamstore.server.MockStreamStore;
import stroom.streamstore.shared.Stream;
import stroom.streamstore.shared.StreamType;
import stroom.streamtask.server.StreamProcessorTaskExecutor;
import stroom.test.AbstractProcessIntegrationTest;
import stroom.test.CommonTranslationTest;
import stroom.test.ComparisonHelper;
import stroom.test.StroomPipelineTestFileUtil;
import stroom.util.shared.Severity;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

public class TestTranslationTask extends AbstractProcessIntegrationTest {
    private static final int N3 = 3;
    private static final int N4 = 4;

    private static final String DIR = "TestTranslationTask/";

    @Resource
    private MockStreamStore streamStore;
    @Resource
    private CommonTranslationTest commonPipelineTest;

    /**
     * Tests Task with a valid resource and feed.
     *
     * @throws Exception Could be thrown.
     */
    @Test
    public void testBothValid() throws Exception {
        commonPipelineTest.setup();

        final List<StreamProcessorTaskExecutor> results = commonPipelineTest.processAll();
        Assert.assertEquals(N4, results.size());
        for (final StreamProcessorTaskExecutor result : results) {
            final PipelineStreamProcessor processor = (PipelineStreamProcessor) result;
            Assert.assertTrue(result.toString(), processor.getWritten() > 0);
            Assert.assertTrue(result.toString(), processor.getRead() <= processor.getWritten());
            Assert.assertEquals(result.toString(), 0, processor.getMarkerCount(Severity.SEVERITIES));
        }

        final Path inputDir = StroomPipelineTestFileUtil.getTestResourcesDir().resolve(DIR);
        final Path outputDir = StroomPipelineTestFileUtil.getTestOutputDir().resolve(DIR);

        for (final Stream stream : streamStore.getFileData().keySet()) {
            if (stream.getStreamType().equals(StreamType.EVENTS)) {
                final byte[] data = streamStore.getFileData().get(stream).get(stream.getStreamType().getId());

                // Write the actual XML out.
                final OutputStream os = StroomPipelineTestFileUtil.getOutputStream(outputDir, "TestTranslationTask.out");
                os.write(data);
                os.flush();
                os.close();

                ComparisonHelper.compareFiles(inputDir.resolve("TestTranslationTask.out"),
                        outputDir.resolve("TestTranslationTask.out"));
            }
        }

        // Make sure 26 records were written.
        Assert.assertEquals(26, ((PipelineStreamProcessor) results.get(N3)).getWritten());
    }

    /**
     * Tests Task with an invalid resource and valid feed.
     *
     * @throws Exception Could be thrown.
     */
    @Test
    public void testInvalidResource() throws Exception {
        commonPipelineTest.setup(CommonTranslationTest.FEED_NAME, CommonTranslationTest.INVALID_RESOURCE_NAME);

        final List<StreamProcessorTaskExecutor> results = commonPipelineTest.processAll();
        Assert.assertEquals(N4, results.size());

        // Make sure no records were written.
        Assert.assertEquals(0, ((PipelineStreamProcessor) results.get(N3)).getWritten());
    }
}
