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
package net.iaeste.iws.api.enums;

import javax.xml.bind.annotation.XmlType;

/**
 * Users can have different status in the system, by default all users are
 * Active. However, a user can also be prevented from logging in - this is
 * a useful feature for someone who are temporarily unavailable. Finally, the
 * user can be deleted. However, as deleting normally implies that the data is
 * removed, the IWS will simply mark the account deleted, and delete all data
 * associated with it. This way, it is still possible to see the things the,
 * now former, user have made in various groups.
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@XmlType(name = "userStatus")
public enum UserStatus {

    NEW,

    ACTIVE,

    SUSPENDED,

    DELETED
}
