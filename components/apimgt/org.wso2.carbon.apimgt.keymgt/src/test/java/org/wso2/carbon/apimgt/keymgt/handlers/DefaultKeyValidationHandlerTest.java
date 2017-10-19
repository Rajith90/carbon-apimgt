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
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.AccessTokenInfo;
import org.wso2.carbon.apimgt.api.model.KeyManager;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.dto.APIKeyValidationInfoDTO;
import org.wso2.carbon.apimgt.impl.factory.KeyManagerHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.keymgt.APIKeyMgtException;
import org.wso2.carbon.apimgt.keymgt.service.TokenValidationContext;
import org.wso2.carbon.apimgt.keymgt.token.TokenGenerator;
import org.wso2.carbon.apimgt.keymgt.util.APIKeyMgtDataHolder;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth2.model.AccessTokenDO;
import org.wso2.carbon.identity.oauth2.validators.OAuth2ScopeValidator;

import java.util.HashSet;
import java.util.Set;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ApiMgtDAO.class, KeyManagerHolder.class ,APIUtil.class, IdentityUtil.class, OAuthServerConfiguration.class,
        APIKeyMgtDataHolder.class})
public class DefaultKeyValidationHandlerTest {
    @Test
    public void testValidateToken () throws Exception {
        TokenValidationContext tokenValidationContext = Mockito.mock(TokenValidationContext.class);
        PowerMockito.mockStatic(KeyManagerHolder.class);
        KeyManagerHolder kmHolder = Mockito.mock(KeyManagerHolder.class);
        KeyManager km = Mockito.mock(KeyManager.class);
        Mockito.when(kmHolder.getKeyManagerInstance()).thenReturn(km);
        PowerMockito.mockStatic(ApiMgtDAO.class);
        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        Mockito.when(apiMgtDAO.getInstance()).thenReturn(apiMgtDAO);

        DefaultKeyValidationHandler defaultKeyValidationHandler = new DefaultKeyValidationHandler();

        //isCacheHit true
        Mockito.when(tokenValidationContext.isCacheHit()).thenReturn(true);
        APIKeyValidationInfoDTO keyValidationInfoDTO = Mockito.mock(APIKeyValidationInfoDTO.class);
        Mockito.when(tokenValidationContext.getValidationInfoDTO()).thenReturn(keyValidationInfoDTO);
        PowerMockito.mockStatic(APIUtil.class);
        APIUtil apiUtil = Mockito.mock(APIUtil.class);
        Mockito.when(apiUtil.isAccessTokenExpired((APIKeyValidationInfoDTO) Mockito.anyObject())).thenReturn(false);
        Assert.assertTrue(defaultKeyValidationHandler.validateToken(tokenValidationContext));

        //token expired true
        Mockito.when(apiUtil.isAccessTokenExpired((APIKeyValidationInfoDTO) Mockito.anyObject())).thenReturn(true);
        Assert.assertFalse(defaultKeyValidationHandler.validateToken(tokenValidationContext));

        //isCacheHit false
        Mockito.when(tokenValidationContext.isCacheHit()).thenReturn(false);
        Assert.assertFalse(defaultKeyValidationHandler.validateToken(tokenValidationContext));

        //token info not null
        AccessTokenInfo tokenInfo = Mockito.mock(AccessTokenInfo.class);
        Mockito.when(km.getTokenMetaData(Mockito.anyString())).thenReturn(tokenInfo);
        Assert.assertFalse(defaultKeyValidationHandler.validateToken(tokenValidationContext));

        //error code > 0
        Mockito.when(tokenInfo.getErrorcode()).thenReturn(1);
        Assert.assertFalse(defaultKeyValidationHandler.validateToken(tokenValidationContext));

        //isTokenValid true
        Mockito.when(tokenInfo.isTokenValid()).thenReturn(true);
        Assert.assertTrue(defaultKeyValidationHandler.validateToken(tokenValidationContext));

        //scopes not null
        String[] scopes = {"api_view", "api_update"};
        Mockito.when(tokenInfo.getScopes()).thenReturn(scopes);
        Assert.assertTrue(defaultKeyValidationHandler.validateToken(tokenValidationContext));
    }

