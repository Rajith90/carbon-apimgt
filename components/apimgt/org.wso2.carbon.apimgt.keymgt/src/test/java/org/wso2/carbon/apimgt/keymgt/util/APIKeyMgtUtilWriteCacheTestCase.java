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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dto.APIKeyValidationInfoDTO;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.keymgt.internal.ServiceReferenceHolder;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.security.AuthenticatorsConfiguration;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;

import javax.cache.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(PowerMockRunner.class) @PrepareForTest({ Caching.class, APIKeyMgtDataHolder.class, IdentityDatabaseUtil.class,
        PrivilegedCarbonContext.class, APIUtil.class, ServiceReferenceHolder.class, AuthenticatorsConfiguration.class })
public class APIKeyMgtUtilWriteCacheTestCase {
    @Before
    public void init() {
        System.setProperty("carbon.home", "");
        PrivilegedCarbonContext carbonContext;
        carbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);

        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(carbonContext);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId()).thenReturn(-1234);
        PowerMockito.doNothing().when(carbonContext).setTenantDomain("carbon.super", true);

        APIIdentifier mockedIdentifier = new APIIdentifier("mockProviderName", "mockApiName",
                "mockedVersion");
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId()).thenReturn(-1234);
    }

    @Test
    public void testWriteToKeyManagerCache() {
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        APIManagerConfigurationService amConfigService = Mockito.mock(APIManagerConfigurationService.class);
        APIManagerConfiguration amConfig = Mockito.mock(APIManagerConfiguration.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);

        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService()).thenReturn(amConfigService);
        Mockito.when(amConfigService.getAPIManagerConfiguration()).thenReturn(amConfig);
        String cacheExpTime = "9000";
        String sampleCacheKey = UUID.randomUUID().toString();
        PowerMockito.when(amConfig.getFirstProperty(APIConstants.TOKEN_CACHE_EXPIRY)).thenReturn(cacheExpTime);
        Cache mockedCache = PowerMockito.mock(Cache.class);
        PowerMockito.mockStatic(Caching.class);
        CacheManager mockedCacheManager = PowerMockito.mock(CacheManager.class);
        Mockito.when(Caching.getCacheManager(APIConstants.API_MANAGER_CACHE_MANAGER)).thenReturn(mockedCacheManager);
        CacheBuilder mockedCacheBuilder = PowerMockito.mock(CacheBuilder.class);
        Mockito.when(mockedCacheManager.createCacheBuilder(APIConstants.KEY_CACHE_NAME)).thenReturn(mockedCacheBuilder);
        Mockito.when(mockedCacheBuilder.build()).thenReturn(mockedCache);
        Mockito.when(mockedCacheBuilder.setStoreByValue(Mockito.anyBoolean())).thenReturn(mockedCacheBuilder);
        PowerMockito.when(mockedCache.get(sampleCacheKey)).thenReturn(Mockito.mock(APIKeyValidationInfoDTO.class));
        Mockito.when(mockedCacheBuilder.setExpiry(CacheConfiguration.ExpiryType.MODIFIED,
                new CacheConfiguration.Duration(TimeUnit.SECONDS, Long.parseLong(cacheExpTime))))
                .thenReturn(mockedCacheBuilder);
        Mockito.when(mockedCacheBuilder.setExpiry(CacheConfiguration.ExpiryType.ACCESSED,
                new CacheConfiguration.Duration(TimeUnit.SECONDS, Long.parseLong(cacheExpTime))))
                .thenReturn(mockedCacheBuilder);

        APIKeyValidationInfoDTO sampleAPIKeyValidationInfoDTO = new APIKeyValidationInfoDTO();
        APIKeyMgtUtil.writeToKeyManagerCache(sampleCacheKey,sampleAPIKeyValidationInfoDTO);
    }
}