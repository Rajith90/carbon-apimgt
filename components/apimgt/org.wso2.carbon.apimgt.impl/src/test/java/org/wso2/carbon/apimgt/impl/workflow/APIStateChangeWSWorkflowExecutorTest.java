/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.impl.workflow;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.WorkflowResponse;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.dto.WorkflowDTO;
import org.wso2.carbon.apimgt.impl.dto.WorkflowProperties;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * APIStateChangeSimpleWorkflowExecutor test cases
 */

@RunWith(PowerMockRunner.class) @PrepareForTest({ ApiMgtDAO.class, ServiceReferenceHolder.class,
        WorkflowExecutorFactory.class, APIUtil.class }) public class APIStateChangeWSWorkflowExecutorTest {

    private APIStateChangeWSWorkflowExecutor apiStateChangeWSWorkflowExecutor;
    private ApiMgtDAO apiMgtDAO;

    @Before public void init() {
        apiStateChangeWSWorkflowExecutor = new APIStateChangeWSWorkflowExecutor();
    }

    @Test public void testRetrievingWorkFlowType() {
        Assert.assertEquals(apiStateChangeWSWorkflowExecutor.getWorkflowType(), "AM_API_STATE");
    }

    @Test public void testExecutingAPIStateChangeWorkFlowAsSimpleWF() {
        APIStateWorkflowDTO workflowDTO = new APIStateWorkflowDTO();
        workflowDTO.setCreatedTime(1234567);
        workflowDTO.setCallbackUrl("http://sample.com/url");
        workflowDTO.setExternalWorkflowReference(UUID.randomUUID().toString());
        workflowDTO.setStatus(WorkflowStatus.CREATED);
        workflowDTO.setApiCurrentState(WorkflowStatus.CREATED.toString());
        workflowDTO.setTenantId(-1234);
        workflowDTO.setTenantDomain("mock.com");
        workflowDTO.setWorkflowType("MockType");

        WorkflowDTO mockedWorkflowDTO = Mockito.mock(WorkflowDTO.class);
        WorkflowExecutor mockedWorkflowExecutor = PowerMockito.mock(WorkflowExecutor.class);

        PowerMockito.mockStatic(ApiMgtDAO.class);
        apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        PowerMockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);

        try {
            PowerMockito.doNothing().when(apiMgtDAO).updateWorkflowStatus(mockedWorkflowDTO);
            Mockito.when(mockedWorkflowExecutor.complete(mockedWorkflowDTO))
                    .thenReturn(Mockito.mock(WorkflowResponse.class));
            apiStateChangeWSWorkflowExecutor.setStateList(
                    "Blocked:Blocked,Created:Blocked,Prototyped:Published,Deprecated:Prototyped,Published:Published");
            WorkflowResponse sampleWorkflowResponse = apiStateChangeWSWorkflowExecutor.execute(workflowDTO);
            Assert.assertNotNull(sampleWorkflowResponse);

        } catch (WorkflowException e) {
            Assert.fail("Unexpected WorkflowException occurred while executing API state change WS workflow");
        } catch (APIManagementException e) {
            Assert.fail("Unexpected APIManagementException occurred while executing API state change WS workflow");
        }
    }

    @Test public void testExecutingAPIStateChangeWorkFlow() throws Exception {
        APIStateWorkflowDTO workflowDTO = new APIStateWorkflowDTO();
        workflowDTO.setCreatedTime(1234567);
        workflowDTO.setCallbackUrl("http://sample.com/url");
        workflowDTO.setExternalWorkflowReference(UUID.randomUUID().toString());
        workflowDTO.setStatus(WorkflowStatus.CREATED);
        workflowDTO.setApiCurrentState(WorkflowStatus.CREATED.toString());
        workflowDTO.setTenantId(-1234);
        workflowDTO.setTenantDomain("mock.com");
        workflowDTO.setWorkflowType("MockType");
        workflowDTO.setApiLCAction(WorkflowStatus.CREATED.toString());

        WorkflowDTO mockedWorkflowDTO = Mockito.mock(WorkflowDTO.class);
        WorkflowExecutor mockedWorkflowExecutor = PowerMockito.mock(WorkflowExecutor.class);

        PowerMockito.mockStatic(ApiMgtDAO.class);
        apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        PowerMockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder mockedServiceReferenceHolder = PowerMockito.mock(ServiceReferenceHolder.class);
        APIManagerConfigurationService amConfigService = Mockito.mock(APIManagerConfigurationService.class);
        APIManagerConfiguration amConfig = Mockito.mock(APIManagerConfiguration.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(mockedServiceReferenceHolder);

        PowerMockito.when(mockedServiceReferenceHolder.getAPIManagerConfigurationService()).thenReturn(amConfigService);
        PowerMockito.when(amConfigService.getAPIManagerConfiguration()).thenReturn(amConfig);
        WorkflowExecutorFactory wfe = PowerMockito.mock(WorkflowExecutorFactory.class);
        PowerMockito.mockStatic(WorkflowExecutorFactory.class);
        Mockito.when(WorkflowExecutorFactory.getInstance()).thenReturn(wfe);

        WorkflowProperties workflowProperties = Mockito.mock(WorkflowProperties.class);
        Mockito.when(amConfig.getWorkflowProperties()).thenReturn(workflowProperties);
        Mockito.when(workflowProperties.getdCREndPoint()).thenReturn("http://mock.sample.wso2.com/dcr");
        Mockito.when(workflowProperties.getServerUrl()).thenReturn("http://mock.sample.wso2.com/server");

        URL url = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(url);
        HttpURLConnection urlConnection = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(url.openConnection()).thenReturn(urlConnection);
        PowerMockito.when(urlConnection.getResponseCode()).thenReturn(200);
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        StatusLine mockedStatusLine = Mockito.mock(StatusLine.class);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(mockedStatusLine);
        Mockito.when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK, HttpStatus.SC_CREATED).thenReturn
                (HttpStatus.SC_BAD_GATEWAY);
        Mockito.when(httpClient.execute(Mockito.any(HttpPost.class))).thenReturn(httpResponse).thenReturn
                (httpResponse).thenReturn(httpResponse).thenThrow(IOException.class).thenThrow
                (ClientProtocolException.class);
        String jsonResponse =
                "{\"clientId\":\"" + UUID.randomUUID().toString() + "\",\"token_type\":\"Bearer\",\"clientSecret\":\""
                        + UUID.randomUUID().toString() + "\"}";
        Mockito.when(httpResponse.getEntity()).thenReturn(new StringEntity(jsonResponse));

        PowerMockito.mockStatic(APIUtil.class);
        PowerMockito.when(APIUtil.getHttpClient(Mockito.anyInt(), Mockito.anyString())).thenReturn(httpClient);
        PowerMockito.doNothing().when(apiMgtDAO).updateWorkflowStatus(mockedWorkflowDTO);
        Mockito.when(mockedWorkflowExecutor.complete(mockedWorkflowDTO))
                .thenReturn(Mockito.mock(WorkflowResponse.class));
        apiStateChangeWSWorkflowExecutor
                .setStateList("CREATED:CREATED,PROTOTYPED:PUBLISHED,DEPRECATED:PROTOTYPED,BLOCKED:BLOCKED");
        WorkflowResponse sampleWorkflowResponse = apiStateChangeWSWorkflowExecutor.execute(workflowDTO);
        Assert.assertNotNull(sampleWorkflowResponse);
        try {
            apiStateChangeWSWorkflowExecutor.execute(workflowDTO);
            Assert.fail();
        } catch (WorkflowException e) {
            Assert.assertTrue(e.getMessage().contains("Error while starting the process:  "));
        }
        try {
            apiStateChangeWSWorkflowExecutor.execute(workflowDTO);
            Assert.fail();
        } catch (WorkflowException e) {
            Assert.assertTrue(e.getMessage().contains("Error while connecting to the BPMN process server from the " +
                    "WorkflowExecutor."));
        }
        try {
            apiStateChangeWSWorkflowExecutor.execute(workflowDTO);
            Assert.fail();
        } catch (WorkflowException e) {
            Assert.assertTrue(e.getMessage().contains("Error while creating the http client"));
        }
    }

    @Test public void testCleanUpPendingTask() throws Exception {
        WorkflowDTO mockedWorkflowDTO = Mockito.mock(WorkflowDTO.class);
        WorkflowExecutor mockedWorkflowExecutor = PowerMockito.mock(WorkflowExecutor.class);

        PowerMockito.mockStatic(ApiMgtDAO.class);
        apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        PowerMockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder mockedServiceReferenceHolder = PowerMockito.mock(ServiceReferenceHolder.class);
        APIManagerConfigurationService amConfigService = Mockito.mock(APIManagerConfigurationService.class);
        APIManagerConfiguration amConfig = Mockito.mock(APIManagerConfiguration.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(mockedServiceReferenceHolder);

        PowerMockito.when(mockedServiceReferenceHolder.getAPIManagerConfigurationService()).thenReturn(amConfigService);
        PowerMockito.when(amConfigService.getAPIManagerConfiguration()).thenReturn(amConfig);
        WorkflowExecutorFactory wfe = PowerMockito.mock(WorkflowExecutorFactory.class);
        PowerMockito.mockStatic(WorkflowExecutorFactory.class);
        Mockito.when(WorkflowExecutorFactory.getInstance()).thenReturn(wfe);

        WorkflowProperties workflowProperties = Mockito.mock(WorkflowProperties.class);
        Mockito.when(amConfig.getWorkflowProperties()).thenReturn(workflowProperties);

        Mockito.when(workflowProperties.getdCREndPoint()).thenReturn("http://mock.sample.wso2.com/dcr");
        Mockito.when(workflowProperties.getServerUrl()).thenReturn("http://mock.sample.wso2.com/server");

        URL url = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(url);
        HttpURLConnection urlConnection = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(url.openConnection()).thenReturn(urlConnection);
        PowerMockito.when(urlConnection.getResponseCode()).thenReturn(200);
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        StatusLine mockedStatusLine = Mockito.mock(StatusLine.class);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(mockedStatusLine);
        Mockito.when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK, HttpStatus.SC_NO_CONTENT)
                .thenReturn(HttpStatus.SC_BAD_GATEWAY);
        Mockito.when(httpClient.execute(Mockito.any(HttpPost.class))).thenReturn(httpResponse).thenReturn(httpResponse)
                .thenReturn(httpResponse).thenThrow(IOException.class).thenThrow(ClientProtocolException.class);
        String jsonResponse =
                "{\"clientId\":\"" + UUID.randomUUID().toString() + "\",\"token_type\":\"Bearer\",\"clientSecret\":\""
                        + UUID.randomUUID().toString() + "\",\"data\":[{\"id\":\"sda67sadsa8f6a87fa6f87a99saa\"}]}";
        Mockito.when(httpResponse.getEntity()).thenReturn(new StringEntity(jsonResponse));

        PowerMockito.mockStatic(APIUtil.class);
        PowerMockito.when(APIUtil.getHttpClient(Mockito.anyInt(), Mockito.anyString())).thenReturn(httpClient);
        PowerMockito.doNothing().when(apiMgtDAO).updateWorkflowStatus(mockedWorkflowDTO);
        Mockito.when(mockedWorkflowExecutor.complete(mockedWorkflowDTO))
                .thenReturn(Mockito.mock(WorkflowResponse.class));

        String mockedWorkflowExtRef = UUID.randomUUID().toString();
        apiStateChangeWSWorkflowExecutor.cleanUpPendingTask(mockedWorkflowExtRef);
        Mockito.verify(apiMgtDAO, Mockito.atLeastOnce()).removeWorkflowEntry(Mockito.anyString(), Mockito.anyString());
    }

}
