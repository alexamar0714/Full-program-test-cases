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
import com.axway.ats.log.autodb.model.AbstractLoggingEvent;
import com.axway.ats.log.autodb.model.LoggingEventType;

@SuppressWarnings( "serial")
public class StartAfterSuiteEvent extends AbstractLoggingEvent {

    public StartAfterSuiteEvent( String loggerFQCN, Logger logger ) {
        super(loggerFQCN, logger, "Start after suite execution", LoggingEventType.START_AFTER_SUITE);
    }

    @Override
    protected LifeCycleState getExpectedLifeCycleState( LifeCycleState state ) {

        return LifeCycleState.SUITE_STARTED;
    }

}
