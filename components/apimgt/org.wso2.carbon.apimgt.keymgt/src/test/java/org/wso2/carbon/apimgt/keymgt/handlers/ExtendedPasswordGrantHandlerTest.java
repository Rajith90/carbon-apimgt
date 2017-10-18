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
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.keymgt.util.APIKeyMgtDataHolder;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.common.cache.BaseCache;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.mgt.ApplicationManagementService;
import org.wso2.carbon.identity.core.util.IdentityConfigParser;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth.cache.OAuthCache;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth.internal.OAuthComponentServiceHolder;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.internal.OAuth2ServiceComponentHolder;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.PasswordGrantHandler;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.user.api.Claim;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static org.wso2.carbon.base.CarbonBaseConstants.CARBON_HOME;

/*
@RunWith(PowerMockRunner.class)
@PrepareForTest({OAuthServerConfiguration.class, IdentityConfigParser.class, IdentityTenantUtil.class,
        MultitenantUtils.class, OAuth2ServiceComponentHolder.class, OAuthComponentServiceHolder.class, OAuth2Util.class,
        OAuthCache.class, ThreadLocal.class, ExtendedPasswordGrantHandlerTest.class, Pattern.class, APIKeyMgtDataHolder.class,
        QName.class, String.class, PrivilegedAction.class, AccessController.class, Map.class, UserCoreUtil.class,
        PrivilegedCarbonContext.class})
@SuppressStaticInitializationFor("org.wso2.carbon.identity.oauth2.util.OAuth2Util")
public class ExtendedPasswordGrantHandlerTest {
    private String requiredHeaderClaimUris =
            " <OAuth>" +
                    "<RequiredRespHeaderClaimUris>" +
                    "<ClaimUri>http://wso2.org/claims/emailaddress</ClaimUri>" +
                    "<ClaimUri>http://wso2.org/claims/gender</ClaimUri>" +
                    " </RequiredRespHeaderClaimUris>" +
                    "</OAuth>";
    @Test
    public void testInit() throws Exception {
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        PowerMockito.mockStatic(IdentityConfigParser.class);

        OAuthServerConfiguration authServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        IdentityConfigParser identityConfigParser = Mockito.mock(IdentityConfigParser.class);
        OMElement omElement = Mockito.mock(OMElement.class);
        Iterator iterator = Mockito.mock(Iterator.class);

        Mockito.when(authServerConfiguration.getInstance()).thenReturn(authServerConfiguration);
        Mockito.when(identityConfigParser.getInstance()).thenReturn(identityConfigParser);
        Mockito.when(identityConfigParser.getConfigElement("OAuth")).thenReturn(omElement);

        ExtendedPasswordGrantHandler extendedPasswordGrantHandler = new ExtendedPasswordGrantHandler();
        extendedPasswordGrantHandler.init();

        //when claimURIs not null
        Mockito.when(omElement.getFirstChildWithName((QName)Mockito.anyObject())).thenReturn(omElement);
        Mockito.when(omElement.getChildrenWithLocalName(Mockito.anyString())).thenReturn(iterator);

        extendedPasswordGrantHandler.init();
    }
    @Test
    public void testValidateGrantWhenValidateGrantFalse() throws Exception {
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        PowerMockito.mockStatic(MultitenantUtils.class);
        PowerMockito.mockStatic(OAuth2ServiceComponentHolder.class);
        OAuthServerConfiguration authServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        OAuthTokenReqMessageContext tokenReqMessageContext = Mockito.mock(OAuthTokenReqMessageContext.class);
        OAuth2AccessTokenReqDTO accessTokenReqDTO = Mockito.mock(OAuth2AccessTokenReqDTO.class);
        MultitenantUtils multitenantUtils = Mockito.mock(MultitenantUtils.class);
        OAuth2ServiceComponentHolder serviceComponentHolder = Mockito.mock(OAuth2ServiceComponentHolder.class);
        ApplicationManagementService applicationManagementService = Mockito.mock(ApplicationManagementService.class);
        ServiceProvider serviceProvider = Mockito.mock(ServiceProvider.class);
        Mockito.when(authServerConfiguration.getInstance()).thenReturn(authServerConfiguration);
        Mockito.when(tokenReqMessageContext.getOauth2AccessTokenReqDTO()).thenReturn(accessTokenReqDTO);
        Mockito.when(accessTokenReqDTO.getResourceOwnerUsername()).thenReturn("user@test.com");
        Mockito.when(multitenantUtils.getTenantAwareUsername(Mockito.anyString())).thenReturn("user@test.com");
        Mockito.doNothing().when(accessTokenReqDTO).setResourceOwnerUsername("user@test.com");
        Mockito.when(multitenantUtils.getTenantDomain(accessTokenReqDTO.getResourceOwnerUsername())).thenReturn("test.com");
        Mockito.when(accessTokenReqDTO.getClientId()).thenReturn("1");
        Mockito.when(accessTokenReqDTO.getTenantDomain()).thenReturn("carbon.super");
        Mockito.when(serviceComponentHolder.getApplicationMgtService()).thenReturn(applicationManagementService);
        Mockito.when(applicationManagementService.getServiceProviderByClientId(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString())).thenReturn(serviceProvider);
        ExtendedPasswordGrantHandler extendedPasswordGrantHandler = new ExtendedPasswordGrantHandler();
        //validated false so far
        Assert.assertFalse(extendedPasswordGrantHandler.validateGrant(tokenReqMessageContext));
    }

    @Test
    public void testValidateGrantWhenValidateGrantTrue() throws Exception {
        System.setProperty(CARBON_HOME, "");
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        PowerMockito.mockStatic(MultitenantUtils.class);
        PowerMockito.mockStatic(OAuth2ServiceComponentHolder.class);
        PowerMockito.mockStatic(IdentityTenantUtil.class);
        PowerMockito.mockStatic(OAuthComponentServiceHolder.class);
        PowerMockito.mockStatic(OAuth2Util.class);
        PowerMockito.mockStatic(APIKeyMgtDataHolder.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);

        OAuthServerConfiguration authServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        OAuthTokenReqMessageContext tokenReqMessageContext = Mockito.mock(OAuthTokenReqMessageContext.class);
        OAuth2AccessTokenReqDTO accessTokenReqDTO = Mockito.mock(OAuth2AccessTokenReqDTO.class);
        MultitenantUtils multitenantUtils = Mockito.mock(MultitenantUtils.class);
        OAuth2ServiceComponentHolder serviceComponentHolder = Mockito.mock(OAuth2ServiceComponentHolder.class);
        ApplicationManagementService applicationManagementService = Mockito.mock(ApplicationManagementService.class);
        ServiceProvider serviceProvider = Mockito.mock(ServiceProvider.class);
        IdentityTenantUtil identityTenantUtil = Mockito.mock(IdentityTenantUtil.class);
        OAuthComponentServiceHolder authComponentServiceHolder = Mockito.mock(OAuthComponentServiceHolder.class);
        RealmService realmService = Mockito.mock(RealmService.class);
        UserRealm userRealm = Mockito.mock(UserRealm.class);
        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        OAuth2Util oAuth2Util = Mockito.mock(OAuth2Util.class);
        AuthenticatedUser authenticatedUser = Mockito.mock(AuthenticatedUser.class);
        APIKeyMgtDataHolder keyMgtDataHolder = Mockito.mock(APIKeyMgtDataHolder.class);

        ArrayList<String> list = Mockito.mock(ArrayList.class);
        String[] scopes = {"api_view", "api_update"};
        Mockito.when(authServerConfiguration.getInstance()).thenReturn(authServerConfiguration);
        Mockito.when(tokenReqMessageContext.getOauth2AccessTokenReqDTO()).thenReturn(accessTokenReqDTO);
        Mockito.when(accessTokenReqDTO.getResourceOwnerUsername()).thenReturn("user@carbon.super");
        Mockito.when(multitenantUtils.getTenantAwareUsername(Mockito.anyString())).thenReturn("user");
        Mockito.doNothing().when(accessTokenReqDTO).setResourceOwnerUsername("user@carbon.super");
        Mockito.when(multitenantUtils.getTenantDomain(accessTokenReqDTO.getResourceOwnerUsername())).thenReturn("carbon.super");
        Mockito.when(accessTokenReqDTO.getClientId()).thenReturn("1");
        Mockito.when(accessTokenReqDTO.getTenantDomain()).thenReturn("carbon.super");
        Mockito.when(serviceComponentHolder.getApplicationMgtService()).thenReturn(applicationManagementService);
        Mockito.when(applicationManagementService.getServiceProviderByClientId(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(serviceProvider);
        Mockito.when(serviceProvider.isSaasApp()).thenReturn(false);
        Mockito.when(identityTenantUtil.getTenantIdOfUser(Mockito.anyString())).thenReturn(1);
        Mockito.when(authComponentServiceHolder.getInstance()).thenReturn(authComponentServiceHolder);
        Mockito.when(authComponentServiceHolder.getRealmService()).thenReturn(realmService);
        Mockito.when(realmService.getTenantUserRealm(Mockito.anyInt())).thenReturn(userRealm);
        Mockito.when(userRealm.getUserStoreManager()).thenReturn(userStoreManager);
        Mockito.doReturn(true).when(userStoreManager).authenticate(Mockito.anyString(), Mockito.anyObject());
        Mockito.when(oAuth2Util.getUserFromUserName(Mockito.anyString())).thenReturn(authenticatedUser);
        Mockito.doNothing().when(authenticatedUser).setAuthenticatedSubjectIdentifier(Mockito.anyString());
        Mockito.doNothing().when(tokenReqMessageContext).setAuthorizedUser(authenticatedUser);
        Mockito.doNothing().when(tokenReqMessageContext).setScope(scopes);
        Mockito.when(keyMgtDataHolder.getRealmService()).thenReturn(realmService);

        List<String> claimUris = new ArrayList<String>();
        claimUris.add("http://wso2.org/claims/emailaddress");
        claimUris.add("http://wso2.org/claims/gender");
        ExtendedPasswordGrantHandler extendedPasswordGrantHandler = new ExtendedPasswordGrantHandler();
        Field requiredHeaderClaimUrisField = ExtendedPasswordGrantHandler.class.getDeclaredField("requiredHeaderClaimUris");
        requiredHeaderClaimUrisField.setAccessible(true);
        requiredHeaderClaimUrisField.set(extendedPasswordGrantHandler, claimUris);

        BaseCache<String, Claim[]> userClaimsCache = Mockito.mock(BaseCache.class);//new BaseCache<String, Claim[]>("UserClaimsCache");
        Field userClaimCacheField = ExtendedPasswordGrantHandler.class.getDeclaredField("userClaimsCache");
        userClaimCacheField.setAccessible(true);
        userClaimCacheField.set(extendedPasswordGrantHandler, userClaimsCache);

        PowerMockito.mockStatic(UserCoreUtil.class);
        UserCoreUtil userCoreUtil = PowerMockito.mock(UserCoreUtil.class);
        Mockito.when(tokenReqMessageContext.getAuthorizedUser()).thenReturn(authenticatedUser);
        Mockito.when(authenticatedUser.getUserStoreDomain()).thenReturn("test.com");
        Mockito.when(userCoreUtil.addDomainToName(Mockito.anyString(), Mockito.anyString())).thenReturn("user@test.com");

        PrivilegedCarbonContext privilegedCarbonContext = PowerMockito.mock(PrivilegedCarbonContext.class);
        Mockito.when(privilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);
        PowerMockito.doNothing().when(privilegedCarbonContext).setTenantId(Mockito.anyInt());
        PowerMockito.doNothing().when(privilegedCarbonContext).setTenantDomain(Mockito.anyString());

        //validated false so far
        extendedPasswordGrantHandler.validateGrant(tokenReqMessageContext);
    }
}
*/

