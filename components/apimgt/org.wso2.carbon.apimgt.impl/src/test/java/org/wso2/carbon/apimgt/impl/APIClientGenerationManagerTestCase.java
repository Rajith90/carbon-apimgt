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

package org.wso2.carbon.apimgt.impl;

import io.swagger.codegen.config.CodegenConfigurator;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.impl.definitions.APIDefinitionFromSwagger20;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.wso2.carbon.base.CarbonBaseConstants.CARBON_HOME;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrivilegedCarbonContext.class, APIUtil.class, MultitenantUtils.class, ServiceReferenceHolder.class,
        APIDefinitionFromSwagger20.class, APIClientGenerationManager.class, SwaggerParser.class, Json.class})
public class APIClientGenerationManagerTestCase {

    private ServiceReferenceHolder serviceReferenceHolder;
    private RegistryService registryService;
    private UserRegistry userRegistry;
    private APIDefinitionFromSwagger20 apiDefinitionFromSwagger20;
    private SwaggerParser swaggerParser;
    private File file;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private CodegenConfigurator codegenConfigurator;
    private APIManagerConfigurationService apiManagerConfigurationService;
    private APIManagerConfiguration apiManagerConfiguration;

    @Before
    public void init() throws Exception {
       PowerMockito.mockStatic(APIUtil.class);
       PowerMockito.mockStatic(Json.class);
       PowerMockito.mockStatic(MultitenantUtils.class);
       PowerMockito.mockStatic(APIDefinitionFromSwagger20.class);
       PowerMockito.mockStatic(SwaggerParser.class);
       serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
       registryService = Mockito.mock(RegistryService.class);
       userRegistry = Mockito.mock(UserRegistry.class);
       swaggerParser = Mockito.mock(SwaggerParser.class);
       apiDefinitionFromSwagger20 = Mockito.mock(APIDefinitionFromSwagger20.class);
       file = Mockito.mock(File.class);
       fileWriter = Mockito.mock(FileWriter.class);
       bufferedWriter = Mockito.mock(BufferedWriter.class);
       codegenConfigurator = Mockito.mock(CodegenConfigurator.class);
       apiManagerConfigurationService = Mockito.mock(APIManagerConfigurationService.class);
       apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
       PowerMockito.mockStatic(ServiceReferenceHolder.class);
       PowerMockito.whenNew(APIDefinitionFromSwagger20.class).withAnyArguments().
               thenReturn(apiDefinitionFromSwagger20);
       PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(file);
       PowerMockito.whenNew(SwaggerParser.class).withAnyArguments().thenReturn(swaggerParser);
       PowerMockito.whenNew(FileWriter.class).withAnyArguments().thenReturn(fileWriter);
       PowerMockito.whenNew(BufferedWriter.class).withAnyArguments().thenReturn(bufferedWriter);
       PowerMockito.whenNew(CodegenConfigurator.class).withAnyArguments().thenReturn(codegenConfigurator);

    }

    @Test
    public void getSupportedSDKLanguagesTest() {
        APIClientGenerationManager apiClientGenerationManager =  new APIClientGenerationManagerWrapper();
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService()).
                thenReturn(apiManagerConfigurationService);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
        Mockito.when(apiManagerConfiguration.getFirstProperty(Mockito.anyString())).thenReturn("java,android");
        assertEquals("java,android", apiClientGenerationManager.getSupportedSDKLanguages());
    }

    @Test
    public void testGenerateSDK() throws Exception {
        APIClientGenerationManager apiClientGenerationManager =  new APIClientGenerationManagerWrapper();

        try {
            apiClientGenerationManager.generateSDK(null, "testAPI", "1.0.0",
                    "testProvider");
            Assert.fail("API Client Generation exception not thrown for error scenario with empty SDK");
        } catch (APIClientGenerationException e) {
            assertEquals("SDK Language, API Name, API Version or API Provider should not be null.",
                    e.getMessage());
        }

        System.setProperty(CARBON_HOME, "");
        PrivilegedCarbonContext privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).
                thenReturn(privilegedCarbonContext);
        PowerMockito.when(MultitenantUtils.getTenantDomain(Mockito.anyString())).thenReturn("carbon.super");
        PowerMockito.when(APIUtil.class, "loadTenantRegistry", Mockito.anyInt()).
                thenThrow(RegistryException.class);

        try {
            apiClientGenerationManager.generateSDK("testLanguage", "testAPI", "1.0.0",
                    "testProvider");
            Assert.fail("Registry exception not thrown for error scenario");
        } catch (APIClientGenerationException e) {
            assertEquals("Failed to load tenant registry for tenant ID : -1234",
                    e.getMessage());
        }
        PowerMockito.doNothing().when(APIUtil.class, "loadTenantRegistry", Mockito.anyInt());
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceUserRegistry(Mockito.anyString(), Mockito.anyString())).
                thenReturn(userRegistry);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService()).
                thenReturn(apiManagerConfigurationService);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
        Mockito.when(apiDefinitionFromSwagger20.getAPIDefinition((APIIdentifier)Mockito.anyObject(),
                (Registry)Mockito.anyObject())).thenThrow(APIManagementException.class);
        Swagger swagger = Mockito.mock(Swagger.class);
        Mockito.when(swaggerParser.parse("testSwagger")).thenReturn(swagger);
        Mockito.when(Json.pretty(swagger)).thenReturn("testData");
        Mockito.when(file.mkdir()).thenReturn(true);
        try {
            apiClientGenerationManager.generateSDK("testLanguage", "testAPI", "1.0.0",
                    "testProvider");
            Assert.fail("API Client Generation exception not thrown for error scenario");
        } catch (APIClientGenerationException e) {
            assertEquals("Error loading swagger file for API testAPI from registry.", e.getMessage());
        }
    }

}