    @Test
    public void testValidateScopes() throws Exception {
        TokenValidationContext tokenValidationContext = Mockito.mock(TokenValidationContext.class);
        PowerMockito.mockStatic(KeyManagerHolder.class);
        KeyManagerHolder kmHolder = Mockito.mock(KeyManagerHolder.class);
        KeyManager km = Mockito.mock(KeyManager.class);
        Mockito.when(kmHolder.getKeyManagerInstance()).thenReturn(km);
        PowerMockito.mockStatic(ApiMgtDAO.class);
        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        Mockito.when(apiMgtDAO.getInstance()).thenReturn(apiMgtDAO);

        DefaultKeyValidationHandler defaultKeyValidationHandler = new DefaultKeyValidationHandler();

        //isCacheHit - true
        Mockito.when(tokenValidationContext.isCacheHit()).thenReturn(true);
        Assert.assertTrue(defaultKeyValidationHandler.validateScopes(tokenValidationContext));

        //isCacheHit - false
        Mockito.when(tokenValidationContext.isCacheHit()).thenReturn(false);
        try {
            defaultKeyValidationHandler.validateScopes(tokenValidationContext);
        } catch (APIKeyMgtException e) {
            Assert.assertTrue("Key Validation information not set".equals(e.getMessage()));
        }

        //apiKeyValidationDTO not null & scopes is not empty
        APIKeyValidationInfoDTO keyValidationInfoDTO = Mockito.mock(APIKeyValidationInfoDTO.class);
        Mockito.when(tokenValidationContext.getValidationInfoDTO()).thenReturn(keyValidationInfoDTO);
        Set<String> scopes = new HashSet<String>();
        scopes.add("api_view");
        scopes.add("api_update");
        Mockito.when(keyValidationInfoDTO.getScopes()).thenReturn(scopes);
        PowerMockito.mockStatic(IdentityUtil.class);
        IdentityUtil identityUtil = Mockito.mock(IdentityUtil.class);
        Mockito.when(identityUtil.extractDomainFromName(Mockito.anyString())).thenReturn("abc"); //not FEDERATED
        Mockito.when(tokenValidationContext.getVersion()).thenReturn("1.0.0");
        Mockito.when(tokenValidationContext.getContext()).thenReturn("/testContext");
        Mockito.when(tokenValidationContext.getMatchingResource()).thenReturn("/resource");
        Mockito.when(tokenValidationContext.getHttpVerb()).thenReturn("POST");
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        OAuthServerConfiguration authServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        Mockito.when(authServerConfiguration.getInstance()).thenReturn(authServerConfiguration);
        OAuth2ScopeValidator scopeValidator = Mockito.mock(OAuth2ScopeValidator.class);
        Mockito.when(authServerConfiguration.getoAuth2ScopeValidator()).thenReturn(scopeValidator);
        Assert.assertFalse(defaultKeyValidationHandler.validateScopes(tokenValidationContext));

        //Domain from name equals FEDERATED
        Mockito.when(identityUtil.extractDomainFromName(Mockito.anyString())).thenReturn("FEDERATED");
        Assert.assertFalse(defaultKeyValidationHandler.validateScopes(tokenValidationContext));

        //api version is prefixed with _default_
        Mockito.when(tokenValidationContext.getVersion()).thenReturn("_default_1.0.0");
        Assert.assertFalse(defaultKeyValidationHandler.validateScopes(tokenValidationContext));

        //validate scope true
        Mockito.when(scopeValidator.validateScope((AccessTokenDO)Mockito.anyObject(), Mockito.anyString())).thenReturn(true);
        Assert.assertTrue(defaultKeyValidationHandler.validateScopes(tokenValidationContext));
    }

