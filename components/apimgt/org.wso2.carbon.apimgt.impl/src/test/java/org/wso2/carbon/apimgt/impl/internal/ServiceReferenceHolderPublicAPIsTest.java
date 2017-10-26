/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.apimgt.impl.internal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapterService;
import org.wso2.carbon.registry.core.jdbc.EmbeddedRegistryService;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.indexing.service.TenantIndexingLoader;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.ConfigurationContextService;

import java.io.IOException;

public class ServiceReferenceHolderPublicAPIsTest {

    private ServiceReferenceHolder serviceReferenceHolder;

    @Before public void setup() throws IOException {
        serviceReferenceHolder = ServiceReferenceHolder.getInstance();
    }

    @Test public void testPublicGetterSetterAPIs() throws Exception {
        RegistryService mockedRegistryService = Mockito.mock(EmbeddedRegistryService.class);
        serviceReferenceHolder.setRegistryService(mockedRegistryService);
        Assert.assertEquals(mockedRegistryService, serviceReferenceHolder.getRegistryService());

        TenantIndexingLoader mockedTenantIndexingLoader = Mockito.mock(TenantIndexingLoader.class);
        serviceReferenceHolder.setIndexLoaderService(mockedTenantIndexingLoader);
        Assert.assertEquals(mockedTenantIndexingLoader, serviceReferenceHolder.getIndexLoaderService());

        OutputEventAdapterService mockedOutputEventAdapterService = Mockito.mock(OutputEventAdapterService.class);
        serviceReferenceHolder.setOutputEventAdapterService(mockedOutputEventAdapterService);
        Assert.assertEquals(mockedOutputEventAdapterService, serviceReferenceHolder.getOutputEventAdapterService());

        APIManagerConfigurationService mockedAPIManagerConfigurationService = Mockito
                .mock(APIManagerConfigurationService.class);
        serviceReferenceHolder.setAPIManagerConfigurationService(mockedAPIManagerConfigurationService);
        Assert.assertEquals(mockedAPIManagerConfigurationService,
                serviceReferenceHolder.getAPIManagerConfigurationService());

        RealmService mockedRealmService = Mockito.mock(RealmService.class);
        serviceReferenceHolder.setRealmService(mockedRealmService);
        Assert.assertEquals(mockedRealmService, serviceReferenceHolder.getRealmService());

        UserRealm mockedUserRealm = Mockito.mock(UserRealm.class);
        ServiceReferenceHolder.setUserRealm(mockedUserRealm);
        Assert.assertEquals(mockedUserRealm, ServiceReferenceHolder.getUserRealm());

        ConfigurationContextService mockedConfigurationContextService = Mockito.mock(ConfigurationContextService.class);
        ServiceReferenceHolder.setContextService(mockedConfigurationContextService);
        Assert.assertEquals(mockedConfigurationContextService, ServiceReferenceHolder.getContextService());
    }
}