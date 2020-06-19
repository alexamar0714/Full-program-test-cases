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

import java.net.URL;
import org.nbandroid.netbeans.gradle.v2.maven.ArtifactData;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.ClassPathProvider;

/**
 * Defines the various class paths for a Android project.
 */
public interface AndroidClassPath extends ClassPathProvider {

    /**
     * A classpath used for given type in Android project.
     */
    ClassPath getClassPath(String type);

    /**
     * Register classpath to GlobalPathRegistry.
     */
    void register();

    /**
     * Unregister classpath from GlobalPathRegistry.
     */
    void unregister();

    public ArtifactData getArtifactData(URL url);

}
