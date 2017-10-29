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

package org.wso2.carbon.apimgt.impl.template;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.APIStatus;
import org.wso2.carbon.apimgt.api.model.URITemplate;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.user.core.service.RealmService;

import java.util.LinkedHashSet;
import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServiceReferenceHolder.class, APIUtil.class})
public class ResourceConfigContextTest {

    @Test
    public void testResourceConfigContext() throws Exception {

        API api = new API(new APIIdentifier("admin", "TestAPI", "1.0.0"));
        api.setStatus(APIStatus.CREATED);
        api.setContextTemplate("/");
        api.setUriTemplates(setAPIUriTemplates());
        ConfigContext configcontext = new APIConfigContext(api);
        ResourceConfigContext resourceConfigContext = new ResourceConfigContext(configcontext, api);
        resourceConfigContext.validate();
        Assert.assertNotNull(resourceConfigContext.getContext().get("resources"));
        Assert.assertNotNull(resourceConfigContext.getContext().get("apiStatus"));
        //assign an empty URITemplate set and check the result
        Set<URITemplate> uriTemplates = new LinkedHashSet<URITemplate>();
        api.setUriTemplates(uriTemplates);
        configcontext = new APIConfigContext(api);
        resourceConfigContext = new ResourceConfigContext(configcontext, api);
        String errorClass = "org.wso2.carbon.apimgt.api.APIManagementException";
        String expectedErrorMessage = "At least one resource is required";
        try {
            resourceConfigContext.validate();
        } catch (APIManagementException e) {
            Assert.assertTrue(errorClass.equalsIgnoreCase(e.getClass().getName()));
            Assert.assertTrue(expectedErrorMessage.equalsIgnoreCase(e.getMessage()));
        }
        //set a null value for URITemplate and check the result
        api.setUriTemplates(null);
        configcontext = new APIConfigContext(api);
        resourceConfigContext = new ResourceConfigContext(configcontext, api);
        try {
            resourceConfigContext.validate();
        } catch (APIManagementException e) {
            Assert.assertTrue(errorClass.equalsIgnoreCase(e.getClass().getName()));
            Assert.assertTrue(expectedErrorMessage.equalsIgnoreCase(e.getMessage()));
        }
    }

    private Set<URITemplate> setAPIUriTemplates() {
        Set<URITemplate> uriTemplates = new LinkedHashSet<URITemplate>();
        URITemplate template = new URITemplate();
        template.setUriTemplate("/test");
        template.setHTTPVerb("GET");
        template.setThrottlingTier("Unlimited");
        template.setAuthType("Application");
        template.setResourceURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        template.setResourceSandboxURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        uriTemplates.add(template);
        return uriTemplates;
    }

    @Test
    public void testValidateWithoutURITemplates() throws  Exception {
        API api = new API(new APIIdentifier("admin", "TestAPI", "1.0.0"));
        api.setStatus(APIStatus.CREATED);
        api.setContextTemplate("/");
        ConfigContext configcontext = new APIConfigContext(api);
        ResourceConfigContext resourceConfigContext = new ResourceConfigContext(configcontext, api);
        try {
            resourceConfigContext.validate();
            Assert.fail("ResourceConfigContext validation passed while URI templates are not set");
        } catch (APIManagementException e) {
            Assert.assertTrue("At least one resource is required".equals(e.getMessage()));
        }

        Set resources = (Set)resourceConfigContext.getContext().get("resources");
        Assert.assertTrue(resources.isEmpty());
    }

