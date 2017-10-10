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

package org.wso2.carbon.apimgt.gateway.handlers.security;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test class for APISecurityUtils
 */
public class APISecurityUtilsTestCase {

    @Test
    public void testAPISecurityUtils() {
        MessageContext messageContext = Mockito.mock(Axis2MessageContext.class);
        AuthenticationContext authenticationContext = Mockito.mock(AuthenticationContext.class);

        APISecurityUtilsWrapper apiSecurityUtilsWrapper = new APISecurityUtilsWrapper() {

        };
        apiSecurityUtilsWrapper.setAuthenticationContext(messageContext, authenticationContext, "abc");
//        apiSecurityUtilsWrapper.getKeyValidatorClientType();
    }

    private class APISecurityUtilsWrapper extends APISecurityUtils {

    }

}
