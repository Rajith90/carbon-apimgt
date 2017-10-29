/*
 *
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dto.Environment;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ServiceReferenceHolder.class, APIUtil.class})
public class APIUtilTestGatewayEndpointsTest {
	
	static ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
    static APIManagerConfigurationService apimConfigService = Mockito.mock(APIManagerConfigurationService.class);
    static APIManagerConfiguration apimConfig = Mockito.mock(APIManagerConfiguration.class);
        
	
	@Test
	public void testGetGatewayendpoint() {
		
		Map<String, Environment> gatewayEnvironments = new HashMap<String, Environment>();
		Environment env1 = new Environment();
		env1.setType("hybrid");
		env1.setApiGatewayEndpoint("http://localhost:8280,https://localhost:8243");
		gatewayEnvironments.put("e1", env1);        
        
		doMockStatics();
		PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()
				.getAPIManagerConfiguration()
				.getApiGatewayEnvironments()).thenReturn(gatewayEnvironments);		
		
	    Assert.assertEquals("http://localhost:8280", APIUtil.getGatewayendpoint("http"));
	    Assert.assertEquals("https://localhost:8243", APIUtil.getGatewayendpoint("https"));
	    Assert.assertNotEquals("http://localhost:8280", APIUtil.getGatewayendpoint("https"));
		
	}
	
	@Test
	public void testGatewayEndpointsForMultipleEnvironments() {		

		Map<String, Environment> gatewayEnvironmentsMultiple = new HashMap<String, Environment>();
		Environment env2 = new Environment();
		env2.setType("production");
		env2.setApiGatewayEndpoint("http://prod:8280,https://prod:8243");
		
		Environment env3 = new Environment();
		env3.setType("sandbox");
		env3.setApiGatewayEndpoint("http://sandbox:8280,https://sandbox:8243");
		
		gatewayEnvironmentsMultiple.put("e2", env2);
		gatewayEnvironmentsMultiple.put("e3", env3);
		
		doMockStatics();
		PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()
				.getAPIManagerConfiguration()
				.getApiGatewayEnvironments()).thenReturn(gatewayEnvironmentsMultiple);	
		
		Assert.assertEquals("http://prod:8280", APIUtil.getGatewayendpoint("http"));
	    Assert.assertEquals("https://prod:8243", APIUtil.getGatewayendpoint("https"));
	    Assert.assertNotEquals("http://prod:8280", APIUtil.getGatewayendpoint("https"));
	    
	}
	
	@Test
	public void testGatewayEndpointsForMultipleHybridEnvironments() {		

		Map<String, Environment> gatewayEnvironmentsMultiple = new HashMap<String, Environment>();
		Environment env2 = new Environment();
		env2.setType("sandbox");
		env2.setApiGatewayEndpoint("http://sandbox1:8280, https://sandbox1:8243");
		
		Environment env3 = new Environment();
		env3.setType("hybrid");
		env3.setApiGatewayEndpoint("http://sandbox2:8280");
		
		gatewayEnvironmentsMultiple.put("e2", env2);
		gatewayEnvironmentsMultiple.put("e3", env3);
		
		doMockStatics();
		PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()
				.getAPIManagerConfiguration()
				.getApiGatewayEnvironments()).thenReturn(gatewayEnvironmentsMultiple);	
		
		Assert.assertEquals("http://sandbox2:8280", APIUtil.getGatewayendpoint("http"));
	    Assert.assertEquals("http://sandbox2:8280", APIUtil.getGatewayendpoint("https"));
	    Assert.assertNotEquals("https://sandbox1:8243", APIUtil.getGatewayendpoint("https"));
	    
	}
	
	@Test
	public void testGatewayEndpointsForMultipleSandboxEnvironments() {		

		// Using LinkedHashMap to preserve the insertion order !!
		Map<String, Environment> gatewayEnvironmentsMultiple = new LinkedHashMap<String, Environment>();
		Environment env2 = new Environment();
		env2.setType("sandbox");
		env2.setApiGatewayEndpoint("http://sandbox1:8280");
		
		Environment env3 = new Environment();
		env3.setType("sandbox");
		env3.setApiGatewayEndpoint("http://sandbox2:8280, https://sandbox2:8243");
		
		gatewayEnvironmentsMultiple.put("e2", env2);
		gatewayEnvironmentsMultiple.put("e3", env3);
		
		doMockStatics();
		PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()
				.getAPIManagerConfiguration()
				.getApiGatewayEnvironments()).thenReturn(gatewayEnvironmentsMultiple);	
		
		Assert.assertEquals("http://sandbox1:8280", APIUtil.getGatewayendpoint("http"));
	    Assert.assertEquals("http://sandbox1:8280", APIUtil.getGatewayendpoint("https"));
	    Assert.assertNotEquals("http://sandbox2:8280", APIUtil.getGatewayendpoint("http"));
	    
	}
	
	public static void doMockStatics() {
		PowerMockito.mockStatic(ServiceReferenceHolder.class);
		PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
		PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()).thenReturn(apimConfigService);
		PowerMockito.when(ServiceReferenceHolder.getInstance()
				.getAPIManagerConfigurationService()
				.getAPIManagerConfiguration()).thenReturn(apimConfig);
	}

}
