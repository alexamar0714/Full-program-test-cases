//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package org.twdata.pkgscanner.pattern;

/**
 * Compiles wildcard patterns
 */
public interface PatternFactory {

    /**
     * Compiles a wildcard pattern
     * @param pattern The original pattern
     * @return The compiled pattern
     */
    CompiledPattern compile(String pattern);
}
