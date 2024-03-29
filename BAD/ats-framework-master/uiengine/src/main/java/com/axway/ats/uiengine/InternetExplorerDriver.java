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
package com.axway.ats.uiengine;

import com.axway.ats.common.PublicAtsApi;

/**
 * A driver operating over Internet Explorer browser
 */
@PublicAtsApi
public class InternetExplorerDriver extends AbstractRealBrowserDriver {

    /**
     * To get InternetExplorerDriver instance use UiDriver.getInternetExplorerDriver()
     * @param url the target application URL
     */
    protected InternetExplorerDriver( String url ) {

        super(AbstractRealBrowserDriver.BrowserType.InternetExplorer, url, null);
    }

    /**
     * To get InternetExplorerDriver instance use UiDriver.getInternetExplorerDriver()
     *
     * @param url the target application URL
     * @param remoteSeleniumURL the remote selenium hub URL (eg. http://10.11.12.13:4444/wd/hub/)
     */
    protected InternetExplorerDriver( String url,
                                      String remoteSeleniumURL ) {

        super(AbstractRealBrowserDriver.BrowserType.InternetExplorer, url, null, remoteSeleniumURL);
    }
}
