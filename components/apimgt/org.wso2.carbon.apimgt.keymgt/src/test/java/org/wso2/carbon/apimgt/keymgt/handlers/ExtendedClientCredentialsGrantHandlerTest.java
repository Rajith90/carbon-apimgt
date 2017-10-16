package org.wso2.carbon.apimgt.keymgt.handlers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.keymgt.ScopesIssuer;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OAuthServerConfiguration.class, String.class, ScopesIssuer.class})
public class ExtendedClientCredentialsGrantHandlerTest {

    @Test
    public void testAuthorizeAccessDelegation() throws Exception {
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        String string = PowerMockito.mock(String.class);

        OAuthTokenReqMessageContext oAuthTokenReqMessageContext = Mockito.mock(OAuthTokenReqMessageContext.class);
        OAuthServerConfiguration oAuthServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO = Mockito.mock(OAuth2AccessTokenReqDTO.class);
        RequestParameter requestParameter = Mockito.mock(RequestParameter.class);

        Mockito.when(oAuthServerConfiguration.getInstance()).thenReturn(oAuthServerConfiguration);
        Mockito.when(oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO()).thenReturn(oAuth2AccessTokenReqDTO);
        RequestParameter[] requestParameters = new RequestParameter[2];
        requestParameters[0] = requestParameter;
        requestParameters[1] = requestParameter;

        String[] params = {"0", "1"};

        Mockito.when(oAuth2AccessTokenReqDTO.getRequestParameters()).thenReturn(requestParameters);
        Mockito.when(requestParameter.getValue()).thenReturn(params);
        Mockito.when(requestParameter.getKey()).thenReturn("validity_period");
        Mockito.when(string.equals(Mockito.anyString())).thenReturn(true);

        ExtendedClientCredentialsGrantHandler eccGrantHandler = new ExtendedClientCredentialsGrantHandler();
        Assert.assertTrue(eccGrantHandler.authorizeAccessDelegation(oAuthTokenReqMessageContext));

        //when parameter key does not equal to validity_period
        Mockito.when(string.equals(Mockito.anyString())).thenReturn(false);
        eccGrantHandler.authorizeAccessDelegation(oAuthTokenReqMessageContext);

        //parameters equal to null
        Mockito.when(oAuth2AccessTokenReqDTO.getRequestParameters()).thenReturn(null);
        Assert.assertTrue(eccGrantHandler.authorizeAccessDelegation(oAuthTokenReqMessageContext));
    }

    @Test
    public void testValidateGrant() throws Exception {
        PowerMockito.mockStatic(OAuthServerConfiguration.class);

        OAuthTokenReqMessageContext oAuthTokenReqMessageContext = Mockito.mock(OAuthTokenReqMessageContext.class);
        OAuthServerConfiguration oAuthServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO = Mockito.mock(OAuth2AccessTokenReqDTO.class);
        AuthenticatedUser authenticatedUser = Mockito.mock(AuthenticatedUser.class);

        String[] scopes = {"api_view", "api_update"};
        Mockito.when(oAuthServerConfiguration.getInstance()).thenReturn(oAuthServerConfiguration);
        Mockito.doNothing().when(oAuthTokenReqMessageContext).setScope(scopes);
        Mockito.when(oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO()).thenReturn(oAuth2AccessTokenReqDTO);
        Mockito.when(oAuth2AccessTokenReqDTO.getScope()).thenReturn(scopes);
        Mockito.when(oAuthTokenReqMessageContext.getAuthorizedUser()).thenReturn(authenticatedUser);
        Mockito.when(authenticatedUser.getUserName()).thenReturn("abcd");

        Assert.assertTrue("abcd".equals(authenticatedUser.getUserName()));
        ExtendedClientCredentialsGrantHandler eccGrantHandler = new ExtendedClientCredentialsGrantHandler();
        eccGrantHandler.validateGrant(oAuthTokenReqMessageContext);
    }

    @Test
    public void testValidateScope() throws Exception {
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        PowerMockito.mockStatic(ScopesIssuer.class);

        String[] scopes = {"api_view", "api_update"};

        OAuthTokenReqMessageContext oAuthTokenReqMessageContext = Mockito.mock(OAuthTokenReqMessageContext.class);
        OAuthServerConfiguration oAuthServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        ScopesIssuer scopesIssuer = Mockito.mock(ScopesIssuer.class);

        Mockito.when(oAuthServerConfiguration.getInstance()).thenReturn(oAuthServerConfiguration);
        Mockito.when(scopesIssuer.getInstance()).thenReturn(scopesIssuer);
        Mockito.when(scopesIssuer.setScopes(oAuthTokenReqMessageContext)).thenReturn(true);
        Mockito.when(oAuthTokenReqMessageContext.getScope()).thenReturn(scopes);

        ExtendedClientCredentialsGrantHandler eccgHandler = new ExtendedClientCredentialsGrantHandler();
        Assert.assertTrue(eccgHandler.validateScope(oAuthTokenReqMessageContext));
    }

}