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
package com.axway.ats.uiengine.elements.html.hiddenbrowser;

import java.lang.reflect.Field;

import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.axway.ats.common.PublicAtsApi;
import com.axway.ats.uiengine.HiddenBrowserDriver;
import com.axway.ats.uiengine.UiDriver;
import com.axway.ats.uiengine.configuration.UiEngineConfigurator;
import com.axway.ats.uiengine.elements.html.HtmlAlert;
import com.axway.ats.uiengine.exceptions.SeleniumOperationException;
import com.axway.ats.uiengine.exceptions.VerificationException;
import com.axway.ats.uiengine.internal.driver.InternalObjectsEnum;
import com.axway.ats.uiengine.utilities.UiEngineUtilities;
import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * An HTML Alert
 */
@PublicAtsApi
public class HiddenHtmlAlert extends HtmlAlert {

    private WebClient webClient;

    private boolean   isProcessed = false;

    public HiddenHtmlAlert( UiDriver uiDriver ) {

        super(uiDriver);

        HiddenBrowserDriver browserDriver = (HiddenBrowserDriver) uiDriver;
        HtmlUnitDriver driver = (HtmlUnitDriver) browserDriver.getInternalObject(InternalObjectsEnum.WebDriver.name());
        Field webClientField = null;
        boolean fieldAccessibleState = false;
        try {

            TargetLocator targetLocator = driver.switchTo();
            webClientField = targetLocator.getClass().getDeclaringClass().getDeclaredField("webClient");
            fieldAccessibleState = webClientField.isAccessible();
            webClientField.setAccessible(true);
            webClient = (WebClient) webClientField.get(targetLocator.defaultContent());
        } catch (Exception e) {

            throw new SeleniumOperationException("Error retrieving internal Selenium web client", e);
        } finally {

            if (webClientField != null) {
                webClientField.setAccessible(fieldAccessibleState);
            }
        }
    }

    @Override
    @PublicAtsApi
    public void clickOk() {

        isProcessed = false;
        webClient.setAlertHandler(new AlertHandler() {

            @Override
            public void handleAlert(
                                     Page alertPage,
                                     String alertText ) {

                isProcessed = true;
                //do nothing, by default it clicks the OK button
            }
        });
    }

    @Override
    @PublicAtsApi
    public void clickOk(
                         final String expectedAlertText ) {

        isProcessed = false;
        webClient.setAlertHandler(new AlertHandler() {

            @Override
            public void handleAlert(
                                     Page alertPage,
                                     String alertText ) {

                isProcessed = true;
                if (!alertText.equals(expectedAlertText)) {

                    throw new VerificationException("The expected alert message was: '" + expectedAlertText
                                                    + "', but actually it is: '" + alertText + "'");
                }
            }
        });
    }

    @Override
    @PublicAtsApi
    public void verifyProcessed() {

        long millis = UiEngineConfigurator.getInstance().getElementStateChangeDelay();
        long endTime = System.currentTimeMillis() + millis;
        do {
            if (isProcessed) {
                return;
            }
            UiEngineUtilities.sleep();
        } while (endTime - System.currentTimeMillis() > 0);

        throw new VerificationException("Failed to verify the Alert is processed within " + millis + " ms");
    }

}
