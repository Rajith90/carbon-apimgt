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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.model.AccessTokenInfo;
import org.wso2.carbon.apimgt.api.model.KeyManager;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.dto.APIKeyValidationInfoDTO;
import org.wso2.carbon.apimgt.impl.factory.KeyManagerHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.keymgt.APIKeyMgtException;
import org.wso2.carbon.apimgt.keymgt.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.keymgt.service.TokenValidationContext;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth2.validators.OAuth2ScopeValidator;

import java.util.HashSet;
import java.util.Set;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ServiceReferenceHolder.class, ApiMgtDAO.class, DefaultKeyValidationHandler.class, KeyManagerHolder.class
    ,APIUtil.class, IdentityUtil.class, OAuthServerConfiguration.class})
public class DefaultKeyValidationHandlerTest {
    @Test
    public void testValidateTokenWhenValidationDTOIsFromCache() throws Exception {
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.mock(ApiMgtDAO.class);
        PowerMockito.mockStatic(KeyManagerHolder.class);
        PowerMockito.mockStatic(APIUtil.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        APIManagerConfigurationService amcService = Mockito.mock(APIManagerConfigurationService.class);
        APIManagerConfiguration apimConfiguration = Mockito.mock(APIManagerConfiguration.class);
        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        TokenValidationContext tokenValidationContext = Mockito.mock(TokenValidationContext.class);
        KeyManagerHolder kmHolder = Mockito.mock(KeyManagerHolder.class);
        KeyManager km = Mockito.mock(KeyManager.class);
        AccessTokenInfo accessTokenInfo = Mockito.mock(AccessTokenInfo.class);
        APIUtil apiUtil = Mockito.mock(APIUtil.class);
        APIKeyValidationInfoDTO apiKeyValidationInfoDTO = Mockito.mock(APIKeyValidationInfoDTO.class);

        Mockito.when(serviceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService()).thenReturn(amcService);
        Mockito.when(amcService.getAPIManagerConfiguration()).thenReturn(apimConfiguration);
        PowerMockito.whenNew(ApiMgtDAO.class).withAnyArguments().thenReturn(apiMgtDAO);
        Mockito.when(kmHolder.getKeyManagerInstance()).thenReturn(km);
        Mockito.when(km.getTokenMetaData(Mockito.anyString())).thenReturn(accessTokenInfo);
        Mockito.when(tokenValidationContext.isCacheHit()).thenReturn(true);
        Mockito.when(apiUtil.isAccessTokenExpired(apiKeyValidationInfoDTO)).thenReturn(true);

        DefaultKeyValidationHandler defaultKeyValidationHandler1 = new DefaultKeyValidationHandler();
        defaultKeyValidationHandler1.validateToken(tokenValidationContext);

        Mockito.when(apiUtil.isAccessTokenExpired(apiKeyValidationInfoDTO)).thenReturn(false);

        defaultKeyValidationHandler1.validateToken(tokenValidationContext);
    }

    @Test
    public void testValidateTokenWhenValidationDTOIsNotInCacheAndTokenInvalid() throws Exception {
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.mock(ApiMgtDAO.class);
        PowerMockito.mockStatic(KeyManagerHolder.class);
        PowerMockito.mockStatic(APIUtil.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        APIManagerConfigurationService amcService = Mockito.mock(APIManagerConfigurationService.class);
        APIManagerConfiguration apimConfiguration = Mockito.mock(APIManagerConfiguration.class);
        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        TokenValidationContext tokenValidationContext = Mockito.mock(TokenValidationContext.class);
        KeyManagerHolder kmHolder = Mockito.mock(KeyManagerHolder.class);
        KeyManager km = Mockito.mock(KeyManager.class);
        AccessTokenInfo accessTokenInfo = Mockito.mock(AccessTokenInfo.class);
        APIUtil apiUtil = Mockito.mock(APIUtil.class);
        APIKeyValidationInfoDTO apiKeyValidationInfoDTO = Mockito.mock(APIKeyValidationInfoDTO.class);

        Mockito.when(serviceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService()).thenReturn(amcService);
        Mockito.when(amcService.getAPIManagerConfiguration()).thenReturn(apimConfiguration);
        PowerMockito.whenNew(ApiMgtDAO.class).withAnyArguments().thenReturn(apiMgtDAO);
        Mockito.when(kmHolder.getKeyManagerInstance()).thenReturn(km);
        Mockito.when(km.getTokenMetaData(Mockito.anyString())).thenReturn(accessTokenInfo);
        Mockito.when(tokenValidationContext.isCacheHit()).thenReturn(false);
        //Mockito.when(apiUtil.isAccessTokenExpired(apiKeyValidationInfoDTO)).thenReturn(true);

        DefaultKeyValidationHandler defaultKeyValidationHandler1 = new DefaultKeyValidationHandler();
        Assert.assertFalse(defaultKeyValidationHandler1.validateToken(tokenValidationContext));
    }

    @Test
    public void testValidateScopes () throws Exception {
        PowerMockito.mockStatic(IdentityUtil.class);
        PowerMockito.mockStatic(OAuthServerConfiguration.class);

        TokenValidationContext tokenValidationContext = Mockito.mock(TokenValidationContext.class);
        APIKeyValidationInfoDTO apiKeyValidationInfoDTO = Mockito.mock(APIKeyValidationInfoDTO.class);
        IdentityUtil identityUtil = Mockito.mock(IdentityUtil.class);
        OAuthServerConfiguration oAuthServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        OAuth2ScopeValidator oAuth2ScopeValidator = Mockito.mock(OAuth2ScopeValidator.class);

        Mockito.when(tokenValidationContext.getValidationInfoDTO()).thenReturn(apiKeyValidationInfoDTO);
        Mockito.when(identityUtil.extractDomainFromName(Mockito.anyString())).thenReturn("test.com");
        Mockito.when(oAuthServerConfiguration.getInstance()).thenReturn(oAuthServerConfiguration);
        Mockito.when(oAuthServerConfiguration.getoAuth2ScopeValidator()).thenReturn(oAuth2ScopeValidator);

        DefaultKeyValidationHandler defaultKeyValidationHandler = new DefaultKeyValidationHandler();
        Assert.assertFalse(defaultKeyValidationHandler.validateScopes(tokenValidationContext));

        //apiKeyValidationInfoDTO == null
        Mockito.when(tokenValidationContext.getValidationInfoDTO()).thenReturn(null);
        try {
            defaultKeyValidationHandler.validateScopes(tokenValidationContext);
        } catch (APIKeyMgtException e) {
            Assert.assertTrue("Key Validation information not set".equals(e.getMessage()));
        }

        //isCacheHit - true
        Mockito.when(tokenValidationContext.getValidationInfoDTO()).thenReturn(apiKeyValidationInfoDTO);
        Mockito.when(tokenValidationContext.isCacheHit()).thenReturn(true);
        Assert.assertTrue(defaultKeyValidationHandler.validateScopes(tokenValidationContext));

        //scopeset not null
        Set<String> scopes = new HashSet<String>();
        scopes.add("api_view");
        Mockito.when(tokenValidationContext.isCacheHit()).thenReturn(false);
        Mockito.when(apiKeyValidationInfoDTO.getScopes()).thenReturn(scopes);
        defaultKeyValidationHandler.validateScopes(tokenValidationContext);

    }


}