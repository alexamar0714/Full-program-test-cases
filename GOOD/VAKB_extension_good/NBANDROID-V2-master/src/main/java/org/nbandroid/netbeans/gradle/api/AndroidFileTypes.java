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

import javax.annotation.Nullable;
import org.openide.filesystems.FileObject;

/**
 * Provides the Android grammar for any documents whose root elements matches a
 * standard pattern.
 *
 * @author Radim Kubacki
 */
public interface AndroidFileTypes {

    boolean isLayoutFile(@Nullable final FileObject fo);
}
