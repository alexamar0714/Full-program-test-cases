/*
 * Copyright 2017 Axway Software
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.axway.ats.common.system;

import com.axway.ats.common.PublicAtsApi;
import com.axway.ats.common.systemproperties.AtsSystemProperties;

/**
 * Enumeration representing the operating system type
 */
@PublicAtsApi
public enum OperatingSystemType {

    @PublicAtsApi
    WINDOWS,

    @PublicAtsApi
    LINUX,

    @PublicAtsApi
    SOLARIS,

    @PublicAtsApi
    AIX,

    @PublicAtsApi
    HP_UX,

    @PublicAtsApi
    UNKNOWN;

    private static OperatingSystemType currentOs;

    private static final String        OS_WINDOWS_PREFIX = "Windows";
    private static final String        OS_LINUX          = "Linux";
    private static final String        OS_SOLARIS        = "Solaris";
    private static final String        OS_SUNOS          = "SunOS";
    private static final String        OS_HP_UX          = "HP-UX";
    private static final String        OS_AIX            = "AIX";

    static {
        //static section to initialize the current OS type
        currentOs = getOsType(AtsSystemProperties.SYSTEM_OS_NAME);
    }

    /**
     * Check if this {@link OperatingSystemType} instance is a Unix-type
     * system
     *
     * @return true if this instance is a Unix-type system
     */
    @PublicAtsApi
    public boolean isUnix() {

        if (this == LINUX || this == SOLARIS || this == AIX || this == HP_UX) {
            return true;
        }

        return false;
    }

    /**
     * Check if this {@link OperatingSystemType} instance is a Windows system
     *
     * @return true if this instance is a Windows system
     */
    @PublicAtsApi
    public boolean isWindows() {

        return this == WINDOWS;
    }

    /**
     * Get the operating system on which the current JVM runs
     *
     * @return the OS on which this JVM runs
     */
    @PublicAtsApi
    public static OperatingSystemType getCurrentOsType() {

        return currentOs;
    }

    /**
     * Extract the operating system type from the name
     *
     * @param osName the name of the operating system
     * @return the OS type
     */
    @PublicAtsApi
    public static OperatingSystemType getOsType(
                                                 String osName ) {

        //not sure if this can happen
        if (osName == null) {
            return OperatingSystemType.UNKNOWN;
        }

        if (osName.startsWith(OS_WINDOWS_PREFIX)) {
            return OperatingSystemType.WINDOWS;
        } else if (osName.equalsIgnoreCase(OS_SOLARIS) || osName.equalsIgnoreCase(OS_SUNOS)) {
            return OperatingSystemType.SOLARIS;
        } else if (osName.equalsIgnoreCase(OS_AIX)) {
            return OperatingSystemType.AIX;
        } else if (osName.equalsIgnoreCase(OS_HP_UX)) {
            return OperatingSystemType.HP_UX;
        } else if (osName.equalsIgnoreCase(OS_LINUX)) {
            return OperatingSystemType.LINUX;
        } else {
            return OperatingSystemType.UNKNOWN;
        }
    }
}
