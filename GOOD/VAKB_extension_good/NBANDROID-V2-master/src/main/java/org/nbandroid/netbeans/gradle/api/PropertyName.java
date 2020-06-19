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

/**
 *
 * @author radim
 */
public enum PropertyName {

    JAR_LIBS_DIR("jar.libs.dir"),
    SRC_DIR("source.dir"),
    SDK_DIR("sdk.dir"),
    TARGET("target"),
    TEST_PROJECT_DIR("tested.project.dir");

    private final String name;

    private PropertyName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