@RunWith(PowerMockRunner.class)
@PrepareForTest({OAuthServerConfiguration.class, IdentityConfigParser.class, MultitenantUtils.class, OAuth2ServiceComponentHolder.class,
        IdentityTenantUtil.class, OAuthComponentServiceHolder.class, OAuth2Util.class, APIKeyMgtDataHolder.class, UserCoreUtil.class})
public class ExtendedPasswordGrantHandlerTest {

    @Test
    public void testInit() throws Exception {
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        OAuthServerConfiguration authServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        Mockito.when(authServerConfiguration.getInstance()).thenReturn(authServerConfiguration);
        PowerMockito.mockStatic(IdentityConfigParser.class);
        IdentityConfigParser identityConfigParser = Mockito.mock(IdentityConfigParser.class);
        Mockito.when(identityConfigParser.getInstance()).thenReturn(identityConfigParser);
        OMElement omElement = Mockito.mock(OMElement.class);
        Mockito.when(identityConfigParser.getConfigElement(Mockito.anyString())).thenReturn(omElement);

        ExtendedPasswordGrantHandler extendedPasswordGrantHandler = new ExtendedPasswordGrantHandler();
        extendedPasswordGrantHandler.init();

        //set loginConfig
        Mockito.when(omElement.getFirstChildWithName((QName)Mockito.anyObject())).thenReturn(omElement);
        extendedPasswordGrantHandler.init();
    }

