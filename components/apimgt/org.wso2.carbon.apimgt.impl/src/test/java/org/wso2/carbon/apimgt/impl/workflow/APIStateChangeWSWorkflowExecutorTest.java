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
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.dto.WorkflowDTO;
import org.wso2.carbon.apimgt.impl.dto.WorkflowProperties;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.wso2.carbon.base.CarbonBaseConstants.CARBON_HOME;

/**
 * APIStateChangeSimpleWorkflowExecutor test cases
 */

@RunWith(PowerMockRunner.class) @PrepareForTest({ ApiMgtDAO.class, ServiceReferenceHolder.class,
        WorkflowExecutorFactory.class, APIUtil.class, CarbonContext.class, PrivilegedCarbonContext.class }) public class APIStateChangeWSWorkflowExecutorTest {

    private APIStateChangeWSWorkflowExecutor apiStateChangeWSWorkflowExecutor;
    private ApiMgtDAO apiMgtDAO;
    private ServiceReferenceHolder serviceReferenceHolder;
    private Registry registry;
    private UserRegistry userRegistry;
    private RegistryService registryService;


    @Before public void init() {
        apiStateChangeWSWorkflowExecutor = new APIStateChangeWSWorkflowExecutor();
        registryService = Mockito.mock(RegistryService.class);
        apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        registryService = Mockito.mock(RegistryService.class);
        registry = Mockito.mock(Registry.class);
        userRegistry = Mockito.mock(UserRegistry.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
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

    @Test public void testComplete() throws Exception {
        APIStateWorkflowDTO workflowDTO = new APIStateWorkflowDTO();
        workflowDTO.setCreatedTime(1234567);
        Map<String, String> workflowDTOAttrs = new HashMap<String, String>();
        workflowDTOAttrs.put(WorkflowConstants.PayloadConstants.VARIABLE_API_LC_ACTION, "CREATE");
        workflowDTOAttrs.put(WorkflowConstants.PayloadConstants.VARIABLE_APINAME, "mockedAPI");
        workflowDTOAttrs.put(WorkflowConstants.PayloadConstants.VARIABLE_APIPROVIDER, "admin");
        workflowDTOAttrs.put(WorkflowConstants.PayloadConstants.VARIABLE_APIVERSION, "V2");
        workflowDTOAttrs.put(WorkflowConstants.PayloadConstants.VARIABLE_INVOKER, "mockedUser");
        workflowDTOAttrs.put(WorkflowConstants.PayloadConstants.VARIABLE_APISTATE, "PUBLISHED");
        workflowDTO.setAttributes(workflowDTOAttrs);
        workflowDTO.setInvoker("mockedInvoker");
        workflowDTO.setStatus(WorkflowStatus.APPROVED);
        workflowDTO.setApiCurrentState(WorkflowStatus.CREATED.toString());
        workflowDTO.setTenantId(-1234);
        workflowDTO.setApiLCAction(WorkflowStatus.CREATED.toString());

        System.setProperty(CARBON_HOME, "");
        PrivilegedCarbonContext privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);
        CarbonContext carbonContext = Mockito.mock(CarbonContext.class);
        PowerMockito.mockStatic(CarbonContext.class);
        PowerMockito.when(CarbonContext.getThreadLocalCarbonContext()).thenReturn(carbonContext);
        PowerMockito.mockStatic(ApiMgtDAO.class);
        apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        PowerMockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
        PowerMockito.doNothing().when(apiMgtDAO)
                .recordAPILifeCycleEvent(Mockito.any(APIIdentifier.class), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.anyInt());

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

        PowerMockito.mockStatic(APIUtil.class);
        GenericArtifact mockedApiArtifact = Mockito.mock(GenericArtifact.class);
        PowerMockito.when(mockedApiArtifact.getLifecycleState()).thenReturn("CREATED");
        PowerMockito.when(APIUtil.getAPIArtifact(Mockito.any(APIIdentifier.class), Mockito.any(Registry.class)))
                .thenReturn(mockedApiArtifact);

        Mockito.when(mockedServiceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceUserRegistry(Mockito.anyString(), Mockito.anyInt())).
                thenReturn(userRegistry);

        apiStateChangeWSWorkflowExecutor.complete(workflowDTO);
        Mockito.verify(apiMgtDAO, Mockito.atLeastOnce())
                .recordAPILifeCycleEvent(Mockito.any(APIIdentifier.class), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.anyInt());
    }

    @Test public void testPublicGetters() throws Exception {
        APIStateChangeWSWorkflowExecutor sampleStateChangeWSWorkflowExecutor = new APIStateChangeWSWorkflowExecutor();

        String stateList = "Blocked:Blocked,Created:Blocked,Prototyped:Published,Deprecated:Prototyped,Published:Published";
        sampleStateChangeWSWorkflowExecutor.setStateList(stateList);
        Assert.assertEquals(stateList, sampleStateChangeWSWorkflowExecutor.getStateList());

        String sampleClientId = UUID.randomUUID().toString();
        sampleStateChangeWSWorkflowExecutor.setClientId(sampleClientId);
        Assert.assertEquals(sampleStateChangeWSWorkflowExecutor.getClientId(), sampleClientId);

        String sampleClientSecret = UUID.randomUUID().toString();
        sampleStateChangeWSWorkflowExecutor.setClientSecret(sampleClientSecret);
        Assert.assertEquals(sampleClientSecret, sampleStateChangeWSWorkflowExecutor.getClientSecret());

        String samplePassword = "Samp1eP@ssw0rd";
        sampleStateChangeWSWorkflowExecutor.setPassword(samplePassword);
        Assert.assertEquals(samplePassword, sampleStateChangeWSWorkflowExecutor.getPassword());

        String sampleProcessDefinitionKey = "dummyProcessDefinitionKey";
        sampleStateChangeWSWorkflowExecutor.setProcessDefinitionKey(sampleProcessDefinitionKey);
        Assert.assertEquals(sampleProcessDefinitionKey, sampleStateChangeWSWorkflowExecutor.getProcessDefinitionKey());

        String sampleServiceEndpoint = "https://test.wso2.com/testEndpoint";
        sampleStateChangeWSWorkflowExecutor.setServiceEndpoint(sampleServiceEndpoint);
        Assert.assertEquals(sampleServiceEndpoint, sampleStateChangeWSWorkflowExecutor.getServiceEndpoint());

        String sampleTokenAPI = "https://test.wso2.com/testEndpoint";
        sampleStateChangeWSWorkflowExecutor.setTokenAPI(sampleTokenAPI);
        Assert.assertEquals(sampleTokenAPI, sampleStateChangeWSWorkflowExecutor.getTokenAPI());

        String sampleUsername = "dummyUsername";
        sampleStateChangeWSWorkflowExecutor.setUsername(sampleUsername);
        Assert.assertEquals(sampleUsername, sampleStateChangeWSWorkflowExecutor.getUsername());

        String sampleCallbackURL = "https://test.sample.wso2.com/callbackURL";
        sampleStateChangeWSWorkflowExecutor.setCallbackURL(sampleCallbackURL);
        Assert.assertEquals(sampleCallbackURL, sampleStateChangeWSWorkflowExecutor.getCallbackURL());
    }

}
