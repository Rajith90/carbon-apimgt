/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.APIPublisher;
import org.wso2.carbon.apimgt.api.model.APIStore;
import org.wso2.carbon.apimgt.impl.dto.Environment;
import org.wso2.carbon.apimgt.impl.dto.ThrottleProperties;
import org.wso2.carbon.apimgt.impl.dto.WorkflowProperties;
import org.wso2.carbon.apimgt.impl.publishers.WSO2APIPublisher;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

@RunWith (PowerMockRunner.class)
@PrepareForTest({APIManagerConfiguration.class, APIUtil.class, FileUtils.class })
public class APIManagerConfigurationTest {
    private final Log log = LogFactory.getLog(APIManagerConfigurationTest.class);
    private File file;

    @Before
    public void init() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource("api-manager.xml").getFile());
        PowerMockito.mockStatic(APIUtil.class);
        Mockito.when(APIUtil.replaceSystemProperty(Mockito.anyString())).then(returnsFirstArg());
        Mockito.when(APIUtil.getClassForName(Mockito.anyString())).thenReturn(WSO2APIPublisher.class);
    }

    @Test
    public void testLoad(){
        APIManagerConfiguration apiManagerConfiguration = new APIManagerConfiguration();
        try {
            apiManagerConfiguration.load(file.getPath());
        } catch (APIManagementException e) {
            log.error("Error while reading configs from file api-manager.xml", e);
        }
    }

    @Test
    public void testExternalAPIStoreConfigs() throws Exception {

        APIManagerConfiguration apiManagerConfiguration = new APIManagerConfiguration();
        apiManagerConfiguration.load(file.getPath());
        APIStore apiStore1 = apiManagerConfiguration.getExternalAPIStore("Store2");
        Assert.assertEquals("wso2", apiStore1.getType());
        Assert.assertEquals("admin", apiStore1.getUsername());
        APIStore apiStore = Mockito.mock(APIStore.class);
        PowerMockito.whenNew(APIStore.class).withAnyArguments().thenReturn(apiStore);
        Mockito.doThrow(InstantiationException.class).when(apiStore).setPublisher((APIPublisher) Mockito.any());
        try {
            apiManagerConfiguration = new APIManagerConfiguration();
            apiManagerConfiguration.load(file.getPath());
            Assert.fail("Instantiation Exception not thrown for error scenario");
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().contains("Unexpected error occurred while parsing configuration"));
        }
        Mockito.doThrow(IllegalAccessException.class).when(apiStore).setPublisher((APIPublisher) Mockito.any());
        try {
            apiManagerConfiguration = new APIManagerConfiguration();
            apiManagerConfiguration.load(file.getPath());
            Assert.fail("Illegal Access Exception not thrown for error scenario");
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().contains("Unexpected error occurred while parsing configuration"));
        }
        Mockito.doThrow(ClassNotFoundException.class).when(apiStore).setPublisher((APIPublisher) Mockito.any());
        try {
            apiManagerConfiguration = new APIManagerConfiguration();
            apiManagerConfiguration.load(file.getPath());
            Assert.fail("Class Not Found Exception not thrown for error scenario");
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().contains("Unexpected error occurred while parsing configuration"));
        }
    }

    @Test
    public void testXMLParseExceptionHandling() throws Exception {
        PowerMockito.mockStatic(FileUtils.class);
        PowerMockito.when(FileUtils.openInputStream((File) Mockito.any())).thenThrow(IOException.class);
        APIManagerConfiguration apiManagerConfiguration = new APIManagerConfiguration();
        try {
            apiManagerConfiguration.load(file.getPath());
            Assert.fail("Exception not thrown for error scenario");
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().contains("I/O error while reading the API manager"));
        }
    }

    @Test
    public void testThrottleConfigurations() throws APIManagementException {
        APIManagerConfiguration apiManagerConfiguration = new APIManagerConfiguration();
        apiManagerConfiguration.load(file.getPath());
        ThrottleProperties throttleProperties = apiManagerConfiguration.getThrottleProperties();
        Assert.assertTrue(throttleProperties.isEnabled());
        Assert.assertEquals(200, throttleProperties.getDataPublisherPool().getInitIdleCapacity());
        Assert.assertEquals(1000, throttleProperties.getDataPublisherPool().getMaxIdle());
        Assert.assertEquals(200, throttleProperties.getDataPublisherThreadPool().getCorePoolSize());
        Assert.assertEquals(1000, throttleProperties.getDataPublisherThreadPool().getMaximumPoolSize());
        Assert.assertEquals(200, throttleProperties.getDataPublisherThreadPool().getKeepAliveTime());
        Assert.assertTrue(throttleProperties.getJmsConnectionProperties().isEnabled());
    }

    @Test
    public void testWorkflowConfigurations() throws APIManagementException {
        APIManagerConfiguration apiManagerConfiguration = new APIManagerConfiguration();
        apiManagerConfiguration.load(file.getPath());
        WorkflowProperties workflowProperties = apiManagerConfiguration.getWorkflowProperties();
        Assert.assertFalse(workflowProperties.isEnabled());
        Assert.assertEquals("https://localhost:9445/bpmn", workflowProperties.getServerUrl());
        Assert.assertEquals("${admin.username}", workflowProperties.getServerUser());
        Assert.assertEquals("${admin.password}", workflowProperties.getServerPassword());
    }

    @Test
    public void testGatewayConfigurations() throws APIManagementException {
        APIManagerConfiguration apiManagerConfiguration = new APIManagerConfiguration();
        apiManagerConfiguration.load(file.getPath());
        Map<String, Environment> environments = apiManagerConfiguration.getApiGatewayEnvironments();
        Assert.assertEquals(1, environments.size());
        Assert.assertEquals("hybrid", environments.get("Production and Sandbox").getType());
    }
}
