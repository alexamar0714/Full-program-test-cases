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
package org.nbandroid.netbeans.gradle.launch;

import com.android.ddmlib.IDevice;
import org.netbeans.api.project.Project;

/**
 * Strategy used to launch code on device. Examples are: no launch, activity
 * launch, test launch.
 *
 * @author radim
 */
public interface LaunchAction {

    boolean doLaunch(LaunchInfo launchInfo, IDevice device, Project project);
}
