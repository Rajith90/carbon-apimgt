/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.apimgt.impl.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.jdbc.handlers.Handler;
import org.wso2.carbon.registry.core.jdbc.handlers.RequestContext;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;

import static org.wso2.carbon.base.CarbonBaseConstants.CARBON_HOME;


@RunWith(PowerMockRunner.class)
@PrepareForTest({APIUtil.class, Caching.class, PrivilegedCarbonContext.class})
public class APIConfigMediaTypeHandlerTest {

    private final int TENANT_ID = 1234;
    private final String TENANT_DOMAIN = "foo.com";
    private static final Log log = LogFactory.getLog(APIConfigMediaTypeHandlerTest.class);

    @Test
    public void testMediaTypeHandler() throws RegistryException {
        System.setProperty(CARBON_HOME, "");
        PrivilegedCarbonContext privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);
        Mockito.when(privilegedCarbonContext.getTenantDomain()).thenReturn(TENANT_DOMAIN);
        Mockito.when(privilegedCarbonContext.getTenantId()).thenReturn(TENANT_ID);

        Cache cache = Mockito.mock(Cache.class);
        Mockito.when(cache.containsKey(Mockito.anyString())).thenReturn(true);
        PowerMockito.mockStatic(Caching.class);
        CacheManager cacheManager = Mockito.mock(CacheManager.class);
        BDDMockito.when(Caching.getCacheManager(Mockito.anyString())).thenReturn(cacheManager);
        BDDMockito.when(cacheManager.getCache(Mockito.anyString())).thenReturn(cache);

        Handler handler = new APIConfigMediaTypeHandler();
        RequestContext requestContext = Mockito.mock(RequestContext.class);
        handler.put(requestContext);
    }
}