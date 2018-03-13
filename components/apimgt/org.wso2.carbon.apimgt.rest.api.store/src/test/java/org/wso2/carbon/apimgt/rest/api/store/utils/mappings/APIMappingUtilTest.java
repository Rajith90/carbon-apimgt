/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.apimgt.rest.api.store.utils.mappings;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIConsumer;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.APIStatus;
import org.wso2.carbon.apimgt.api.model.Tier;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dto.Environment;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.rest.api.store.dto.APIDTO;
import org.wso2.carbon.apimgt.rest.api.store.utils.RestAPIStoreUtils;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RestAPIStoreUtils.class, RestApiUtil.class, ServiceReferenceHolder.class})
public class APIMappingUtilTest {

    @Test
    public void testFromAPItoDTO() throws Exception {
        PowerMockito.mockStatic(RestApiUtil.class);
        PowerMockito.mockStatic(RestAPIStoreUtils.class);
        APIConsumer apiConsumer = Mockito.mock(APIConsumer.class);
        PowerMockito.doReturn(apiConsumer).when(RestApiUtil.class, "getLoggedInUserConsumer");
        PowerMockito.doReturn("new swagger").when(RestAPIStoreUtils.class, "removeXMediationScriptsFromSwagger",
                Mockito.anyString());
        String tenantDomain = "abc.com";
        Map<String, String> domainMappings = new HashMap<>();
        domainMappings.put(APIConstants.CUSTOM_URL, "https://abc.gw.com");
        Mockito.when(apiConsumer.getTenantDomainMappings(tenantDomain, APIConstants.API_DOMAIN_MAPPINGS_GATEWAY))
                .thenReturn(domainMappings);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        PowerMockito.doReturn(serviceReferenceHolder).when(ServiceReferenceHolder.class, "getInstance");
        APIManagerConfigurationService apiManagerConfigurationService = Mockito
                .mock(APIManagerConfigurationService.class);
        Mockito.doReturn(apiManagerConfigurationService).when(serviceReferenceHolder)
                .getAPIManagerConfigurationService();
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        Mockito.doReturn(apiManagerConfiguration).when(apiManagerConfigurationService).getAPIManagerConfiguration();

        Map<String, Environment> environmentMap = new HashMap<>();
        String envProdAndSandbox = "Production and Sandbox";
        Environment prodAndSandbox = new Environment();
        prodAndSandbox.setApiGatewayEndpoint("https://prodAndSandbox.com, http://prodAndSandbox.com");
        environmentMap.put(envProdAndSandbox, prodAndSandbox);
        Mockito.doReturn(environmentMap).when(apiManagerConfiguration).getApiGatewayEnvironments();

        API api = new API(new APIIdentifier("admin-AT-abc.com", "API1", "1.0.0"));
        api.setStatus(APIStatus.PUBLISHED);
        api.setTransports("https,http");
        api.setContext("/test");
        Set<String> environments = new HashSet<>();
        environments.add(envProdAndSandbox);
        environments.add("Sandbox");
        api.setEnvironments(environments);
        Set<Tier> availableTiers = new HashSet<>();
        availableTiers.add(new Tier("Unlimited"));
        api.addAvailableTiers(availableTiers);
        api.setThumbnailUrl("ThumbnailUrl");
        api.setUUID(UUID.randomUUID().toString());

        APIDTO apidto = APIMappingUtil.fromAPItoDTO(api, tenantDomain);
        Assert.assertNotNull("APIDto is returned", apidto);
    }


}

