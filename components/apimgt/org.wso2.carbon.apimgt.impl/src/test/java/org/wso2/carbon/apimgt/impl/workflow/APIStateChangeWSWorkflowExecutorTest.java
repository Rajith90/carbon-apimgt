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
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.dto.WorkflowDTO;

import java.util.UUID;

/**
 * APIStateChangeSimpleWorkflowExecutor test cases
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApiMgtDAO.class })
public class APIStateChangeWSWorkflowExecutorTest {

    private APIStateChangeWSWorkflowExecutor apiStateChangeWSWorkflowExecutor;
    private ApiMgtDAO apiMgtDAO;

    @Before public void init() {
        apiStateChangeWSWorkflowExecutor = new APIStateChangeWSWorkflowExecutor();
    }

    @Test public void testRetrievingWorkFlowType() {
        Assert.assertEquals(apiStateChangeWSWorkflowExecutor.getWorkflowType(), "AM_API_STATE");
    }

    @Test public void testExecutingAPIStateChangeWorkFlow() {
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

}
