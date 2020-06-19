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
package net.iaeste.iws.persistence.entities;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import net.iaeste.iws.api.dtos.AuthenticationToken;
import net.iaeste.iws.common.configuration.Settings;
import net.iaeste.iws.persistence.AccessDao;
import net.iaeste.iws.persistence.Authentication;
import net.iaeste.iws.persistence.ExchangeDao;
import net.iaeste.iws.persistence.entities.exchange.PublishingGroupEntity;
import net.iaeste.iws.persistence.jpa.AccessJpaDao;
import net.iaeste.iws.persistence.jpa.ExchangeJpaDao;
import net.iaeste.iws.persistence.setup.SpringConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Contains tests for PublishingGroupEntityTest and fetching from ExchangeDao
 *
 * @author  Pavel Fiala / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = SpringConfig.class)
public class PublishingGroupEntityTest {

    private static final String SHARING_LIST_EXTERNAL_ID = "adc8dfd4-bc3a-4b27-897b-87d3950db503";
    private static final String SHARING_LIST_NAME = "My Selection";

    @PersistenceContext
    private EntityManager entityManager;

    private ExchangeDao exchangeDao = null;
    private AccessDao accessDao = null;

    private Authentication authentication = null;

    @Before
    public void before() {
        final Settings settings = new Settings();
        accessDao = new AccessJpaDao(entityManager, settings);
        exchangeDao = new ExchangeJpaDao(entityManager, settings);

        final AuthenticationToken token = new AuthenticationToken();
        final UserEntity user = accessDao.findActiveUserByUsername("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7d1c080e090f141c3d141c180e0918531c09">[email protected]</a>");
        final GroupEntity group = accessDao.findNationalGroup(user);
        authentication = new Authentication(token, user, group, UUID.randomUUID().toString());
    }

    @Test
    @Transactional
    public void testClassflow() {
        final PublishingGroupEntity sharingList = new PublishingGroupEntity();
        final List<GroupEntity> groups = new ArrayList<>(2);

        final UserEntity polandUser = accessDao.findActiveUserByUsername("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="8afae5e6ebe4eecae3ebeff9feefa4fae6">[email protected]</a>");
        final GroupEntity polandGroup = accessDao.findNationalGroup(polandUser);
        final UserEntity germanyUser = accessDao.findActiveUserByUsername("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9afdffe8f7fbf4e3daf3fbffe9eeffb4feff">[email protected]</a>");
        final GroupEntity germanyGroup = accessDao.findNationalGroup(germanyUser);
        final UserEntity norwayUser = accessDao.findActiveUserByUsername("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3759584540564e775e5652444352195958">[email protected]</a>");
        final GroupEntity norwayGroup = accessDao.findNationalGroup(norwayUser);

        groups.add(polandGroup);
        groups.add(germanyGroup);
        groups.add(norwayGroup);

        sharingList.setExternalId(SHARING_LIST_EXTERNAL_ID);
        sharingList.setGroup(authentication.getGroup());
        sharingList.setName(SHARING_LIST_NAME);
        sharingList.setList(groups);

        entityManager.persist(sharingList);

        assertThat(sharingList.getId(), is(not(nullValue())));

        final PublishingGroupEntity fetchedList1 = exchangeDao.getSharingListByExternalIdAndOwnerId(SHARING_LIST_EXTERNAL_ID, authentication.getGroup().getId());
        assertThat(sharingList, is(fetchedList1));

        groups.remove(norwayGroup);
        entityManager.merge(sharingList);
        final PublishingGroupEntity fetchedList2 = exchangeDao.getSharingListByExternalIdAndOwnerId(SHARING_LIST_EXTERNAL_ID, authentication.getGroup().getId());
        assertThat(sharingList, is(fetchedList2));
    }
}