    @Test
    public void testValidateGrant() throws Exception {
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        OAuthServerConfiguration authServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        Mockito.when(authServerConfiguration.getInstance()).thenReturn(authServerConfiguration);
        PowerMockito.mockStatic(IdentityConfigParser.class);
        IdentityConfigParser identityConfigParser = Mockito.mock(IdentityConfigParser.class);
        Mockito.when(identityConfigParser.getInstance()).thenReturn(identityConfigParser);
        OMElement omElement = Mockito.mock(OMElement.class);
        Mockito.when(identityConfigParser.getConfigElement(Mockito.anyString())).thenReturn(omElement);

        ExtendedPasswordGrantHandler extendedPasswordGrantHandler = new ExtendedPasswordGrantHandler();

        OAuthTokenReqMessageContext tokenReqMessageContext = Mockito.mock(OAuthTokenReqMessageContext.class);
        OAuth2AccessTokenReqDTO accessTokenReqDTO = Mockito.mock(OAuth2AccessTokenReqDTO.class);
        Mockito.when(tokenReqMessageContext.getOauth2AccessTokenReqDTO()).thenReturn(accessTokenReqDTO);
        Mockito.when(accessTokenReqDTO.getResourceOwnerUsername()).thenReturn("user@test.com");
        PowerMockito.mockStatic(MultitenantUtils.class);
        MultitenantUtils multitenantUtils = Mockito.mock(MultitenantUtils.class);
        Mockito.when(multitenantUtils.getTenantDomain(Mockito.anyString())).thenReturn("test.com");
        Mockito.when(accessTokenReqDTO.getClientId()).thenReturn("1111");
        Mockito.when(accessTokenReqDTO.getTenantDomain()).thenReturn("carbon.super");
        PowerMockito.mockStatic(OAuth2ServiceComponentHolder.class);
        OAuth2ServiceComponentHolder serviceComponentHolder = Mockito.mock(OAuth2ServiceComponentHolder.class);
        ApplicationManagementService applicationManagementService = Mockito.mock(ApplicationManagementService.class);
        Mockito.when(serviceComponentHolder.getApplicationMgtService()).thenReturn(applicationManagementService);
        ServiceProvider serviceProvider = Mockito.mock(ServiceProvider.class);
        Mockito.when(applicationManagementService.getServiceProviderByClientId(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(serviceProvider);

        Assert.assertFalse(extendedPasswordGrantHandler.validateGrant(tokenReqMessageContext));

        //isValidated - true
        Mockito.when(serviceProvider.isSaasApp()).thenReturn(true);
        /*Mockito.when(accessTokenReqDTO.getResourceOwnerUsername()).thenReturn("user@carbon.super");
        Mockito.when(multitenantUtils.getTenantDomain(Mockito.anyString())).thenReturn("carbon.super");*/
        PowerMockito.mockStatic(IdentityTenantUtil.class);
        IdentityTenantUtil identityTenantUtil = Mockito.mock(IdentityTenantUtil.class);
        Mockito.when(multitenantUtils.getTenantAwareUsername(Mockito.anyString())).thenReturn("user");
        Mockito.when(identityTenantUtil.getTenantIdOfUser(Mockito.anyString())).thenReturn(1);
        RealmService realmService = Mockito.mock(RealmService.class);
        PowerMockito.mockStatic(OAuthComponentServiceHolder.class);
        OAuthComponentServiceHolder authComponentServiceHolder = Mockito.mock(OAuthComponentServiceHolder.class);
        Mockito.when(authComponentServiceHolder.getInstance()).thenReturn(authComponentServiceHolder);
        Mockito.when(authComponentServiceHolder.getRealmService()).thenReturn(realmService);
        PowerMockito.mockStatic(APIKeyMgtDataHolder.class);
        APIKeyMgtDataHolder keyMgtDataHolder = Mockito.mock(APIKeyMgtDataHolder.class);
        Mockito.when(keyMgtDataHolder.getRealmService()).thenReturn(realmService);
        UserRealm userRealm = Mockito.mock(UserRealm.class);
        Mockito.when(realmService.getTenantUserRealm(Mockito.anyInt())).thenReturn(userRealm);
        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        Mockito.when(userRealm.getUserStoreManager()).thenReturn(userStoreManager);
        Mockito.when(userStoreManager.authenticate(Mockito.anyString(), Mockito.anyObject())).thenReturn(true);
        PowerMockito.mockStatic(OAuth2Util.class);
        OAuth2Util auth2Util = Mockito.mock(OAuth2Util.class);
        AuthenticatedUser authenticatedUser = Mockito.mock(AuthenticatedUser.class);
        Mockito.when(auth2Util.getUserFromUserName(Mockito.anyString())).thenReturn(authenticatedUser);
        Mockito.doNothing().when(authenticatedUser).setAuthenticatedSubjectIdentifier(Mockito.anyString());
        Mockito.doNothing().when(tokenReqMessageContext).setAuthorizedUser(authenticatedUser);
        String[] scopes = {"api_view", "api_update"};
        Mockito.doNothing().when(tokenReqMessageContext).setScope(scopes);

        Assert.assertTrue(extendedPasswordGrantHandler.validateGrant(tokenReqMessageContext));

        //TenantUserRealmException
       /* Mockito.doThrow(UserStoreException.class).when(userStoreManager).authenticate(Mockito.anyString(), Mockito.anyObject());
        try {
            extendedPasswordGrantHandler.validateGrant(tokenReqMessageContext);
        } catch (Exception e) {
            //Assert.assertTrue("Error when getting the tenant's UserStoreManager".equals(e.getMessage()));
        }*/

        //set requiredHeaderClaimUri
        List<String> claimUris = new ArrayList<String>();
        claimUris.add("http://wso2.org/claims/emailaddress");
        claimUris.add("http://wso2.org/claims/gender");
        Field requiredHeaderClaimUrisField = ExtendedPasswordGrantHandler.class.getDeclaredField("requiredHeaderClaimUris");
        requiredHeaderClaimUrisField.setAccessible(true);
        requiredHeaderClaimUrisField.set(extendedPasswordGrantHandler, claimUris);

        Mockito.when(tokenReqMessageContext.getAuthorizedUser()).thenReturn(authenticatedUser);
        Mockito.when(authenticatedUser.getUserStoreDomain()).thenReturn("test.com");
        PowerMockito.mockStatic(UserCoreUtil.class);
        UserCoreUtil userCoreUtil = Mockito.mock(UserCoreUtil.class);
        Mockito.when(userCoreUtil.addDomainToName(Mockito.anyString(), Mockito.anyString())).thenReturn("user@test.com");

        BaseCache<String, Claim[]> userClaimsCache = Mockito.mock(BaseCache.class);//new BaseCache<String, Claim[]>("UserClaimsCache");
        Field userClaimCacheField = ExtendedPasswordGrantHandler.class.getDeclaredField("userClaimsCache");
        userClaimCacheField.setAccessible(true);
        userClaimCacheField.set(extendedPasswordGrantHandler, userClaimsCache);

        //populate userclaims cache

        Claim claim = Mockito.mock(Claim.class);
        Claim[] claims = {claim, claim};
        Mockito.when(userClaimsCache.getValueFromCache(Mockito.anyString())).thenReturn(claims);
        Assert.assertTrue(extendedPasswordGrantHandler.validateGrant(tokenReqMessageContext));

        //when claim[] is null
        Mockito.when(userClaimsCache.getValueFromCache(Mockito.anyString())).thenReturn(null);
        extendedPasswordGrantHandler.validateGrant(tokenReqMessageContext);

        //isSeconderyUserName - true
        Map<String,Map<String,String>> loginConfiguration1 = new ConcurrentHashMap<String, Map<String,String>>();
        Map<String,String> login1 = new HashMap<String, String>();
        login1.put("primary", "true");
        loginConfiguration1.put("EmailLogin", login1);
        Field loginConfigField = ExtendedPasswordGrantHandler.class.getDeclaredField("loginConfiguration");
        loginConfigField.setAccessible(true);
        loginConfigField.set(extendedPasswordGrantHandler, loginConfiguration1);
        extendedPasswordGrantHandler.validateGrant(tokenReqMessageContext);

    }
}