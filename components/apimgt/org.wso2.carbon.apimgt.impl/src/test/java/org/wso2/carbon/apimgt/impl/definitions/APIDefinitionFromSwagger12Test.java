package org.wso2.carbon.apimgt.impl.definitions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.URITemplate;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.TestUtils;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.UserStoreException;

import java.util.LinkedHashSet;
import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServiceReferenceHolder.class })
public class APIDefinitionFromSwagger12Test {

    private ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
    private APIManagerConfigurationService apiManagerConfigurationService = Mockito
            .mock(APIManagerConfigurationService.class);
    private APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService())
                .thenReturn(apiManagerConfigurationService);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
    }

    @Test
    public void getURITemplatesTest() throws APIManagementException, UserStoreException, RegistryException {

        APIDefinitionFromSwagger12 apiDefinitionFromSwagger12 = new APIDefinitionFromSwagger12();
        API api = new API(new APIIdentifier("admin", "PhoneVerification", "1.0.0"));
        Set<URITemplate> uriTemplates = new LinkedHashSet<URITemplate>();
        URITemplate template = new URITemplate();
        template.setUriTemplate("/test");
        template.setHTTPVerb("GET");
        template.setThrottlingTier("Unlimited");
        template.setAuthType("Application");
        template.setResourceURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        template.setResourceSandboxURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        uriTemplates.add(template);
        api.setUriTemplates(uriTemplates);
        TestUtils.mockAPIMConfiguration();

        String expectedResult =
                "{\"apiVersion\":\"1.0.0\",\"swaggerVersion\":\"1.2\",\"apis\":[{\"path\":\"\\/default\""
                        + ",\"file\":{\"apiVersion\":\"1.0.0\",\"swaggerVersion\":\"1.2\",\"apis\":[{\"path\":\"\\"
                        + "/test\",\"operations\":[{\"auth_type\":\"Application\",\"throttling_tier\":\"Unlimited\",\""
                        + "method\":\"\",\"parameters\":[]}]}],\"resourcePath\":\"\\/default\",\"authorizations\":"
                        + "{\"oauth2\":{\"scopes\":[],\"type\":\"oauth2\"}},\"info\":{\"license\":\"\",\"licenseUrl\""
                        + ":\"\",\"termsOfServiceUrl\":\"\",\"contact\":\"\",\"description\":\"\",\"title\":\"\"}},"
                        + "\"description\":\"\"}],\"authorizations\":{\"oauth2\":{\"scopes\":[],\"type\":\"oauth2\"}},"
                        + "\"info\":{\"license\":\"\",\"licenseUrl\":\"\",\"termsOfServiceUrl\":\"\",\"contact\":\"\","
                        + "\"description\":\"\",\"title\":\"\"}}";

        String result = apiDefinitionFromSwagger12.generateAPIDefinition(api);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void getURITemplatesTestCustomResourcePath()
            throws APIManagementException, UserStoreException, RegistryException {

        APIDefinitionFromSwagger12 apiDefinitionFromSwagger12 = new APIDefinitionFromSwagger12();
        API api = new API(new APIIdentifier("admin", "PhoneVerification", "1.0.0"));
        Set<URITemplate> uriTemplates = new LinkedHashSet<URITemplate>();
        URITemplate template = new URITemplate();
        template.setUriTemplate("/test/");
        template.setHTTPVerb("GET");
        template.setThrottlingTier("Unlimited");
        template.setAuthType("Application");
        template.setResourceURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        template.setResourceSandboxURI("http://maps.googleapis.com/maps/api/geocode/json?address=Colombo");
        uriTemplates.add(template);
        api.setUriTemplates(uriTemplates);
        TestUtils.mockAPIMConfiguration();

        String expectedResult =
                "{\"apiVersion\":\"1.0.0\",\"swaggerVersion\":\"1.2\",\"apis\":[{\"path\":\"\\/test\""
                        + ",\"file\":{\"apiVersion\":\"1.0.0\",\"swaggerVersion\":\"1.2\",\"apis\":[{\"path\":\"\\"
                        + "/test\\/\",\"operations\":[{\"auth_type\":\"Application\",\"throttling_tier\":\"Unlimited\",\""
                        + "method\":\"\",\"parameters\":[]}]}],\"resourcePath\":\"\\/test\",\"authorizations\":"
                        + "{\"oauth2\":{\"scopes\":[],\"type\":\"oauth2\"}},\"info\":{\"license\":\"\",\"licenseUrl\""
                        + ":\"\",\"termsOfServiceUrl\":\"\",\"contact\":\"\",\"description\":\"\",\"title\":\"\"}},"
                        + "\"description\":\"\"}],\"authorizations\":{\"oauth2\":{\"scopes\":[],\"type\":\"oauth2\"}},"
                        + "\"info\":{\"license\":\"\",\"licenseUrl\":\"\",\"termsOfServiceUrl\":\"\",\"contact\":\"\","
                        + "\"description\":\"\",\"title\":\"\"}}";

        String result = apiDefinitionFromSwagger12.generateAPIDefinition(api);
        Assert.assertEquals(expectedResult, result);
    }
}
