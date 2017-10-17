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
 */

package org.wso2.carbon.apimgt.gateway.handlers;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.gateway.handlers.security.APISecurityException;
import org.wso2.carbon.apimgt.gateway.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.dto.APIKeyValidationInfoDTO;
import org.wso2.carbon.apimgt.keymgt.stub.validator.APIKeyValidationServiceAPIKeyMgtException;
import org.wso2.carbon.apimgt.keymgt.stub.validator.APIKeyValidationServiceAPIManagementException;
import org.wso2.carbon.apimgt.keymgt.stub.validator.APIKeyValidationServiceStub;

import java.rmi.RemoteException;

import static junit.framework.Assert.fail;

/**
 * Test class for WebsocketThriftClient
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WebsocketThriftClientTestCase.class, ServiceReferenceHolder.class})
public class WebsocketThriftClientTestCase {

    /*
    * Test for APISecurityException when Error while accessing backend services
    * */
    @Test
    public void testetAPIKeyData() {
        try {
            PowerMockito.mockStatic(ServiceReferenceHolder.class);
            ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
            APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
//            APIKeyValidationInfoDTO apiKeyValidationInfoDTO = Mockito.mock(APIKeyValidationInfoDTO.class);
//            APIKeyValidationInfoDTO apiKeyValidationInfoDTO1 = Mockito.mock(APIKeyValidationInfoDTO.class);
//            APIKeyValidationServiceStub apiKeyValidationServiceStub = Mockito.mock(APIKeyValidationServiceStub.class);
            PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
            Mockito.when(serviceReferenceHolder.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
            Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_URL))
                    .thenReturn("http://localhost:8083");
            Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_USERNAME))
                    .thenReturn("ishara");
            Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_PASSWORD))
                    .thenReturn("abc123");
//            try {
//                Mockito.when(apiKeyValidationServiceStub.validateKeyforHandshake("/ishara", "1.0",
//                        "PhoneVerify")).
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            } catch (APIKeyValidationServiceAPIManagementException e) {
//                e.printStackTrace();
//            } catch (APIKeyValidationServiceAPIKeyMgtException e) {
//                e.printStackTrace();
//            }

            WebsocketWSClient websocketWSClient = new WebsocketWSClient();
            try {
                ConfigurationContext ctx = ConfigurationContextFactory
                        .createConfigurationContextFromFileSystem(null, null);
//                APIKeyValidationServiceStub keyValidationServiceStub =
//                        new APIKeyValidationServiceStub(ctx,"http://localhost:8083APIKeyValidationService");

//                websocketWSClient.setKeyValidationServiceStub(apiKeyValidationServiceStub);
                APIKeyValidationInfoDTO apiKeyValidationInfoDTOActual = websocketWSClient.getAPIKeyData("/ishara", "1.0", "PhoneVerify");
            } catch (AxisFault axisFault) {
                fail("AxisFault is thrown when creating ConfigurationContext " + axisFault.getMessage());
            }
        } catch (APISecurityException e) {
            e.printStackTrace();
            Assert.assertTrue(e.getMessage().startsWith("Error while accessing backend services for API key validation"));
        }
    }
}
