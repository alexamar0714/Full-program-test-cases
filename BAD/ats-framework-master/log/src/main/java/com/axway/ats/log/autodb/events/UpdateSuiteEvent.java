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
package com.axway.ats.log.autodb.events;

import org.apache.log4j.Logger;

import com.axway.ats.log.autodb.LifeCycleState;
import com.axway.ats.log.autodb.model.LoggingEventType;

@SuppressWarnings( "serial")
public class UpdateSuiteEvent extends StartSuiteEvent {

    private final String userNote;

    public UpdateSuiteEvent( String loggerFQCN,
                             Logger logger,
                             String suiteName,
                             String userNote ) {

        super(loggerFQCN,
              logger,
              "Update suite '" + suiteName + "'",
              suiteName,
              null,
              LoggingEventType.UPDATE_SUITE);

        this.userNote = userNote;
    }

    public String getUserNote() {

        return this.userNote;
    }

    @Override
    protected LifeCycleState getExpectedLifeCycleState(
                                                        LifeCycleState event ) {

        return LifeCycleState.ATLEAST_SUITE_STARTED;
    }

}