    @Test
    public void testValidateSubscription() throws Exception {
        TokenValidationContext tokenValidationContext = Mockito.mock(TokenValidationContext.class);
        PowerMockito.mockStatic(KeyManagerHolder.class);
        KeyManagerHolder kmHolder = Mockito.mock(KeyManagerHolder.class);
        KeyManager km = Mockito.mock(KeyManager.class);
        Mockito.when(kmHolder.getKeyManagerInstance()).thenReturn(km);
        PowerMockito.mockStatic(ApiMgtDAO.class);
        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        Mockito.when(apiMgtDAO.getInstance()).thenReturn(apiMgtDAO);

        DefaultKeyValidationHandler defaultKeyValidationHandler = new DefaultKeyValidationHandler();
        Assert.assertFalse(defaultKeyValidationHandler.validateSubscription(tokenValidationContext));

        //when ValidationInfoDTO is not null & isCacheHit false
        APIKeyValidationInfoDTO keyValidationInfoDTO = Mockito.mock(APIKeyValidationInfoDTO.class);
        Mockito.when(tokenValidationContext.getValidationInfoDTO()).thenReturn(keyValidationInfoDTO);
        Assert.assertFalse(defaultKeyValidationHandler.validateSubscription(tokenValidationContext));

        //validationInfoDTO not null & isCacheHit true
        Mockito.when(tokenValidationContext.isCacheHit()).thenReturn(true);
        Assert.assertTrue(defaultKeyValidationHandler.validateSubscription(tokenValidationContext));

        //tokeninfo not null & isCacheHit false
        Mockito.when(tokenValidationContext.isCacheHit()).thenReturn(false);
        AccessTokenInfo accessTokenInfo = Mockito.mock(AccessTokenInfo.class);
        Mockito.when(tokenValidationContext.getTokenInfo()).thenReturn(accessTokenInfo);
        Assert.assertFalse(defaultKeyValidationHandler.validateSubscription(tokenValidationContext));

        //isApplicationToken - true
        Mockito.when(accessTokenInfo.isApplicationToken()).thenReturn(true);
        Assert.assertFalse(defaultKeyValidationHandler.validateSubscription(tokenValidationContext));

        //test hasTokenRequiredAuthLevel - authScheme == "Application"
        Mockito.when(tokenValidationContext.getRequiredAuthenticationLevel()).thenReturn(APIConstants.AUTH_APPLICATION_LEVEL_TOKEN);
        Assert.assertFalse(defaultKeyValidationHandler.validateSubscription(tokenValidationContext));

        //test hasTokenRequiredAuthLevel - authScheme == "Application_User"
        Mockito.when(tokenValidationContext.getRequiredAuthenticationLevel()).thenReturn(APIConstants.AUTH_APPLICATION_USER_LEVEL_TOKEN);
        Assert.assertFalse(defaultKeyValidationHandler.validateSubscription(tokenValidationContext));

    }

    @Test
    public void testGenerateConsumerToken() throws Exception {
        TokenValidationContext tokenValidationContext = Mockito.mock(TokenValidationContext.class);
        PowerMockito.mockStatic(KeyManagerHolder.class);
        KeyManagerHolder kmHolder = Mockito.mock(KeyManagerHolder.class);
        KeyManager km = Mockito.mock(KeyManager.class);
        Mockito.when(kmHolder.getKeyManagerInstance()).thenReturn(km);
        PowerMockito.mockStatic(ApiMgtDAO.class);
        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        Mockito.when(apiMgtDAO.getInstance()).thenReturn(apiMgtDAO);

        DefaultKeyValidationHandler defaultKeyValidationHandler = new DefaultKeyValidationHandler();

        PowerMockito.mockStatic(APIKeyMgtDataHolder.class);
        APIKeyMgtDataHolder apiKeyMgtDataHolder = Mockito.mock(APIKeyMgtDataHolder.class);
        TokenGenerator tokenGenerator = Mockito.mock(TokenGenerator.class);
        Mockito.when(apiKeyMgtDataHolder.getTokenGenerator()).thenReturn(tokenGenerator);
        APIKeyValidationInfoDTO keyValidationInfoDTO = Mockito.mock(APIKeyValidationInfoDTO.class);
        Mockito.when(tokenValidationContext.getValidationInfoDTO()).thenReturn(keyValidationInfoDTO);
        Assert.assertTrue(defaultKeyValidationHandler.generateConsumerToken(tokenValidationContext));

        //exception
        Mockito.doThrow(APIManagementException.class).when(tokenGenerator).generateToken(tokenValidationContext);

        Assert.assertFalse(defaultKeyValidationHandler.generateConsumerToken(tokenValidationContext));

    }
}

