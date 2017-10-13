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
import org.wso2.carbon.apimgt.keymgt.APIKeyMgtException;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationRequestDTO;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.sql.Connection;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({APIKeyMgtDataHolder.class,IdentityDatabaseUtil.class})
public class APIKeyMgtUtilTestCase {

    @Test
    public void testConstructParameterMap() throws Exception {

        OAuth2TokenValidationRequestDTO.TokenValidationContextParam param1 = new OAuth2TokenValidationRequestDTO()
                .new TokenValidationContextParam();
        param1.setKey("Key1");
        param1.setValue("Value1");
        OAuth2TokenValidationRequestDTO.TokenValidationContextParam param2 = new OAuth2TokenValidationRequestDTO()
                .new TokenValidationContextParam();
        param2.setKey("Key2");
        param2.setValue("Value2");
        OAuth2TokenValidationRequestDTO.TokenValidationContextParam[] params = {param1, param2};

        Map<String, String> paramMap = APIKeyMgtUtil.constructParameterMap(params);
        Assert.assertEquals(2, paramMap.size());
        Assert.assertEquals("Value1", paramMap.get("Key1"));
        Assert.assertEquals("Value2", paramMap.get("Key2"));
    }

    @Test
    public void testConstructParameterMapForNull() throws Exception {

        Map<String, String> paramMap = APIKeyMgtUtil.constructParameterMap(null);
        Assert.assertNull(paramMap);
    }

    @Test
    public void testGetTenantDomainFromTenantId() throws Exception {

        PowerMockito.mockStatic(APIKeyMgtDataHolder.class);
        RealmService realmService = Mockito.mock(RealmService.class);
        PowerMockito.when(APIKeyMgtDataHolder.getRealmService()).thenReturn(realmService);

        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(tenantManager.getDomain(Mockito.anyInt())).thenReturn("carbon.super");

        Assert.assertNotNull(APIKeyMgtUtil.getTenantDomainFromTenantId(-1234));
    }


    @Test(expected = APIKeyMgtException.class)
    public void testGetTenantDomainFromTenantIdForException() throws Exception {

        PowerMockito.mockStatic(APIKeyMgtDataHolder.class);
        RealmService realmService = Mockito.mock(RealmService.class);
        PowerMockito.when(APIKeyMgtDataHolder.getRealmService()).thenReturn(realmService);

        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.doThrow(UserStoreException.class).when(tenantManager).getDomain(Mockito.anyInt());

        APIKeyMgtUtil.getTenantDomainFromTenantId(-1234);
    }

    @Test
    public void testGetDBConnection() throws Exception {

        PowerMockito.mockStatic(IdentityDatabaseUtil.class);
        Connection connection = Mockito.mock(Connection.class);
        PowerMockito.when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        Assert.assertNotNull(APIKeyMgtUtil.getDBConnection());
    }

}