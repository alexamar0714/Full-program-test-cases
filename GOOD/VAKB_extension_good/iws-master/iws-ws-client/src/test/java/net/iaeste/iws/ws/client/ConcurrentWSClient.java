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
package net.iaeste.iws.ws.client;

import net.iaeste.iws.api.Access;
import net.iaeste.iws.api.Exchange;
import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.dtos.AuthenticationToken;
import net.iaeste.iws.api.dtos.Authorization;
import net.iaeste.iws.api.dtos.Group;
import net.iaeste.iws.api.dtos.exchange.Offer;
import net.iaeste.iws.api.enums.FetchType;
import net.iaeste.iws.api.enums.GroupType;
import net.iaeste.iws.api.exceptions.IWSException;
import net.iaeste.iws.api.requests.AuthenticationRequest;
import net.iaeste.iws.api.requests.exchange.FetchOffersRequest;
import net.iaeste.iws.api.requests.exchange.OfferRequest;
import net.iaeste.iws.api.responses.AuthenticationResponse;
import net.iaeste.iws.api.responses.Response;
import net.iaeste.iws.api.responses.FetchPermissionResponse;
import net.iaeste.iws.api.responses.exchange.FetchOffersResponse;
import net.iaeste.iws.api.responses.exchange.OfferCSVDownloadResponse;
import net.iaeste.iws.api.responses.exchange.OfferResponse;
import net.iaeste.iws.api.util.Verifications;
import net.iaeste.iws.ws.client.exceptions.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Concurrent WS Client, which will will run with requests from all Committees
 * spread over n THREADS.
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.1
 */
