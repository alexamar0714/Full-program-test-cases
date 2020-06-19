/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.nbandroid.netbeans.gradle.v2.sdk;

import com.android.repository.api.Channel;
import com.android.repository.api.LocalPackage;
import com.android.repository.api.ProgressRunner;
import com.android.repository.api.RepoManager;
import com.android.repository.api.SettingsController;
import com.android.repository.api.UpdatablePackage;
import com.android.sdklib.devices.Device;
import com.android.sdklib.devices.DeviceManager;
import com.android.sdklib.repository.AndroidSdkHandler;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.nbandroid.netbeans.gradle.core.sdk.NbOutputWindowProgressIndicator;
import org.nbandroid.netbeans.gradle.v2.sdk.manager.SdkManagerPlatformChangeListener;
import org.nbandroid.netbeans.gradle.v2.sdk.manager.SdkManagerToolsChangeListener;
import org.openide.filesystems.FileObject;

/**
 * SDK Manager
 *
 * @author arsi
 */
public abstract class AndroidSdk {

    public static final String DEFAULT_SDK = "DEFAULT_SDK";
    public static final String LOCATION = "LOCATION";

    public static final ExecutorService pool = Executors.newCachedThreadPool();
    private PropertyChangeSupport supp;

    /**
     * Add SdkManagerPlatformChangeListener to listen of SDK platform pakages
     * list changes On add is listener called with actual package list
     *
     * @param l SdkManagerPlatformChangeListener
     */
    public abstract void addSdkPlatformChangeListener(SdkManagerPlatformChangeListener l);

    /**
     * Remove SdkManagerPlatformChangeListener
     *
     * @param l SdkManagerPlatformChangeListener
     */
    public abstract void removeSdkPlatformChangeListener(SdkManagerPlatformChangeListener l);

    public abstract AndroidSdkHandler getAndroidSdkHandler();

    public abstract List<AndroidPlatformInfo> getPlatforms();

    /**
     * Get Android repo manager
     *
     * @return RepoManager or null if no SDK location is set
     */
    public abstract RepoManager getRepoManager();

    public abstract void store();

    /**
     * Update SDK platform pakages list After update is fired
     * SdkManagerPlatformChangeListener and SdkManagerToolsChangeListener
     */
    public abstract void updateSdkPlatformPackages();

    /**
     * Add addSdkToolsChangeListener to listen of SDK tools pakages list changes
     * On add is listener called with actual package list
     *
     * @param l addSdkToolsChangeListener
     */
    public abstract void addSdkToolsChangeListener(SdkManagerToolsChangeListener l);

    /**
     * Remove SdkManagerToolsChangeListener
     *
     * @param l SdkManagerToolsChangeListener
     */
    public abstract void removeSdkToolsChangeListener(SdkManagerToolsChangeListener l);

    /**
     * Uninstall android package After unistall is called
     * updateSdkPlatformPackages()
     *
     * @param aPackage LocalPackage
     */
    public abstract void uninstallPackage(LocalPackage aPackage);

    /**
     * Install or Update Android package After istall is called
     * updateSdkPlatformPackages()
     *
     * @param aPackage UpdatablePackage
     */
    public abstract void installPackage(final UpdatablePackage aPackage);

    public abstract String getSdkPath();

    public abstract void addLocalPlatformChangeListener(LocalPlatformChangeListener l);

    public abstract void removeLocalPlatformChangeListener(LocalPlatformChangeListener l);

    public abstract String getDisplayName();

    public abstract String getHtmlDisplayName();

    public abstract FileObject getInstallFolder();

    public abstract List<FileObject> getInstallFolders();

    public abstract Map<String, String> getProperties();

    abstract Map<String, String> getSystemProperties();

    public abstract boolean isDefaultSdk();

    public abstract void setDefault(boolean defaultSdk);

    public abstract void setDisplayName(String displayName);

    public abstract void setSdkRootFolder(String sdkPath);

    public abstract boolean isBroken();

    public abstract boolean isValid();

    protected class DownloadErrorCallback implements Runnable {

        @Override
        public void run() {
        }
    }

    protected class NbProgressRunner implements ProgressRunner {

        @Override
        public void runAsyncWithProgress(ProgressRunner.ProgressRunnable r) {
            r.run(new NbOutputWindowProgressIndicator(), this);
        }

        @Override
        public void runSyncWithProgress(ProgressRunner.ProgressRunnable r) {
            r.run(new NbOutputWindowProgressIndicator(), this);
        }

        @Override
        public void runSyncWithoutProgress(Runnable r) {
            pool.submit(r);
        }
    }

    protected class NbSettingsController implements SettingsController {

        boolean force = false;

        @Override
        public boolean getForceHttp() {
            return force;
        }

        @Override
        public void setForceHttp(boolean force) {
            this.force = force;
        }

        @Override
        public Channel getChannel() {
            //TODO add channel selection to Android settings
            return Channel.create(0);
        }
    }

    public final void addPropertyChangeListener(PropertyChangeListener l) {
        synchronized (this) {
            if (supp == null) {
                supp = new PropertyChangeSupport(this);
            }
        }
        supp.addPropertyChangeListener(l);
    }

    /**
     * Removes a listener registered previously
     */
    public final void removePropertyChangeListener(PropertyChangeListener l) {
        if (supp != null) {
            supp.removePropertyChangeListener(l);
        }
    }

    /**
     * Fires PropertyChange to all registered PropertyChangeListeners
     *
     * @param propName
     * @param oldValue
     * @param newValue
     */
    protected final void firePropertyChange(String propName, Object oldValue, Object newValue) {
        if (supp != null) {
            supp.firePropertyChange(propName, oldValue, newValue);
        }
    }

    public abstract AndroidPlatformInfo findPlatformForTarget(String target);

    public abstract AndroidPlatformInfo findPlatformForPlatformVersion(final int sdkVersion);

    public abstract DeviceManager getDeviceManager();

    public abstract Iterable<Device> getDevices();

    public abstract FileObject findTool(String toolName);

}
