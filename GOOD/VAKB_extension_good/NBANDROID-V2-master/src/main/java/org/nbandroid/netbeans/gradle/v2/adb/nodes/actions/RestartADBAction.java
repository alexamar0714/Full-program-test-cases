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
package org.nbandroid.netbeans.gradle.v2.adb.nodes.actions;

import com.android.ddmlib.AndroidDebugBridge;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.nbandroid.netbeans.gradle.v2.adb.nodes.MobileDeviceNode;
import org.nbandroid.netbeans.gradle.v2.sdk.AndroidSdkProvider;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author radim
 * @author arsi
 */
public class RestartADBAction extends AbstractAction {

    public RestartADBAction() {
        putValue(NAME, NbBundle.getMessage(MobileDeviceNode.class, "NAME_Restart_ADB_Action"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final AndroidDebugBridge debugBridge = AndroidSdkProvider.getAdb();
        if (debugBridge != null) {
            RequestProcessor.getDefault().post(new Runnable() {

                @Override
                public void run() {
                    StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(MobileDeviceNode.class, "MSG_restarting_ADB"));
                    boolean status = debugBridge.restart();
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(
                            MobileDeviceNode.class,
                            status ? "MSG_ADB_restart_success" : "MSG_ADB_restart_fail"));
                }
            });
        } else {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(MobileDeviceNode.class, "ERR_cannot_restart_ADB"));
        }
    }

}
