/*
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.apimgt.keymgt.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServiceReferenceHolder.class})
public class APIKeyMgtDataHolderTestCase {

    @Test
    public void testGetInitValues() throws Exception {

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);

        APIManagerConfigurationService apiManagerConfigurationService = Mockito
                .mock(APIManagerConfigurationService.class);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService())
                .thenReturn(apiManagerConfigurationService);
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);

        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.KEY_MANAGER_TOKEN_CACHE)).thenReturn("true");

        APIKeyMgtDataHolder.setAmConfigService(apiManagerConfigurationService);
        APIKeyMgtDataHolder.initData();
        Assert.assertTrue(APIKeyMgtDataHolder.getKeyCacheEnabledKeyMgt());
        Assert.assertFalse(APIKeyMgtDataHolder.getThriftServerEnabled());

        APIKeyMgtDataHolder.setKeyCacheEnabledKeyMgt(false);
        Assert.assertFalse(APIKeyMgtDataHolder.getKeyCacheEnabledKeyMgt());
        APIKeyMgtDataHolder.setThriftServerEnabled(true);
        Assert.assertTrue(APIKeyMgtDataHolder.getThriftServerEnabled());
    }

    @Test
    public void testInitData() throws Exception {

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);

        APIManagerConfigurationService apiManagerConfigurationService = Mockito
                .mock(APIManagerConfigurationService.class);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService())
                .thenReturn(apiManagerConfigurationService);
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);

        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.KEY_MANAGER_TOKEN_CACHE)).thenReturn("true");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_ENABLE_THRIFT_SERVER))
                .thenReturn("true");
        String applicationTokenScope = "applicationTokenScope";
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.APPLICATION_TOKEN_SCOPE))
                .thenReturn(applicationTokenScope);
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.ENABLE_JWT_GENERATION)).thenReturn("true");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.TOKEN_GENERATOR_IMPL)).thenReturn(null);

        APIKeyMgtDataHolder.setAmConfigService(apiManagerConfigurationService);
        APIKeyMgtDataHolder.initData();

        Assert.assertTrue(APIKeyMgtDataHolder.getKeyCacheEnabledKeyMgt());
        Assert.assertTrue(APIKeyMgtDataHolder.getThriftServerEnabled());
        Assert.assertTrue(APIKeyMgtDataHolder.isJwtGenerationEnabled());
        Assert.assertEquals(applicationTokenScope, APIKeyMgtDataHolder.getApplicationTokenScope());
        Assert.assertNotNull(APIKeyMgtDataHolder.getTokenGenerator());
    }

    @Test
    public void testInitDataWhenConfigurationIsNull() throws Exception {

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);

        APIManagerConfigurationService amService = Mockito.mock(APIManagerConfigurationService.class);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService())
                .thenReturn(amService);
        Mockito.when(amService.getAPIManagerConfiguration()).thenReturn(null);

        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        APIManagerConfigurationService apiManagerConfigurationService = Mockito
                .mock(APIManagerConfigurationService.class);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);

        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.KEY_MANAGER_TOKEN_CACHE)).thenReturn("true");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_ENABLE_THRIFT_SERVER))
                .thenReturn("true");
        APIKeyMgtDataHolder.setAmConfigService(apiManagerConfigurationService);
        APIKeyMgtDataHolder.initData();

        Assert.assertTrue(APIKeyMgtDataHolder.getKeyCacheEnabledKeyMgt());
        Assert.assertTrue(APIKeyMgtDataHolder.getThriftServerEnabled());
    }
}