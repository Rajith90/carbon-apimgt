package org.wso2.carbon.apimgt.keymgt.handlers;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OAuthServerConfiguration.class, String.class})
public class ApplicationTokenGrantHandlerTest {
    @Test
    public void testAuthorizeAccessDelegation() throws Exception {
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        String str = PowerMockito.mock(String.class);

        OAuthTokenReqMessageContext authTokenReqMessageContext = Mockito.mock(OAuthTokenReqMessageContext.class);
        OAuthServerConfiguration authServerConfiguration = Mockito.mock(OAuthServerConfiguration.class);
        OAuth2AccessTokenReqDTO accessTokenReqDTO = Mockito.mock(OAuth2AccessTokenReqDTO.class);
        RequestParameter param = Mockito.mock(RequestParameter.class);

        RequestParameter[] params = new RequestParameter[2];
        params[0] = param;
        params[1] = param;
        String[] paramsValues = {"0", "1"};

        Mockito.when(authServerConfiguration.getInstance()).thenReturn(authServerConfiguration);
        Mockito.when(authTokenReqMessageContext.getOauth2AccessTokenReqDTO()).thenReturn(accessTokenReqDTO);
        Mockito.when(accessTokenReqDTO.getRequestParameters()).thenReturn(params);
        Mockito.when(param.getKey()).thenReturn("validity_period");
        Mockito.when(str.equals(Mockito.anyString())).thenReturn(true);
        Mockito.when(param.getValue()).thenReturn(paramsValues);

        ApplicationTokenGrantHandler tokenGrantHandler = new ApplicationTokenGrantHandler();
        Assert.assertTrue(tokenGrantHandler.authorizeAccessDelegation(authTokenReqMessageContext));

        //when validity period not null and not zero
        String[] paramsValues2 = {"1", "1"};
        Mockito.when(param.getValue()).thenReturn(paramsValues2);
        Assert.assertTrue(tokenGrantHandler.authorizeAccessDelegation(authTokenReqMessageContext));

        //params equal to null
        Mockito.when(accessTokenReqDTO.getRequestParameters()).thenReturn(null);
        Assert.assertTrue(tokenGrantHandler.authorizeAccessDelegation(authTokenReqMessageContext));
    }

}