//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package org.twdata.pkgscanner.pattern;

/**
 * A wildcard pattern that has been compiled
 */
public interface CompiledPattern {

    /**
     * @return The original pattern
     */
    String getOriginal();

    /**
     * Tries to match a value
     * @param value The value to match
     * @return True if fully matched
     */
    boolean matches(String value);
}
