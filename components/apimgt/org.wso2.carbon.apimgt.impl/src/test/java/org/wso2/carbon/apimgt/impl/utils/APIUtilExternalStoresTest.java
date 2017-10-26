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
 *
 */
package org.wso2.carbon.apimgt.impl.utils;

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.APIStore;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.ResourceImpl;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServiceReferenceHolder.class, APIUtil.class})
public class APIUtilExternalStoresTest {

	ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
	APIManagerConfigurationService apimConfigService = Mockito.mock(APIManagerConfigurationService.class);
    APIManagerConfiguration apimConfig = Mockito.mock(APIManagerConfiguration.class);
    RegistryService registryService = Mockito.mock(RegistryService.class);
    UserRegistry userRegistry = Mockito.mock(UserRegistry.class);
 
    
    @Test
    public void testExternalStoresFromConfigFile() throws Exception {
    	
    	Set<APIStore> externalAPIStores = new HashSet<APIStore>();
    	APIStore store1 = new APIStore();
    	store1.setDisplayName("store1");
    	store1.setName("store1");
    	store1.setEndpoint("http://host.store1.com");
    	
    	APIStore store2 = new APIStore();
    	store2.setDisplayName("store2");
    	store2.setName("store2");
    	store2.setEndpoint("http://host.store2.com");
    	
    	externalAPIStores.add(store1);
    	externalAPIStores.add(store2);
    	
    	doMockStatics();        
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()
				.getAPIManagerConfiguration()
				.getExternalAPIStores()).thenReturn(externalAPIStores);   	
    	
    	Set<APIStore> apiStores = APIUtil.getExternalStores(1);
    	Assert.assertEquals(2, apiStores.size());
    	
