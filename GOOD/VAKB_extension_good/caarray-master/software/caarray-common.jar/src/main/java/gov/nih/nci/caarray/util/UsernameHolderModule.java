//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util;

import com.google.inject.AbstractModule;

/**
 * @author jscott
 *
 */
public class UsernameHolderModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(UsernameHolder.class).to(UsernameHolderImpl.class).asEagerSingleton();            
    }

}
