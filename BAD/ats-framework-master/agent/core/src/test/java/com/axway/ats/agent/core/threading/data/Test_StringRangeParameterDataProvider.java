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
package com.axway.ats.agent.core.threading.data;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.axway.ats.agent.core.BaseTest;
import com.axway.ats.agent.core.action.ArgumentValue;
import com.axway.ats.agent.core.exceptions.AgentException;
import com.axway.ats.agent.core.threading.data.config.ParameterProviderLevel;

public class Test_StringRangeParameterDataProvider extends BaseTest {

    @Test
    public void perThreadStaticGeneration() {

        StringRangeParameterDataProvider dataProvider = new StringRangeParameterDataProvider( "param1",
                                                                                              "user{0}@test.com",
                                                                                              10,
                                                                                              20,
                                                                                              ParameterProviderLevel.PER_THREAD_STATIC );

        ArgumentValue generatedValue = dataProvider.getValue( new ArrayList<ArgumentValue>() );
        assertEquals( "param1", generatedValue.getName() );
        assertEquals( "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="82f7f1e7f0b3b2c2f6e7f1f6ace1edef">[email protected]</a>", generatedValue.getValue() );

        //make sure only one instance per thread is returned
        generatedValue = dataProvider.getValue( new ArrayList<ArgumentValue>() );
        assertEquals( "param1", generatedValue.getName() );
        assertEquals( "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c2b7b1a7b0f3f282b6a7b1b6eca1adaf">[email protected]</a>", generatedValue.getValue() );
    }

    @Test
    public void perInvocationGeneration() {

        StringRangeParameterDataProvider dataProvider = new StringRangeParameterDataProvider( "param1",
                                                                                              "user{0}@test.com",
                                                                                              10,
                                                                                              20,
                                                                                              ParameterProviderLevel.PER_INVOCATION );

        ArgumentValue generatedValue = dataProvider.getValue( new ArrayList<ArgumentValue>() );
        assertEquals( "param1", generatedValue.getName() );
        assertEquals( "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="dca9afb9aeedec9ca8b9afa8f2bfb3b1">[email protected]</a>", generatedValue.getValue() );

        //make sure only one instance per thread is returned
        generatedValue = dataProvider.getValue( new ArrayList<ArgumentValue>() );
        assertEquals( "param1", generatedValue.getName() );
        assertEquals( "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3a4f495f480b0b7a4e5f494e14595557">[email protected]</a>", generatedValue.getValue() );
    }

    @Test
    public void whenRangeEndIsReachedProviderGoesBackToRangeStart() {

        StringRangeParameterDataProvider dataProvider = new StringRangeParameterDataProvider( "param1",
                                                                                              "user{0}@test.com",
                                                                                              10,
                                                                                              14,
                                                                                              ParameterProviderLevel.PER_INVOCATION );

        ArgumentValue generatedValue;
        for( int i = 10; i < 15; i++ ) {
            generatedValue = dataProvider.getValue( new ArrayList<ArgumentValue>() );
            assertEquals( "param1", generatedValue.getName() );
            assertEquals( "user" + i + "@test.com", generatedValue.getValue() );
        }

        //make sure only one instance per thread is returned
        generatedValue = dataProvider.getValue( new ArrayList<ArgumentValue>() );
        assertEquals( "param1", generatedValue.getName() );
        assertEquals( "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4b3e382e397a7b0b3f2e383f65282426">[email protected]</a>", generatedValue.getValue() );
    }

    @Test
    public void initialize() throws AgentException {

        StringRangeParameterDataProvider dataProvider = new StringRangeParameterDataProvider( "param1",
                                                                                              "user{0}@test.com",
                                                                                              10,
                                                                                              20,
                                                                                              ParameterProviderLevel.PER_INVOCATION );

        ArgumentValue generatedValue = dataProvider.getValue( new ArrayList<ArgumentValue>() );
        assertEquals( "param1", generatedValue.getName() );
        assertEquals( "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3346405641020373475640471d505c5e">[email protected]</a>", generatedValue.getValue() );

        dataProvider.initialize();

        //make sure only one instance per thread is returned
        generatedValue = dataProvider.getValue( new ArrayList<ArgumentValue>() );
        assertEquals( "param1", generatedValue.getName() );
        assertEquals( "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7702041205464737031204035914181a">[email protected]</a>", generatedValue.getValue() );
    }
}