    @Test
    public void testValidatewhenFaultSequenceIsDefined() throws Exception {
        APIIdentifier apiIdentifier = new APIIdentifier("admin", "TestAPI", "1.0.0");
        API api = new API(apiIdentifier);
        api.setStatus(APIStatus.CREATED);
        api.setContextTemplate("/");
        Set<URITemplate> uriTemplates = new LinkedHashSet<URITemplate>();
        URITemplate template = new URITemplate();
        template.setUriTemplate("/test");
        template.setHTTPVerb("GET");
        template.setThrottlingTier("Unlimited");
        template.setAuthType("Application");
        template.setResourceURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        template.setResourceSandboxURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        uriTemplates.add(template);
        api.setUriTemplates(uriTemplates);
        api.setFaultSequence("fault-sequence");
        ConfigContext configcontext = new APIConfigContext(api);
        ResourceConfigContext resourceConfigContext = new ResourceConfigContext(configcontext, api);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        Mockito.when(serviceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        RealmService realmService = Mockito.mock(RealmService.class);
        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(tenantManager.getTenantId("carbon.super")).thenReturn(-1234);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        UserRegistry userRegistry = Mockito.mock(UserRegistry.class);
        Mockito.when(registryService.getGovernanceSystemRegistry(-1234)).thenReturn(userRegistry);

        resourceConfigContext.validate();
        Assert.assertNotNull(resourceConfigContext.getContext().get("resources"));
        Assert.assertNotNull(resourceConfigContext.getContext().get("apiStatus"));

        //UserStoreException
        Mockito.when(tenantManager.getTenantId("carbon.super")).thenThrow(UserStoreException.class);
        try {
            resourceConfigContext.validate();
            Assert.fail("UserStoreException was not thrown as expected.");
        } catch (APIManagementException e) {
            Assert.assertTrue("Error while retrieving tenant Id from admin".equals(e.getMessage()));
        }
    }

    @Test
    public void testValidatewhenIsPerAPISequenceTrue() throws Exception {
        APIIdentifier apiIdentifier = new APIIdentifier("admin", "TestAPI", "1.0.0");
        API api = new API(apiIdentifier);
        api.setStatus(APIStatus.CREATED);
        api.setContextTemplate("/");
        Set<URITemplate> uriTemplates = new LinkedHashSet<URITemplate>();
        URITemplate template = new URITemplate();
        template.setUriTemplate("/test");
        template.setHTTPVerb("GET");
        template.setThrottlingTier("Unlimited");
        template.setAuthType("Application");
        template.setResourceURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        template.setResourceSandboxURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        uriTemplates.add(template);
        api.setUriTemplates(uriTemplates);
        api.setFaultSequence("fault-sequence");
        ConfigContext configcontext = new APIConfigContext(api);
        ResourceConfigContext resourceConfigContext = new ResourceConfigContext(configcontext, api);

        PowerMockito.mockStatic(APIUtil.class);
        APIUtil apiUtil = Mockito.mock(APIUtil.class);
        Mockito.when(apiUtil.isSequenceDefined("fault-sequence")).thenReturn(true);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        Mockito.when(serviceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        RealmService realmService = Mockito.mock(RealmService.class);
        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(tenantManager.getTenantId("carbon.super")).thenReturn(-1234);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        UserRegistry userRegistry = Mockito.mock(UserRegistry.class);
        Mockito.when(registryService.getGovernanceSystemRegistry(-1234)).thenReturn(userRegistry);
        Mockito.when(apiUtil.isPerAPISequence("fault-sequence", -1234, apiIdentifier, "fault")).thenReturn(true);
        Mockito.when(apiUtil.getSequenceExtensionName(api)).thenReturn("admin--TestAPI:v1.0.0");

        resourceConfigContext.validate();

        Assert.assertTrue("admin--TestAPI:v1.0.0--Fault".equals(resourceConfigContext.getContext().get("faultSequence")));

        //APIManagementException
        Mockito.when(apiUtil.isPerAPISequence("fault-sequence", -1234, apiIdentifier, "fault")).thenThrow(APIManagementException.class);
        try {
            resourceConfigContext.validate();
            Assert.fail("APIManagementException was not thrown as expected.");
        } catch (APIManagementException e) {
            Assert.assertTrue("Error while checking whether sequence fault-sequence is a per API sequence.".equals(e.getMessage()));
        }
    }

    @Test
    public void testValidatewhenProviderNameContainsAT() throws Exception {
        APIIdentifier apiIdentifier = new APIIdentifier("admin-AT-carbon.super", "TestAPI", "1.0.0");
        API api = new API(apiIdentifier);
        api.setStatus(APIStatus.CREATED);
        api.setContextTemplate("/");
        Set<URITemplate> uriTemplates = new LinkedHashSet<URITemplate>();
        URITemplate template = new URITemplate();
        template.setUriTemplate("/test");
        template.setHTTPVerb("GET");
        template.setThrottlingTier("Unlimited");
        template.setAuthType("Application");
        template.setResourceURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        template.setResourceSandboxURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        uriTemplates.add(template);
        api.setUriTemplates(uriTemplates);
        api.setFaultSequence("fault-sequence");
        ConfigContext configcontext = new APIConfigContext(api);
        ResourceConfigContext resourceConfigContext = new ResourceConfigContext(configcontext, api);

        PowerMockito.mockStatic(APIUtil.class);
        APIUtil apiUtil = Mockito.mock(APIUtil.class);
        Mockito.when(apiUtil.isSequenceDefined("fault-sequence")).thenReturn(true);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        Mockito.when(serviceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        RealmService realmService = Mockito.mock(RealmService.class);
        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(tenantManager.getTenantId("carbon.super")).thenReturn(-1234);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        UserRegistry userRegistry = Mockito.mock(UserRegistry.class);
        Mockito.when(registryService.getGovernanceSystemRegistry(-1234)).thenReturn(userRegistry);

        resourceConfigContext.validate();

        Assert.assertNotNull(resourceConfigContext.getContext().get("resources"));
        Assert.assertNotNull(resourceConfigContext.getContext().get("apiStatus"));
    }
}
