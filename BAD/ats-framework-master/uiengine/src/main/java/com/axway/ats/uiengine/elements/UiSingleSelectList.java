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
package com.axway.ats.uiengine.elements;

import com.axway.ats.common.PublicAtsApi;
import com.axway.ats.uiengine.UiDriver;

/**
 * A SingleSelectList
 */
@PublicAtsApi
public abstract class UiSingleSelectList extends UiElement {

    public UiSingleSelectList( UiDriver uiDriver,
                               UiElementProperties properties ) {

        super(uiDriver, properties);
    }

    /**
     * select a value
     * 
     * @param value the value to select
     */
    @PublicAtsApi
    public abstract void setValue(
                                   String value );

    /**
     * @return the selected value
     */
    @PublicAtsApi
    public abstract String getValue();

    /**
     * Verify the specified value is selected
     *  
     * @param expectedValue
     */
    @PublicAtsApi
    public abstract void verifyValue(
                                      String expectedValue );

    /**
     * Verify the specified value is NOT selected
     * 
     * @param notExpectedValue
     */
    @PublicAtsApi
    public abstract void verifyNotValue(
                                         String notExpectedValue );

}