    	Mockito.verify(apimConfigService, Mockito.atLeastOnce()).getAPIManagerConfiguration();
    	Mockito.verify(registryService, Mockito.never()).getGovernanceSystemRegistry(1);
    }
    
    @Test
    public void testNullExternalStoresFromConfigFile() throws Exception {
    	
    	Set<APIStore> externalAPIStores = new HashSet<APIStore>();
    	Resource resource = createResourceWithOneExternalStore();
    	
    	doMockStatics();
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()
				.getAPIManagerConfiguration()
				.getExternalAPIStores()).thenReturn(externalAPIStores);	
    	doMockRegistry();
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getRegistryService()
				.getGovernanceSystemRegistry(1)).thenReturn(userRegistry);
    	PowerMockito.when(userRegistry.resourceExists("/apimgt/externalstores/external-api-stores.xml")).thenReturn(true);
    	PowerMockito.when(userRegistry.get("/apimgt/externalstores/external-api-stores.xml")).thenReturn(resource);    	
    	
    	Set<APIStore> apiStores = APIUtil.getExternalStores(1);
        Assert.assertEquals(1, apiStores.size());
        
        Mockito.verify(registryService, Mockito.atLeastOnce()).getGovernanceSystemRegistry(1);
    }
    
    @Test
    public void testExternalStoresFromRegistry() throws Exception {
    	Set<APIStore> externalAPIStores = null;
    	Resource resource = createResourceWithOneExternalStore();
    	
    	doMockStatics();
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()
				.getAPIManagerConfiguration()
				.getExternalAPIStores()).thenReturn(externalAPIStores);	
    	doMockRegistry();
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getRegistryService()
				.getGovernanceSystemRegistry(1)).thenReturn(userRegistry);
    	PowerMockito.when(userRegistry.resourceExists("/apimgt/externalstores/external-api-stores.xml")).thenReturn(true);
    	PowerMockito.when(userRegistry.get("/apimgt/externalstores/external-api-stores.xml")).thenReturn(resource);    	
    	
    	Set<APIStore> apiStores = APIUtil.getExternalStores(1);
        Assert.assertEquals(1, apiStores.size());
        
        Mockito.verify(registryService, Mockito.atLeastOnce()).getGovernanceSystemRegistry(1);
    }
    
    @Test(expected = APIManagementException.class)
    public void testExternalStoresRegistryException() throws Exception {
    	testExceptionCommon();
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getRegistryService()
				.getGovernanceSystemRegistry(1)).thenThrow(RegistryException.class);
    	
    	APIUtil.getExternalStores(1);
                
        Mockito.verify(registryService, Mockito.atLeastOnce()).getGovernanceSystemRegistry(1);
    }
    
    @Test(expected = APIManagementException.class)
    public void testExternalStoresXMLStreamException() throws Exception {
    	testExceptionCommon();
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getRegistryService()
				.getGovernanceSystemRegistry(1)).thenThrow(XMLStreamException.class);
    	
    	APIUtil.getExternalStores(1);
                
        Mockito.verify(registryService, Mockito.atLeastOnce()).getGovernanceSystemRegistry(1);
    }
    
    @Test(expected = APIManagementException.class)
    public void testExternalStoresClassNotFoundException() throws Exception {
    	testExceptionCommon();
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getRegistryService()
				.getGovernanceSystemRegistry(1)).thenThrow(ClassNotFoundException.class);
    	
    	APIUtil.getExternalStores(1);
                
        Mockito.verify(registryService, Mockito.atLeastOnce()).getGovernanceSystemRegistry(1);
    }
    
    @Test(expected = APIManagementException.class)
    public void testExternalStoresInstantiationException() throws Exception {
    	testExceptionCommon();
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getRegistryService()
				.getGovernanceSystemRegistry(1)).thenThrow(InstantiationException.class);
    	
    	APIUtil.getExternalStores(1);
                
        Mockito.verify(registryService, Mockito.atLeastOnce()).getGovernanceSystemRegistry(1);
    }
    
    @Test(expected = APIManagementException.class)
    public void testExternalStoresIllegalAccessException() throws Exception {
    	testExceptionCommon();
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getRegistryService()
				.getGovernanceSystemRegistry(1)).thenThrow(IllegalAccessException.class);
    	APIUtil.getExternalStores(1);                
        Mockito.verify(registryService, Mockito.atLeastOnce()).getGovernanceSystemRegistry(1);
    }
    
    
    
    private void testExceptionCommon() throws Exception{
    	Set<APIStore> externalAPIStores = null;
    	Resource resource = createResourceWithOneExternalStore();    	
    	doMockStatics();
    	PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()
				.getAPIManagerConfiguration()
				.getExternalAPIStores()).thenReturn(externalAPIStores);	
    	doMockRegistry();
    	PowerMockito.when(userRegistry.resourceExists("/apimgt/externalstores/external-api-stores.xml")).thenReturn(true);
    	PowerMockito.when(userRegistry.get("/apimgt/externalstores/external-api-stores.xml")).thenReturn(resource);  
    }

	private Resource createResourceWithOneExternalStore() throws RegistryException {
		Resource resource = new ResourceImpl();
    	String externalAPIStoresDescription = "<ExternalAPIStores>"
    			+ "<StoreURL>http://localhost:9763/store</StoreURL>"
    			+ "<ExternalAPIStore id=\"Store2\" type=\"wso2\" className=\"org.wso2.carbon.apimgt.impl.publishers.WSO2APIPublisher\">"
    			+ "<DisplayName>Store1</DisplayName><Endpoint>http://localhost:9763/store</Endpoint>"
    			+ "<Username>xxxx</Username><Password>xxxx</Password>"
    			+ "</ExternalAPIStore>"
    			+ "</ExternalAPIStores>";
    	resource.setContent(externalAPIStoresDescription.getBytes());
		return resource;
	}
    
	public void doMockStatics() {
		doMockServiceReferenceHolder();
		PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()).thenReturn(apimConfigService);
		PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()
				.getAPIManagerConfiguration()).thenReturn(apimConfig);
	}
	
	public void doMockServiceReferenceHolder () {
		PowerMockito.mockStatic(ServiceReferenceHolder.class);
		PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);		
	}
	
	public void doMockRegistry() {
		doMockServiceReferenceHolder();
		PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getRegistryService()).thenReturn(registryService);	
		
	}
}
