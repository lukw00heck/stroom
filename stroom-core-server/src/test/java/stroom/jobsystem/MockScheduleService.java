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

package stroom.jobsystem;

import stroom.jobsystem.shared.JobNode.JobType;
import stroom.jobsystem.shared.ScheduledTimes;

public class MockScheduleService implements ScheduleService {
    @Override
    public ScheduledTimes getScheduledTimes(final JobType jobType, final Long scheduleReferenceTime,
                                            final Long lastExecutedTime, final String expression) throws RuntimeException {
        return null;
    }
}