public final class ConcurrentWSClient implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentWSClient.class);

    /**
     * <p>The number of Threads to run with in parallel. The Job Queue will
     * contain all the National Secretaries (see below), but only n of these
     * will be processed concurrently. If the number is set to low, then the
     * test may take very long, and if set too high - it may not properly
     * spread the load.</p>
     * <p>Default value is 5, which is a fairly low number yet - high enough
     * to keep the IWS busy.</p>
     */
    private static final int THREADS = 5;

    /**
     * <p>The IWS Host, is the server where IWS is currently running, including
     * Protocol information, resolvable DNS name and port.</p>
     * <ul>
     *   <li><b>http://localhost:8080</b> <i>if IWS is running locally under Glassfish.</i></li>
     *   <li><b>http://localhost:9080</b> <i>if IWS is running locally under WildFly.</i></li>
     *   <li><b>https://iws.iaeste.net:9443</b> <i>for the production IWS instance.</i></li>
     * </ul>
     */
    private static final String IWS_HOST = "http://localhost:9080";

    /**
     * <p>The test will iterate over a number of Exchange Years. The Exchange
     * Year is ending/starting around September 1st. The first year with Offers
     * registered is 2005, but to reduce the test, a later year can also be
     * chosen.</p>
     */
    private static final int START_YEAR = 2005;

    /**
     * <p>The last Exchange Year to use, by default it is set to the
     * current, using the API Exchange Year Calculator.</p>
     */
    private static final int EXCHANGE_YEAR = Verifications.calculateExchangeYear();

    /**
     * <p>This is a list of All National Secretaries order by the number of
     * Offers they have, with the Country with most being the first. Although
     * Threads are not ordered, the Jobs themselves are, so by sorting by the
     * Country with most Offers in the beginning, we can prevent that the
     * longest running jobs is started so late that other Threads have
     * completed.</p>
     * <p>The list was compiled on May 1st, 2015.</p>
     */
    private static final String[] users = {
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="97e7f6f9fcf6e2d7f3f6f6f3b9f3f2">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="fc929d889593929d908f999f8e99889d8e85bc959d998f8899d29592">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="accdc2c2cd82c6c9ded6cdc7ecc5cdc9dfd8c982dcc0">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d9a9a9abb8bdb699b8bbb0a9bcf7b6abbef7bbab">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="630a02061017064d171611080a1a0623040e020a0f4d000c0e">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="740715161d1a115a18111a0e341b12121d17115a1d15110700115a171c">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4d242c283e39283e3d0d383d3b23283963383d3b63283e">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f8948d93998bd68b9b908f9d969c91969f9d8ab891999d8b8c9dd6998c">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="98f1f9fdebecfdd8ecf5feb6faffb6f9fbb6eaeb">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c1b3a8a2a9a0b3a5efb6b481a8a0a4b2b5a4eca2a9a8afa0efaeb3a6">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e58c8480969180cb91908b8c968c84a58288848c89cb868a88">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="94e3f1faf0edbae3f5e6fdfaf3d4f6e6fde0fde7fcf7fbe1faf7fdf8bafbe6f3">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="374156545b5641194756415b5e5c775e565244435219544d">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c0a1aeaea5aca9a5b3eeb6a5b2ada5a9b280b5a7a5aeb4eea2a5">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1761767b7279637e7976397d78617e742657707a767e7b3974787a">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="26444354484754424447435f4348551466414b474f4a0845494b">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="8ffcf5e6fde2eee6a1ffeee3cfe6eeeafcfbeaa1e7fa">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e2918a87898390818aa29796cc8381cc8b90">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="661512070b03080d091048000f0a0f1626010b070f0a4805090b">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e68f8783959283818ea69f878e8989c885898b">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="41372425332e797924352701262c20282d6f222e2c">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1c737a7a757f792e5c757d796f687932736e32766c">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="6713040c270c0a1213090549060449130f">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e68d879287948f8887c88d89948388858788a68f8783959283c8958f">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="31585054424554475854455f505c71565c50585d1f525e5c">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4820213a2229236625213a273b24293e082f25292124662b2725">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1b727578747672757c5b727a7e686f7e696e6868727a35696e">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bccfd5d0cad5dd92cfddd2c8d3cffcc8d9dfd2d5dfd392c9d0d5cfded3dd92ccc8">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9cf7fdeef3f0f5f2f9f0f9f7eaf9dcfbf1fdf5f0b2fff3f1">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2252504d444751514d505b4d5762454f434b4e0c414d4f">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1475737a71673a75667775667b7a67547379757d783a777b79">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3352575a4752734042461d5657461d5c5e">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1e7a7b697b6c6a5e7d6b726a6b6c7f7268776d6a7f6d30716c79">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c0b3b4b2a980a1a5b2eeaeb4b5edabb0a9eeaba9a5b6eeb5a1">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="0c7e2265616d614c667922696879226663">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="81e8e0e4f2f5e4deeafbc1e9eef5ece0e8edafe2eeec">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="345d51574155505b46744d555c5b5b1a575b59">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5930383c2a2d3c193430373a202d773e363b77382b">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7a131b1f090e1f250815171b14131b3a031b121515541915540f11">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="cdafa18da4aca8beb9a8e3a9a6">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1a787f7b6e6873797f3479726f5a6a7576636f347f7e6f347271">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5f2f2a333e2b30291f3d34712d2a">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="61080004121504210214154f00024f0218">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="513d3032393d303f393023213d342811363c30383d7f323e3c">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1f767e7a6c6b7a5f7c7a716b6d7e7331716b6a7e31786d">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="781c0e19381511161d1c0d560d16111a1d14561a01">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="771a121a18021c3716021559121302591b15">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="aecfd9cfcaeec3cfc7c280c0cfc4cfc680cbcadb">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5a333b3f292e3f1a2e22742e3f393234333534743b39743336">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="ddbcb3b9b8afaef3bbafb8b9b8b39dbeb5bcb1b0b8afaef3aeb8">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="95f7f4e1effae7fcf2d5f8e0e6e1bbf0f1e0bbf8fb">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9bf2fafee8effedbf7fefae9fcfae8b5f2fe">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f994988b9098d78a8d9c959598d789968b8d9c959590b990989c8a8d9cd7968b9ed7948d">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="fc959d998f8899bc8f949d8e969d94d29d9fd29d99">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="81ece0f3e8e3e4edafe0ede0f8fbe0c1f4e5e4f1aff1e4">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2a444b5e4345444b46594f49584f5e4b58536a434b4f595e4f494b444b4e4b0445584d">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="44322d27302b366a37252a272c213e043130346a25276a3425">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9cf5fdf9efe8f9b1f7f9f2e5fddcf6f7e9fde8b2fdffb2f7f9">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e695938b8988a685879283858e838293c885898b">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e08a8f8e898c8fd48c8f9685d1a09981888f8fce838f8d">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="becccad1dfddfecbd3dfdd90d3d1">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="fa8c9b939e9bd4898a8f9e838e9fba9d979b9396d4999597">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="13747b7a7d725362663d7677663d6272">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="dcb5bdb9afa8b9b2b9b8b9aeb0bdb2b89cbbb1bdb5b0f2bfb3b1">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bbd1dad6ced0cbd4c8fbc2dad3d4d495d8d4d6">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1f767e7a6c6b7a317a78666f6b5f727e76737c766b66317c7072">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5e3d31302d2b3b32311e3f33372e2e70312c39">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="ef8186878e83af829d9bc18e8cc18384">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7811191d0b0c1d3808171d1b56161d0c">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d8bebab9bcadb6bcafb9eae8e8ec98a1b9b0b7b7f6bbb7b5">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bcd1ddced9c892d4d9d5d2fcc8c8c992d9d9">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="92fffde1f7e1fef7e5f3fefeebd2ebf3fafdfdbcf1fdff">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="37455a5650424356774e565f58581954585a">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1d79757c767c713376757c7a7873796f7c5d7a707c7471337e7270">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e3938297918a808a82938284828d8a848290908691a3848e828a8fcd808c8e">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="573e36322423320827363c3e24233639172e363f383879343879223c">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="95e1f4f9f4e1f8e1d5e7f4f8f7f9f0e7bbe7e0">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5a3f3e3633283b743529373b34331a3d373b333674393537">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="05766472606e606864457c646d6a6a2b666a68">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f09392959e9e958484b09a9f898384899f85849895889398919e9795999e849cde9f8297">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="374142505645774e5244524152594319584550">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9aeafbeee8f3f9f3f0fbb4f1fbe8fbdafdf7fbf3f6b4f9f5f7">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="8be2eaeef8ffeea5f8f2f9cbe3e4ffe6eae2e7a5e8e4e6">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="315c50595e424250585f030101004442714850595e5e1f525e5c">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="96fbfbf3e4f3fdffd6fbf9e6ffe6ffb8e3f4b8f4e1">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="0d6e787e7d686e646c616c7e7e647e796c63794d746c656262236e6260">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5b38333a29373e2875303a3c3229321b3f302e2f753a3875303e">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="dbb7baaea9bab6beb5bfbea1aba9beb5b8b0be9bbcb6bab2b7f5b8b4b6">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a9c2c6c29898e9c1c087c0da">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="422b2c25302b266c316c2f212735232c022b23273136276c2d30256c2337">[email protected]</a>",
            "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bbd3daddd2c1dad7fbcec8d695d6c2">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="681806091e091a1a0d1c0d280b071a18091a091d0b09060109460b04">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3c5b494e5d514f5350535b5d4f544a5550557c455d545353125f53124957">[email protected]</a>"
    };

    private final Object lock = new Object();

    private final String host;
    private final String username;
    private final String password;

    private Access access = null;
    private Exchange exchange = null;

    /**
     * <p>Concurrent Constructor, takes the Host & Port for the IWS WebService,
     * and additionally the User Credentials, and the Start/End years for the
     * Offers to process.</p>
     *   Production Hostname; https://iws.iaeste.net:9443
     *
     * @param host     Hostname (resolvable DNS record or IPv4) for the IWS instance
     * @param username Username for the User to access
     * @param password Password for the User
     */
    private ConcurrentWSClient(final String host, final String username, final String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     * <p>Concurrent main method, which will iterate over the list of users
     * provided, and for each user, retrieve offers from all registered Exchange
     * Years (2005 - 2015), and simply update all of them.</p>
     *
     * <p>The goal of this, is to stress test the IWS running under either
     * WildFly or Glassfish.</p>
     *
     * @param args Command Line Parameters
     */
    public static void main(final String[] args) throws InterruptedException {
        LOG.info("Starting {} THREADS concurrently to process the Offers for {} users from {} until {}.", THREADS, users.length, START_YEAR, EXCHANGE_YEAR);
        final long startTime = System.nanoTime();

        final ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        final List<Callable<Object>> jobs = new ArrayList<>(users.length);
        for (final String username : users) {
            final ConcurrentWSClient client = new ConcurrentWSClient(IWS_HOST, username, "faked");
            jobs.add(Executors.callable(client));
        }
        final List<Future<Object>> result = executor.invokeAll(jobs);
        executor.shutdown();

        final DecimalFormat format = new DecimalFormat("###,###.##");
        final String duration = format.format((double) (System.nanoTime() - startTime) / 60000000000L);
        LOG.info("Completed concurrent processing of Offers with {} THREADS in {} minutes.", result.size(), duration);
    }

    // =========================================================================
    // Constants, Settings and Constructor
    // =========================================================================

    /**
     * Runner Method, which will iterate over the Offers for the current User.
     */
    @Override
    public void run() {
        LOG.info("Starting Offer Processing thread for {}.", username);

        // Before we can do anything, we first need to log in
        final AuthenticationResponse authResponse = login(username, password);

        // If the login request was successful, then we can make further things
        if (authResponse.isOk()) {
            final AuthenticationToken token = authResponse.getToken();

            try {
                final FetchPermissionResponse permissionResponse = fetchPermissions(token);
                final Group member = findGroupByType(permissionResponse, GroupType.MEMBER);
                processOffers(token, member);
            } catch (RuntimeException t) {
                LOG.error(t.getMessage(), t);
            } finally {
                // Always remember to log out, otherwise the Account will be
                // blocked for a longer time period
                LOG.debug("Deprecated Session for user '{}': {}", username, deprecateSession(token).getMessage());
            }
        }

        LOG.info("Completed Offer Processing thread for {}.", username);
    }

    private void processOffers(final AuthenticationToken token, final Group member) {
        // Iterate over the Exchange Years
        for (int i = START_YEAR; i <= EXCHANGE_YEAR; i++) {
            LOG.debug("Start iterating over Exchange Year {} for Group '{}'.", i, member.getCommitteeName());
            final FetchOffersResponse domestic = fetchOffers(token, FetchType.DOMESTIC, i);
            final FetchOffersResponse shared = fetchOffers(token, FetchType.SHARED, i);
            LOG.info("Found {} Domestic Offers & {} Shared Offers for {} in {}", domestic.getOffers().size(), shared.getOffers().size(), member.getCommitteeName(), i);

            for (final Offer offer : domestic.getOffers()) {
                final OfferResponse response = processOffer(token, offer);
                if (!response.isOk()) {
                    LOG.warn("Processing Offer with Reference Number '{}' failed: {}", offer.getRefNo(), processOffer(token, offer).getMessage());
                }
            }
        }
    }

    // =========================================================================
    // Prepare IWS WebServices
    // =========================================================================

    private Access getAccess() {
        synchronized (lock) {
            if (access == null) {
                try {
                    access = new AccessWSClient(host + "/iws-ws/accessWS?wsdl");
                } catch (MalformedURLException e) {
                    throw new WebServiceException(e);
                }
            }

            return access;
        }
    }

    private Exchange getExchange() {
        synchronized (lock) {
            if (exchange == null) {
                try {
                    exchange = new ExchangeWSClient(host + "/iws-ws/exchangeWS?wsdl");
                } catch (MalformedURLException e) {
                    throw new WebServiceException(e);
                }
            }

            return exchange;
        }
    }

    // =========================================================================
    // Class Methods, used to perform various actions
    // =========================================================================

    /**
     * <p>Sample IWS Login request. The request requires two parameters,
     * username (the e-mail address whereby the User is registered), and the
     * password.</p>
     *
     * <p>The method will build and send the Request Object, and return the
     * Response Object from the IWS.</p>
     *
     * @param username E-mail address whereby the user is registered in the IWS
     * @param password The user's password for the IWS
     * @return Response Object from the IWS
     */
    public AuthenticationResponse login(final String username, final String password) {
        final AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername(username);
        request.setPassword(password);

        return getAccess().generateSession(request);
    }

    /**
     * <p>Sample IWS Deprecate Session request. The request requires just the
     * current Session Token to be deprecated.</p>
     *
     * <p>The method will build and send the Request Object, and return the
     * Response Object from the IWS.</p>
     *
     * @param token User Authentication (Session) Token
     * @return Response Object from the IWS
     */
    public Response deprecateSession(final AuthenticationToken token) {
        return getAccess().deprecateSession(token);
    }

    /**
     * <p>Sample IWS Fetch Permissions request. The request requires just the
     * current Session Token, and will return the Groups which the user is a
     * member of, together with the permissions that the user has in each
     * Group.</p>
     *
     * <p>The method will build and send the Request Object, and return the
     * Response Object from the IWS.</p>
     *
     * @param token User Authentication (Session) Token
     * @return Response Object from the IWS
     */
    public FetchPermissionResponse fetchPermissions(final AuthenticationToken token) {
        return getAccess().fetchPermissions(token);
    }

    /**
     * <p>Sample IWS Fetch Offers request. The request requires two parameters,
     * the current Session Token and the type of Offers to be fetched. The
     * Exchange Year is omitted, as it is the current.</p>
     *
     * <p>The method will build and send the Request Object, and return the
     * Response Object from the IWS.</p>
     *
     * @param token User Authentication (Session) Token
     * @param type  Type of Offers to be fetch (domestic or shared)
     * @param year  Exchange Year
     * @return Response Object from the IWS
     */
    public FetchOffersResponse fetchOffers(final AuthenticationToken token, final FetchType type, final int year) {
        final FetchOffersRequest offerRequest = new FetchOffersRequest();
        offerRequest.setFetchType(type);
        offerRequest.setExchangeYear(year);

        return getExchange().fetchOffers(token, offerRequest);
    }

    /**
     * <p>Sample IWS Download Offers request. The request requires two parameters,
     * the current Session Token and the type of Offers to be fetched. The
     * Exchange Year is omitted, as it is the current.</p>
     *
     * <p>The method will build and send the Request Object, and return the
     * Response Object from the IWS.</p>
     *
     * @param token User Authentication (Session) Token
     * @param type  Type of Offers to be fetch (domestic or shared)
     * @param year  Exchange Year
     * @return Response Object from the IWS
     */
    public OfferCSVDownloadResponse downloadOffers(final AuthenticationToken token, final FetchType type, final int year) {
        final FetchOffersRequest request = new FetchOffersRequest();
        request.setFetchType(type);
        request.setExchangeYear(year);

        return getExchange().downloadOffers(token, request);
    }

    /**
     * <p>Sample IWS Process Offer Request. The request requires st current
     * token, and an Employer to be processed.</p>
     *
     * <p>The method will build and send the Request Object, and return the
     * Response Object from the IWS.</p>
     *
     * @param token User Authentication (Session) Token
     * @param offer The Offer to be processed (created or updated)
     * @return Response Object from the IWS
     */
    private OfferResponse processOffer(final AuthenticationToken token, final Offer offer) {
        final OfferRequest request = new OfferRequest();
        request.setOffer(offer);

        return getExchange().processOffer(token, request);
    }

    // =========================================================================
    // Internal Methods
    // =========================================================================

    private static Group findGroupByType(final FetchPermissionResponse response, final GroupType type) {
        Group group = null;

        for (final Authorization authorization : response.getAuthorizations()) {
            final Group current = authorization.getUserGroup().getGroup();
            if (current.getGroupType() == type) {
                group = current;
                break;
            }
        }

        if (group == null) {
            throw new IWSException(IWSErrors.FATAL, "Cannot complete test.");
        }

        return group;
    }
}
