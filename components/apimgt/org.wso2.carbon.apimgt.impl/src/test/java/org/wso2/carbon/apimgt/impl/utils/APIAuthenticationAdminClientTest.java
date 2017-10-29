package org.wso2.carbon.apimgt.impl.utils;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.handlers.security.stub.APIAuthenticationServiceStub;
import org.wso2.carbon.apimgt.handlers.security.stub.types.APIKeyMapping;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationServiceImpl;
import org.wso2.carbon.apimgt.impl.dto.Environment;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.utils.ConfigurationContextService;

import java.rmi.RemoteException;
import java.util.Collections;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {APIAuthenticationAdminClient.class})
public class APIAuthenticationAdminClientTest {
    private APIAuthenticationServiceStub apiAuthenticationServiceStub;
    private Environment environment;
    private AuthenticationAdminStub authAdminStub;
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String ENV_NAME = "test-environment";
    private final String SERVER_URL = "https://localhost.com";

    @Before
    public void setup() throws Exception {
        environment = new Environment();
        environment.setName(ENV_NAME);
        environment.setPassword(PASSWORD);
        environment.setUserName(USERNAME);
        environment.setServerURL(SERVER_URL);
        apiAuthenticationServiceStub = Mockito.mock(APIAuthenticationServiceStub.class);
        Options options = new Options();
        ServiceContext serviceContext = new ServiceContext();
        OperationContext operationContext = Mockito.mock(OperationContext.class);
        serviceContext.setProperty(HTTPConstants.COOKIE_STRING, "");
        ServiceClient serviceClient = Mockito.mock(ServiceClient.class);
        authAdminStub = Mockito.mock(AuthenticationAdminStub.class);
        Mockito.doReturn(true).when(authAdminStub).login(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(authAdminStub._getServiceClient()).thenReturn(serviceClient);
        Mockito.when(serviceClient.getLastOperationContext()).thenReturn(operationContext);
        Mockito.when(operationContext.getServiceContext()).thenReturn(serviceContext);
        Mockito.when(apiAuthenticationServiceStub._getServiceClient()).thenReturn(serviceClient);
        Mockito.when(serviceClient.getOptions()).thenReturn(options);
        PowerMockito.whenNew(APIAuthenticationServiceStub.class).withArguments(Mockito.any(ConfigurationContext
                .class), Mockito.anyString()).thenReturn(apiAuthenticationServiceStub);
        PowerMockito.whenNew(AuthenticationAdminStub.class).withArguments(Mockito.any(ConfigurationContext
                .class), Mockito.anyString()).thenReturn(authAdminStub);
    }

    @Test
    public void createAPIAuthenticationAdminClientKeyCacheEnable() throws Exception {
        Mockito.when(authAdminStub.login(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn
                (true).thenThrow(RemoteException.class).thenThrow(LoginAuthenticationExceptionException.class);
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        ServiceReferenceHolder.getInstance().setAPIManagerConfigurationService(new APIManagerConfigurationServiceImpl
                (apiManagerConfiguration));
        ConfigurationContext configurationContext = Mockito.mock(ConfigurationContext.class);
        ServiceReferenceHolder.setContextService(new ConfigurationContextService(configurationContext,
                configurationContext));
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.KEY_MANAGER_TOKEN_CACHE)).thenReturn
                ("true");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.GATEWAY_TOKEN_CACHE_ENABLED)).thenReturn
                ("false");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_USERNAME)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_PASSWORD)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_URL)).thenReturn
                ("http://localhost");

        APIAuthenticationAdminClient apiAuthenticationAdminClient = new APIAuthenticationAdminClient(environment);
        try {
            new APIAuthenticationAdminClient(environment);
            Assert.fail();
        }catch (AxisFault axisFault){
            Assert.assertTrue(axisFault.getMessage().contains("Error while contacting the authentication admin services"));
        }
        try {
            new APIAuthenticationAdminClient(environment);
            Assert.fail();
        }catch (AxisFault axisFault){
            Assert.assertTrue(axisFault.getMessage().contains("Error while authenticating against the API keyMgt admin"));
        }
    }

    @Test
    public void createAPIAuthenticationAdminClientKGwCacheEnable() throws Exception {
        Mockito.when(authAdminStub.login(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn
                (true).thenThrow(RemoteException.class).thenThrow(LoginAuthenticationExceptionException.class);
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        ServiceReferenceHolder.getInstance().setAPIManagerConfigurationService(new APIManagerConfigurationServiceImpl
                (apiManagerConfiguration));
        ConfigurationContext configurationContext = Mockito.mock(ConfigurationContext.class);
        ServiceReferenceHolder.setContextService(new ConfigurationContextService(configurationContext,
                configurationContext));
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.KEY_MANAGER_TOKEN_CACHE)).thenReturn
                ("false");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.GATEWAY_TOKEN_CACHE_ENABLED)).thenReturn
                ("true");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_USERNAME)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_PASSWORD)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_URL)).thenReturn
                ("http://localhost");

        APIAuthenticationAdminClient apiAuthenticationAdminClient = new APIAuthenticationAdminClient(environment);
        try {
            new APIAuthenticationAdminClient(environment);
            Assert.fail();
        }catch (AxisFault axisFault){
            Assert.assertTrue(axisFault.getMessage().contains("Error while contacting the authentication admin services"));
        }
        try {
            new APIAuthenticationAdminClient(environment);
            Assert.fail();
        }catch (AxisFault axisFault){
            Assert.assertTrue(axisFault.getMessage().contains("Error while authenticating against the API gateway admin"));
        }
    }

    @Test
    public void invalidateKeys() throws Exception {
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        ServiceReferenceHolder.getInstance().setAPIManagerConfigurationService(new APIManagerConfigurationServiceImpl
                (apiManagerConfiguration));
        ConfigurationContext configurationContext = Mockito.mock(ConfigurationContext.class);
        ServiceReferenceHolder.setContextService(new ConfigurationContextService(configurationContext,
                configurationContext));
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.KEY_MANAGER_TOKEN_CACHE)).thenReturn
                ("false");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.GATEWAY_TOKEN_CACHE_ENABLED)).thenReturn
                ("true");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_USERNAME)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_PASSWORD)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_URL)).thenReturn
                ("http://localhost");

        APIAuthenticationAdminClient apiAuthenticationAdminClient = new APIAuthenticationAdminClient(environment);
        Mockito.doNothing().doThrow(RemoteException.class).when(apiAuthenticationServiceStub).invalidateKeys(Mockito
                .any(APIKeyMapping[].class));
        apiAuthenticationAdminClient.invalidateKeys(Mockito.anyListOf(APIKeyMapping.class));
        try {
            apiAuthenticationAdminClient.invalidateKeys(Mockito.anyListOf(APIKeyMapping.class));
            Assert.fail();
        }catch (AxisFault axisFault){
            Assert.assertTrue(axisFault.getMessage().contains("Error while invalidating API keys"));
        }
    }

    @Test
    public void invalidateOAuthKeys() throws Exception {
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        ServiceReferenceHolder.getInstance().setAPIManagerConfigurationService(new APIManagerConfigurationServiceImpl
                (apiManagerConfiguration));
        ConfigurationContext configurationContext = Mockito.mock(ConfigurationContext.class);
        ServiceReferenceHolder.setContextService(new ConfigurationContextService(configurationContext,
                configurationContext));
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.KEY_MANAGER_TOKEN_CACHE)).thenReturn
                ("false");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.GATEWAY_TOKEN_CACHE_ENABLED)).thenReturn
                ("true");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_USERNAME)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_PASSWORD)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_URL)).thenReturn
                ("http://localhost");

        APIAuthenticationAdminClient apiAuthenticationAdminClient = new APIAuthenticationAdminClient(environment);
        Mockito.doNothing().doThrow(RemoteException.class).when(apiAuthenticationServiceStub).invalidateOAuthKeys
                (Mockito.anyString(), Mockito.anyString());
        apiAuthenticationAdminClient.invalidateOAuthKeys("","");
        try {
            apiAuthenticationAdminClient.invalidateOAuthKeys("","");
            Assert.fail();
        }catch (AxisFault axisFault){
            Assert.assertTrue(axisFault.getMessage().contains("Error while invalidating API keys"));
        }
    }

    @Test
    public void invalidateResourceCache() throws Exception {
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        ServiceReferenceHolder.getInstance().setAPIManagerConfigurationService(new APIManagerConfigurationServiceImpl
                (apiManagerConfiguration));
        ConfigurationContext configurationContext = Mockito.mock(ConfigurationContext.class);
        ServiceReferenceHolder.setContextService(new ConfigurationContextService(configurationContext,
                configurationContext));
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.KEY_MANAGER_TOKEN_CACHE)).thenReturn
                ("false");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.GATEWAY_TOKEN_CACHE_ENABLED)).thenReturn
                ("true");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_USERNAME)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_PASSWORD)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_URL)).thenReturn
                ("http://localhost");

        APIAuthenticationAdminClient apiAuthenticationAdminClient = new APIAuthenticationAdminClient(environment);
        Mockito.doNothing().doThrow(RemoteException.class).when(apiAuthenticationServiceStub).invalidateResourceCache(
                Mockito.anyString(),Mockito.anyString(), Mockito.anyString(),Mockito.anyString());
        apiAuthenticationAdminClient.invalidateResourceCache("","","","");
        try {
            apiAuthenticationAdminClient.invalidateResourceCache("","","","");
            Assert.fail();
        }catch (AxisFault axisFault){
            Assert.assertTrue(axisFault.getMessage().contains("Error while invalidating API keys"));
        }
    }

    @Test
    public void invalidateCachedTokens() throws Exception {
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        ServiceReferenceHolder.getInstance().setAPIManagerConfigurationService(new APIManagerConfigurationServiceImpl
                (apiManagerConfiguration));
        ConfigurationContext configurationContext = Mockito.mock(ConfigurationContext.class);
        ServiceReferenceHolder.setContextService(new ConfigurationContextService(configurationContext,
                configurationContext));
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.KEY_MANAGER_TOKEN_CACHE)).thenReturn
                ("false");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.GATEWAY_TOKEN_CACHE_ENABLED)).thenReturn
                ("true");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_USERNAME)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_PASSWORD)).thenReturn
                ("admin");
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_URL)).thenReturn
                ("http://localhost");

        APIAuthenticationAdminClient apiAuthenticationAdminClient = new APIAuthenticationAdminClient(environment);
        Mockito.doNothing().doThrow(RemoteException.class).when(apiAuthenticationServiceStub).invalidateCachedTokens(
                (Mockito.any(String[].class)));
        apiAuthenticationAdminClient.invalidateCachedTokens(Collections.<String>emptySet());
        try {
            apiAuthenticationAdminClient.invalidateCachedTokens(Collections.<String>emptySet());
            Assert.fail();
        }catch (AxisFault axisFault){
            Assert.assertTrue(axisFault.getMessage().contains("RemoteException occurred when calling service method " +
                    "'invalidateCachedTokens' of "));
        }
    }

}