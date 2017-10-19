/*
 *  Copyright WSO2 Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.apimgt.keymgt.handlers;

import org.apache.axiom.om.OMElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.identity.core.util.IdentityConfigParser;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OAuthServerConfiguration.class, IdentityConfigParser.class, IdentityTenantUtil.class})
public class ExtendedPasswordGrantHandlerTest {
    @Test
    public void testInit() throws Exception {
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        PowerMockito.mockStatic(IdentityConfigParser.class);

        OAuthServerConfiguration authServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        IdentityConfigParser identityConfigParser = Mockito.mock(IdentityConfigParser.class);
        OMElement omElement = Mockito.mock(OMElement.class);

        Mockito.when(authServerConfiguration.getInstance()).thenReturn(authServerConfiguration);
        Mockito.when(identityConfigParser.getInstance()).thenReturn(identityConfigParser);
        Mockito.when(identityConfigParser.getConfigElement("OAuth")).thenReturn(omElement);

        ExtendedPasswordGrantHandler extendedPasswordGrantHandler = new ExtendedPasswordGrantHandler();
        extendedPasswordGrantHandler.init();
    }
}