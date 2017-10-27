/*
 *
 *    Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *    WSO2 Inc. licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except
 *    in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.wso2.carbon.apimgt.impl.utils;

import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.config.RegistryContext;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.core.utils.RegistryUtils;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.io.IOException;

import static org.mockito.Matchers.eq;

/**
 * Test class for APIUtil lines 2924 - 3161.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LogFactory.class, ServiceReferenceHolder.class, RegistryUtils.class})
public class APIUtilTestCase {
    private RegistryContext registryContext;
    private UserRegistry registry;
    private RegistryService registryService;
    private Resource resource;

    @Before
    public void setup() throws org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {
        System.setProperty("carbon.home", "");
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        registryService = Mockito.mock(RegistryService.class);
        registry = Mockito.mock(UserRegistry.class);
        UserRegistry superTenantRegistry = Mockito.mock(UserRegistry.class);
        resource = Mockito.mock(Resource.class);
        registryContext = Mockito.mock(RegistryContext.class);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.mockStatic(RegistryUtils.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(-1234))).thenReturn(superTenantRegistry);
        Mockito.when(registry.newResource()).thenReturn(resource);
        Mockito.when(superTenantRegistry.newResource()).thenReturn(resource);
//        Mockito.when(registryContext.getBasePath()).thenReturn(resource);
        Mockito.doNothing().when(resource).setContent(Matchers.anyString());


//        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RealmService realmService = Mockito.mock(RealmService.class);
        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        UserRealm userRealm = Mockito.mock(UserRealm.class);
        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);

        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);

        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(realmService.getTenantUserRealm(Mockito.anyInt())).thenReturn(userRealm);
        org.wso2.carbon.user.api.AuthorizationManager authManager
                = Mockito.mock(org.wso2.carbon.user.api.AuthorizationManager.class);
        Mockito.when(userRealm.getAuthorizationManager()).thenReturn(authManager);
    }

    @Test
    public void testLoadTenantGAConfig() throws APIManagementException,
            org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {

        PowerMockito.when(RegistryUtils.getAbsolutePath(registryContext,
                RegistryConstants.GOVERNANCE_REGISTRY_BASE_PATH) + APIConstants.GA_CONFIGURATION_LOCATION)
                .thenReturn("path");

        APIUtil.loadTenantGAConfig(1);
    }


    @Test
    public void testLoadTenantWorkFlowExtensions() throws APIManagementException,
            org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {

        PowerMockito.when(RegistryUtils.getAbsolutePath(registryContext,
                RegistryConstants.GOVERNANCE_REGISTRY_BASE_PATH) + APIConstants.GA_CONFIGURATION_LOCATION)
                .thenReturn("path");

        APIUtil.loadTenantWorkFlowExtensions(1);
    }

    @Test
    public void testLoadTenantSelfSignUpConfigurations() throws APIManagementException,
            org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {

        PowerMockito.when(RegistryUtils.getAbsolutePath(registryContext,
                RegistryConstants.GOVERNANCE_REGISTRY_BASE_PATH) + APIConstants.GA_CONFIGURATION_LOCATION)
                .thenReturn("path");
        APIUtil.loadTenantSelfSignUpConfigurations(1);

        //test for super tenant
        APIUtil.loadTenantSelfSignUpConfigurations(org.wso2.carbon.utils.multitenancy.MultitenantConstants.SUPER_TENANT_ID);
        Assert.assertTrue(true);
    }

    @Test
    public void testLoadTenantConf() throws APIManagementException, RegistryException {

        Mockito.when(registryService.getConfigSystemRegistry(1)).thenReturn(registry);
        APIUtil.loadTenantConf(1);

        // test when fle is found in the path
        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        APIUtil.loadTenantConf(1);

        Assert.assertTrue(true);
    }

    @Test
    public void testLoadTenantConfRegistryException() throws RegistryException, IOException {

        Mockito.when(registryService.getConfigSystemRegistry(1)).thenReturn(registry);

        //test RegistryException
        Mockito.when(registry.newResource()).thenThrow(new RegistryException("")).thenReturn(resource);
        try {
            APIUtil.loadTenantConf(1);
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().startsWith("Error while saving tenant conf to the registry"));
        }
    }
}
