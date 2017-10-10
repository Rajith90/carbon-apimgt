/*
* Copyright (c) 2017, WSO2 Inc.(http://www.wso2.org) All Rights Reserved.
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* */
package org.wso2.carbon.apimgt.gateway.handlers.logging;

import org.apache.axis2.context.OperationContext;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.RESTConstants;
import org.junit.Test;
import org.wso2.carbon.apimgt.gateway.APIMgtGatewayConstants;
import org.wso2.carbon.apimgt.gateway.TestUtils;
import org.wso2.carbon.apimgt.impl.APIConstants;

import java.util.HashMap;
import java.util.Map;


public class APILogMessageHandlerTest {
    @Test
    public void handleRequest() throws Exception {
        MessageContext messageContext = TestUtils.getMessageContext("/api1/1.0.0", "abc-def");
        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext)
                .getAxis2MessageContext();
        OperationContext operationContext = new OperationContext();
        org.apache.axis2.context.MessageContext inMessageContext = new org.apache.axis2.context.MessageContext();
        Map transportHeaders = new HashMap();
        transportHeaders.put(APIConstants.ACTIVITY_ID, "123456");
        inMessageContext.setProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS, transportHeaders);
        operationContext.getMessageContexts().put(WSDL2Constants.MESSAGE_LABEL_IN, inMessageContext);
        axis2MessageContext.setOperationContext(operationContext);
        messageContext.setProperty(APIMgtGatewayConstants.APPLICATION_NAME, "Defaultapp");
        axis2MessageContext.setProperty(APIMgtGatewayConstants.REQUEST_RECEIVED_TIME, "1234");
        axis2MessageContext.setProperty(WSDL2Constants.MESSAGE_LABEL_IN, "true");
        axis2MessageContext.setProperty(APIMgtGatewayConstants.END_USER_NAME, "admin");
        APILogMessageHandler apiLogMessageHandler = new APILogMessageHandler();
        apiLogMessageHandler.handleRequest(messageContext);
        apiLogMessageHandler.handleResponse(messageContext);
    }
    @Test
    public void handleLoginRequest() throws Exception {
        MessageContext messageContext = TestUtils.getMessageContext("/api1/1.0.0", "abc-def");
        messageContext.setProperty(RESTConstants.REST_FULL_REQUEST_PATH,"/token/");
        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext)
                .getAxis2MessageContext();
        OperationContext operationContext = new OperationContext();
        org.apache.axis2.context.MessageContext inMessageContext = new org.apache.axis2.context.MessageContext();
        Map transportHeaders = new HashMap();
        transportHeaders.put(APIConstants.ACTIVITY_ID, "123456");
        inMessageContext.setProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS, transportHeaders);
        operationContext.getMessageContexts().put(WSDL2Constants.MESSAGE_LABEL_IN, inMessageContext);
        axis2MessageContext.setOperationContext(operationContext);
        messageContext.setProperty(APIMgtGatewayConstants.APPLICATION_NAME, "Defaultapp");
        axis2MessageContext.setProperty(APIMgtGatewayConstants.REQUEST_RECEIVED_TIME, "1234");
        axis2MessageContext.setProperty(WSDL2Constants.MESSAGE_LABEL_IN, "true");
        axis2MessageContext.setProperty(APIMgtGatewayConstants.END_USER_NAME, "admin");
        APILogMessageHandler apiLogMessageHandler = new APILogMessageHandler();
        apiLogMessageHandler.handleRequest(messageContext);
        apiLogMessageHandler.handleResponse(messageContext);
    }

}