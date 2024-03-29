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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.axway.ats.log.autodb.LifeCycleState;
import com.axway.ats.log.autodb.model.AbstractLoggingEvent;
import com.axway.ats.log.autodb.model.CacheableEvent;
import com.axway.ats.log.autodb.model.LoggingEventType;

@SuppressWarnings( "serial")
public class InsertMessageEvent extends AbstractLoggingEvent implements CacheableEvent {

    private final boolean escapeHtml;
    private final boolean isRunMessage;

    public InsertMessageEvent( String loggerFQCN,
                               Logger logger,
                               Level level,
                               String message,
                               Throwable throwable,
                               boolean escapeHtml,
                               boolean sendRunMessage ) {

        super(loggerFQCN, logger, level, message, throwable, LoggingEventType.INSERT_MESSAGE);

        this.escapeHtml = escapeHtml;
        this.isRunMessage = sendRunMessage;
    }

    public boolean isEscapeHtml() {

        return escapeHtml;
    }

    public boolean isRunMessage() {

        return isRunMessage;
    }

    @Override
    protected LifeCycleState getExpectedLifeCycleState(
                                                        LifeCycleState state ) {

        switch (state) {
            case RUN_STARTED:
            case SUITE_STARTED:
                return state;
            default:
                return LifeCycleState.TEST_CASE_STARTED;
        }
    }
}
