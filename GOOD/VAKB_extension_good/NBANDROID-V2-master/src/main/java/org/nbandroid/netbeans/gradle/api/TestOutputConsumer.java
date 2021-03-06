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
package org.nbandroid.netbeans.gradle.api;

import com.android.ddmlib.testrunner.ITestRunListener;
import org.netbeans.api.project.Project;

/**
 * An interface implemented in a module that can process output from test runs.
 *
 * @author radim
 */
public interface TestOutputConsumer {

    /**
     * Creates a test listener.
     *
     * @param project a project
     * @return the corresponding platform, or null if the project was null, not
     * an Android project, or otherwise had a problem
     */
    ITestRunListener createTestListener(Project project);
}
