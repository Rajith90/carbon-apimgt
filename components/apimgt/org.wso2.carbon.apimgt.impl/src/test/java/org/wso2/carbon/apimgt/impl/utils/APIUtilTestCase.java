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
import org.wso2.carbon.apimgt.impl.internal.APIManagerComponent;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.config.RegistryContext;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.service.TenantRegistryLoader;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.core.utils.RegistryUtils;
import org.wso2.carbon.registry.indexing.service.TenantIndexingLoader;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.mockito.Matchers.eq;

/**
 * Test class for APIUtil lines 2924 - 3161.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LogFactory.class, ServiceReferenceHolder.class, RegistryUtils.class, DocumentBuilderFactory.class,
        APIManagerComponent.class})
public class APIUtilTestCase {
    private RegistryContext registryContext;
    private UserRegistry registry;
    private RegistryService registryService;
    private Resource resource;
    private int tenantId = 1;

    @Before
    public void setup() throws org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {
        System.setProperty("carbon.home", "");
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        registryService = Mockito.mock(RegistryService.class);
        registry = Mockito.mock(UserRegistry.class);
        UserRegistry superTenantRegistry = Mockito.mock(UserRegistry.class);
        resource = Mockito.mock(Resource.class);
        registryContext = Mockito.mock(RegistryContext.class);
        TenantIndexingLoader tenantIndexingLoader = Mockito.mock(TenantIndexingLoader.class);
        TenantRegistryLoader tenantRegistryLoader = Mockito.mock(TenantRegistryLoader.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.mockStatic(APIManagerComponent.class);
        PowerMockito.mockStatic(RegistryUtils.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(-1234))).thenReturn(superTenantRegistry);
        Mockito.when(registry.newResource()).thenReturn(resource);
        Mockito.when(superTenantRegistry.newResource()).thenReturn(resource);
        PowerMockito.when(APIManagerComponent.getTenantRegistryLoader()).thenReturn(tenantRegistryLoader);
        Mockito.doNothing().when(resource).setContent(Matchers.anyString());
        Mockito.when(registryService.getConfigSystemRegistry(tenantId)).thenReturn(registry);

        RealmService realmService = Mockito.mock(RealmService.class);
        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        UserRealm userRealm = Mockito.mock(UserRealm.class);

        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
        Mockito.when(serviceReferenceHolder.getIndexLoaderService()).thenReturn(tenantIndexingLoader);

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

        APIUtil.loadTenantGAConfig(tenantId);
    }


    @Test
    public void testLoadTenantWorkFlowExtensions() throws APIManagementException,
            org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {

        PowerMockito.when(RegistryUtils.getAbsolutePath(registryContext,
                RegistryConstants.GOVERNANCE_REGISTRY_BASE_PATH) + APIConstants.GA_CONFIGURATION_LOCATION)
                .thenReturn("path");

        APIUtil.loadTenantWorkFlowExtensions(tenantId);
    }

    @Test
    public void testLoadTenantSelfSignUpConfigurations() throws APIManagementException,
            org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {

        PowerMockito.when(RegistryUtils.getAbsolutePath(registryContext,
                RegistryConstants.GOVERNANCE_REGISTRY_BASE_PATH) + APIConstants.GA_CONFIGURATION_LOCATION)
                .thenReturn("path");
        APIUtil.loadTenantSelfSignUpConfigurations(tenantId);

        //test for super tenant
        APIUtil.loadTenantSelfSignUpConfigurations(org.wso2.carbon.utils.multitenancy.MultitenantConstants.SUPER_TENANT_ID);
        Assert.assertTrue(true);
    }

    @Test
    public void testLoadTenantConf() throws APIManagementException, RegistryException {

        APIUtil.loadTenantConf(tenantId);

        // test when fle is found in the path
        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        APIUtil.loadTenantConf(tenantId);

        Assert.assertTrue(true);
    }

    @Test
    public void testLoadTenantConfRegistryException() throws RegistryException, IOException {

        //test RegistryException
        Mockito.when(registry.newResource()).thenThrow(new RegistryException("")).thenReturn(resource);
        try {
            APIUtil.loadTenantConf(tenantId);
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().startsWith("Error while saving tenant conf to the registry"));
        }
    }

    @Test(expected = APIManagementException.class)
    public void testCreateSelfSignUpRolesExceptions() throws RegistryException, IOException, APIManagementException {

        //test when APIConstants.SELF_SIGN_UP_CONFIG_LOCATION does not exists in registry
        APIUtil.createSelfSignUpRoles(tenantId);

        //test when APIConstants.SELF_SIGN_UP_CONFIG_LOCATION exists in registry

        Mockito.when(registry.resourceExists(APIConstants.SELF_SIGN_UP_CONFIG_LOCATION)).thenReturn(true);
        Mockito.when(registry.get(APIConstants.SELF_SIGN_UP_CONFIG_LOCATION)).thenReturn(resource);
        InputStream content = Mockito.mock(InputStream.class);
        Mockito.when(resource.getContentStream()).thenReturn(content);

        APIUtil.createSelfSignUpRoles(tenantId);
    }

    @Test
    public void testIsSubscriberRoleCreationEnabled() throws APIManagementException, RegistryException, FileNotFoundException {

        String signupConfig = "{\n" +
                "  \"SelfSignUp\": {\n" +
                "    \"EnableSignup\": \"true\",\n" +
                "    \"SignUpDomain\": \"PRIMARY\",\n" +
                "    \"AdminUserName\": \"${admin.username}\",\n" +
                "    \"AdminPassword\": \"${admin.password}\",\n" +
                "    \"SignUpRoles\": {\n" +
                "      \"SignUpRole\": {\n" +
                "        \"RoleName\": \"subscriber\",\n" +
                "        \"IsExternalRole\": \"false\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Mockito.when(registryService.getConfigSystemRegistry(0)).thenReturn(registry);

        Mockito.when(registry.resourceExists("/apimgt/applicationdata/tenant-conf.json")).thenReturn(true);
        Mockito.when(registry.get("/apimgt/applicationdata/tenant-conf.json")).thenReturn(resource);
        Mockito.when(resource.getContent()).thenReturn(signupConfig.getBytes(Charset.forName("UTF-8")));
        //Sunscriber role is not enabled in tenant-conf.json
        Assert.assertFalse(APIUtil.isSubscriberRoleCreationEnabled(tenantId));

        //Sunscriber role is enabled in tenant-conf.json
        signupConfig = "{\n" +
                "  \"SelfSignUp\": {\n" +
                "    \"EnableSignup\": \"true\",\n" +
                "    \"SignUpDomain\": \"PRIMARY\",\n" +
                "    \"AdminUserName\": \"${admin.username}\",\n" +
                "    \"AdminPassword\": \"${admin.password}\",\n" +
                "    \"SignUpRoles\": {\n" +
                "      \"SignUpRole\": {\n" +
                "        \"RoleName\": \"subscriber\",\n" +
                "        \"IsExternalRole\": \"false\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                " \"DefaultRoles\": {\n" +
                "    \"SubscriberRole\": {\n" +
                "        \"RoleName\": \"subscriber\",\n" +
                "        \"IsExternalRole\": \"true\"\n" +
                "        \"CreateOnTenantLoad\": true\n" +
                "      }\n" +
                "    }\n" +
                "}";
        Mockito.when(resource.getContent()).thenReturn(signupConfig.getBytes(Charset.forName("UTF-8")));
        Assert.assertTrue(APIUtil.isSubscriberRoleCreationEnabled(tenantId));
    }
}
