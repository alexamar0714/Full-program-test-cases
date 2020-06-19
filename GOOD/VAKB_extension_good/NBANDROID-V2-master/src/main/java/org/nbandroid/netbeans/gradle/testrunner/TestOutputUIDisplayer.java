/*
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
package org.nbandroid.netbeans.gradle.testrunner;

import com.android.ddmlib.testrunner.ITestRunListener;
import com.google.common.base.Preconditions;
import java.util.logging.Logger;
import org.nbandroid.netbeans.gradle.api.TestOutputConsumer;
import org.netbeans.api.project.Project;

/**
 * A factory for test run listener that send output to GSF testrunner.
 */
class TestOutputUIDisplayer implements TestOutputConsumer {

    private static final Logger LOG = Logger.getLogger(TestOutputUIDisplayer.class.getName());

    private final Project project;

    public TestOutputUIDisplayer(Project project) {
        this.project = Preconditions.checkNotNull(project);
    }

    @Override
    public ITestRunListener createTestListener(Project p) {
        return new TestRunListener(project);
    }
}
