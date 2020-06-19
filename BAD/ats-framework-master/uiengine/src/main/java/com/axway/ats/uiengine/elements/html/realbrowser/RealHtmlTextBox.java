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
package com.axway.ats.uiengine.elements.html.realbrowser;

import org.openqa.selenium.WebElement;

import com.axway.ats.common.PublicAtsApi;
import com.axway.ats.uiengine.UiDriver;
import com.axway.ats.uiengine.elements.UiElementProperties;
import com.axway.ats.uiengine.elements.html.HtmlElementLocatorBuilder;
import com.axway.ats.uiengine.elements.html.HtmlTextBox;
import com.axway.ats.uiengine.exceptions.VerifyEqualityException;
import com.axway.ats.uiengine.exceptions.VerifyNotEqualityException;
import com.axway.ats.uiengine.utilities.UiEngineUtilities;
import com.axway.ats.uiengine.utilities.realbrowser.html.RealHtmlElementState;

/**
 * An HTML Text Box
 * @see RealHtmlElement
 */
@PublicAtsApi
public class RealHtmlTextBox extends HtmlTextBox {

    //private WebDriver webDriver;

    public RealHtmlTextBox( UiDriver uiDriver,
                            UiElementProperties properties ) {

        super(uiDriver, properties);
        String[] matchingRules = properties.checkTypeAndRules(this.getClass().getSimpleName(),
                                                              "RealHtml",
                                                              RealHtmlElement.RULES_DUMMY);

        // generate the xpath of this HTML element
        String xpath = HtmlElementLocatorBuilder.buildXpathLocator(matchingRules,
                                                                   properties,
                                                                   new String[]{ "text",
                                                                                 "password",
                                                                                 "hidden",
                                                                                 "search",
                                                                                 "email",
                                                                                 "url",
                                                                                 "tel" },
                                                                   "input");
        properties.addInternalProperty(HtmlElementLocatorBuilder.PROPERTY_ELEMENT_LOCATOR, xpath);

        //webDriver = ( WebDriver ) ( ( AbstractRealBrowserDriver ) super.getUiDriver() ).getInternalObject( InternalObjectsEnum.WebDriver.name() );
    }

    /**
     * Get the Text Box value
     * @return
     */
    @Override
    @PublicAtsApi
    public String getValue() {

        new RealHtmlElementState(this).waitToBecomeExisting();

        return RealHtmlElementLocator.findElement(this).getAttribute("value");
    }

    /**
     * Append text to the current content of a Text Box
     * 
     * @param value
     */
    @PublicAtsApi
    public void appendValue(
                             String value ) {

        new RealHtmlElementState(this).waitToBecomeExisting();

        WebElement element = RealHtmlElementLocator.findElement(this);
        element.sendKeys(value);

        UiEngineUtilities.sleep();
    }

    /**
     * Set the Text Box value
     *
     * @param value
     */
    @Override
    @PublicAtsApi
    public void setValue(
                          String value ) {

        new RealHtmlElementState(this).waitToBecomeExisting();

        WebElement element = RealHtmlElementLocator.findElement(this);
        element.clear();
        element.sendKeys(value);

        UiEngineUtilities.sleep();
    }

    /**
     * Verify the Text Box value is as specified
     *
     * @param expectedValue
     */
    @Override
    @PublicAtsApi
    public void verifyValue(
                             String expectedValue ) {

        expectedValue = expectedValue.trim();

        String actualText = getValue().trim();
        if (!actualText.equals(expectedValue)) {
            throw new VerifyEqualityException(expectedValue, actualText, this);
        }
    }

    /**
     * Verify the Text Box value is NOT as specified
     *
     * @param notExpectedValue
     */
    @Override
    @PublicAtsApi
    public void verifyNotValue(
                                String notExpectedValue ) {

        String actualText = getValue();
        if (actualText.equals(notExpectedValue)) {
            throw new VerifyNotEqualityException(notExpectedValue, this);
        }
    }
}
