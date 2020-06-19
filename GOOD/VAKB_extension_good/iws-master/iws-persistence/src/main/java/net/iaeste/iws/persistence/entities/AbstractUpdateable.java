/*
 * Licensed to IAESTE A.s.b.l. (IAESTE) under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership. The Authors
 * (See the AUTHORS file distributed with this work) licenses this file to
 * You under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a
 * copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.iaeste.iws.persistence.entities;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.persistence.Externable;

/**
 * To Implement the diff method required by the Updateable interface, we have
 * this little Abstract class that contains helper methods.
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public abstract class AbstractUpdateable<T> implements Updateable<T> {

    /** {@link IWSConstants#SERIAL_VERSION_UID}. */
    private static final long serialVersionUID = IWSConstants.SERIAL_VERSION_UID;

    /**
     * Returns 1 (one) if the two Objects are different, otherwise a 0 (zero)
     * is returned.
     *
     * @param first  The Object to compare against
     * @param second The other Object to compare with
     * @return 1 (one) if the Objects are different, otherwise 0 (zero)
     */
    protected static <T> int different(final T first, final T second) {
        // To make it easier to read, the conditional expression is expanded
        final int result;

        if (first != null) {
            if (first.equals(second)) {
                result = 0;
            } else {
                result = 1;
            }
        } else {
            if (second == null) {
                result = 0;
            } else {
                result = 1;
            }
        }

        return result;
    }

    /**
     * Determines which value should be updated. If the changes is not null,
     * then they are returned, otherwise the existing is returned.
     *
     * @param existing Existing value to use if changes is null
     * @param changes  Updated value to use if not null
     * @return Changes if they are not null, otherwise the existing
     */
    protected final <E> E which(final E existing, final E changes) {
        return (changes != null) ? changes : existing;
    }

    /**
     * To be able to merge changes from one Object into another, we need to make
     * sure that certain conditions have been met. This method will take care of
     * this by checking both Objects for null and compare relevant Id's.
     *
     * @param existing The existing IWS Entity we need to update
     * @param changes  The changes to be merged in
     * @return True if the records can be merged, otherwise false is returned
     */
    protected final <E extends Externable<E>> boolean canMerge(final E existing, final E changes) {
        // don't merge if objects are not the same entity
        boolean result = false;

        if ((existing != null) && (changes != null) && (existing.getId() != null)) {
            final String existingExternalId = existing.getExternalId();
            final String changesExternalId = changes.getExternalId();

            if ((changesExternalId == null) || existingExternalId.equals(changesExternalId)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * To be able to merge changes from one Object into the current, we need to
     * make sure that certain conditions have been met. This method will take
     * care of this by checking both Objects for null and compare relevant Id's.
     *
     * @param changes  The changes to be merged in
     * @return True if the records can be merged, otherwise false is returned
     */
    protected final boolean canMerge(final T changes) {
        boolean result = false;

        // Merging cannot be done if the Object to merge is null or if the
        // current Object has not yet been persisted.
        if ((changes != null) && (getId() != null)) {
            // If we have an Externable Object, then we also have to verify that
            // the Id's match, as we do not wish to merge two different Offers
            // since it may potentially give Unique Constraint Violations.
            if (this instanceof Externable) {
                final String existingId = ((Externable<?>) this).getExternalId();
                final String givenId = ((Externable<?>) changes).getExternalId();

                // Often, changes are created using an empty Entity, i.e. an
                // Entity which have not been persisted. So the check exclude
                // these.
                if ((givenId == null) || existingId.equals(givenId)) {
                    result = true;
                }
            } else {
                // Not an external, then we'll just allow that they can be
                // merged.
                result = true;
            }
        }

        return result;
    }
}
