/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.nbandroid.netbeans.gradle.v2;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.nbandroid.netbeans.gradle.v2.apk.actions.DebugApkAction;
import org.nbandroid.netbeans.gradle.v2.apk.actions.ReleaseUnsignedApkAction;
import org.nbandroid.netbeans.gradle.v2.apk.actions.SignApkAction;
import org.netbeans.gradle.project.api.nodes.GradleProjectContextActions;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author arsi
 */
public class AndroidProjectContextActions implements GradleProjectContextActions {

    @Override
    public List<Action> getContextActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(SystemAction.get(DebugApkAction.class));
        actions.add(SystemAction.get(SignApkAction.class));
        actions.add(SystemAction.get(ReleaseUnsignedApkAction.class));
        return actions;
    }

}
