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
import org.wso2.carbon.apimgt.api.model.Scope;
import org.wso2.carbon.apimgt.api.model.URITemplate;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.TestUtils;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.api.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServiceReferenceHolder.class })
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

        String expectedResult = "{\"apiVersion\":\"1.0.0\",\"swaggerVersion\":\"1.2\",\"apis\":[{\"path\":\"\\/test\""
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

    @Test
    public void getURITemplatesWithResourceConfigTest()
            throws UserStoreException, RegistryException, APIManagementException {
        APIDefinitionFromSwagger12 apiDefinitionFromSwagger12 = new APIDefinitionFromSwagger12();

        String resourceConfig = "{\"api_doc\": {\"apiVersion\" : \"1.1.1\", \"swaggerVersion\" : \"1.2\", \""
                + "authorizations\" : {\"oauth2\" : {\"scopes\" : [{\"description\" : \"descrptiion\", \"roles\" : "
                + "\"role1\", \"name\" : \"view api\", \"key\" : \"api_view\"}, {\"description\" : \"description\", "
                + "\"roles\" : \"role2\", \"name\" : \"api manage\", \"key\" : \"api_manage\"}], \"type\" : \"oauth2\"}"
                + "}, \"apis\" : [{\"description\" : \"\", \"path\" : \"/foo\"}, {\"description\" : \"\", \"path\" : "
                + "\"/bar\"}, {\"description\" : \"\", \"path\" : \"/yoo\"}], \"info\" : {\"termsOfServiceUrl\" : \"\","
                + " \"title\" : \"TestApi\", \"description\" : \"api_description\", \"license\" : \"\", \"contact\" : "
                + "\"\", \"licenseUrl\" : \"\"}}}";

        Set<Scope> result = apiDefinitionFromSwagger12.getScopes(resourceConfig);
        Assert.assertEquals(result.size(), 2);
    }

    @Test
    public void getAPIDefinitionWithRegistryWhenDoesNotExists()
            throws UserStoreException, org.wso2.carbon.registry.api.RegistryException, APIManagementException {

        APIDefinitionFromSwagger12 apiDefinitionFromSwagger12 = new APIDefinitionFromSwagger12();

        APIIdentifier apiIdentifier = new APIIdentifier("admin", "PhoneVerification", "1.0.0");
        API api = new API(apiIdentifier);
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

        Registry registry = Mockito.mock(Registry.class);
        String result = apiDefinitionFromSwagger12.getAPIDefinition(apiIdentifier, registry);
        Assert.assertEquals(null, result);

    }

    @Test
    public void getAPIDefinitionWithRegistryWithPassError()
            throws UserStoreException, org.wso2.carbon.registry.api.RegistryException, APIManagementException {

        APIDefinitionFromSwagger12 apiDefinitionFromSwagger12 = new APIDefinitionFromSwagger12();

        APIIdentifier apiIdentifier = new APIIdentifier("admin", "PhoneVerification", "1.0.0");
        API api = new API(apiIdentifier);
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

        Registry registry = Mockito.mock(Registry.class);
        Mockito.when(registry.resourceExists(Mockito.anyString())).thenReturn(true);

        String resourceConfig = "{\"api_doc\": {\"apiVersion\" : \"1.1.1\", \"swaggerVersion\" : \"1.2\", \""
                + "authorizations\" : {\"oauth2\" : {\"scopes\" : [{\"description\" : \"descrptiion\", \"roles\" : "
                + "\"role1\", \"name\" : \"view api\", \"key\" : \"api_view\"}, {\"description\" : \"description\", "
                + "\"roles\" : \"role2\", \"name\" : \"api manage\", \"key\" : \"api_manage\"}], \"type\" : \"oauth2\"}"
                + "}, \"apis\" : [{\"description\" : \"\", \"path\" : \"/foo\"}, {\"description\" : \"\", \"path\" : "
                + "\"/bar\"}, {\"description\" : \"\", \"path\" : \"/yoo\"}], \"info\" : {\"termsOfServiceUrl\" : \"\","
                + " \"title\" : \"TestApi\", \"description\" : \"api_description\", \"license\" : \"\", \"contact\" : "
                + "\"\", \"licenseUrl\" : \"\"}}}";

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(registry.get(Mockito.anyString())).thenReturn(resource);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] sink = bos.toByteArray();
        Mockito.when(resource.getContent()).thenReturn(sink);

        try {
            apiDefinitionFromSwagger12.getAPIDefinition(apiIdentifier, registry);
        } catch (APIManagementException e) {
            String expectedResult = "Error while parsing Swagger Definition for PhoneVerification-1.0.0 in /apimgt/"
                    + "applicationdata/api-docs/PhoneVerification-1.0.0-admin/1.2";
            Assert.assertEquals(expectedResult, e.getMessage());
        }
    }

    @Test
    public void getAPIDefinitionWithRegistry()
            throws UserStoreException, org.wso2.carbon.registry.api.RegistryException, APIManagementException {

        APIDefinitionFromSwagger12 apiDefinitionFromSwagger12 = new APIDefinitionFromSwagger12();

        APIIdentifier apiIdentifier = new APIIdentifier("admin", "PhoneVerification", "1.0.0");
        API api = new API(apiIdentifier);
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

        Registry registry = Mockito.mock(Registry.class);
        Mockito.when(registry.resourceExists(Mockito.anyString())).thenReturn(true);

        String apiConfig = "{\"apiVersion\":\"1.0.0\",\"swaggerVersion\":\"1.2\",\"apis\":[{\"path\":\"\\/test\""
                + ",\"file\":{\"apiVersion\":\"1.0.0\",\"swaggerVersion\":\"1.2\",\"apis\":[{\"path\":\"\\"
                + "/test\\/\",\"operations\":[{\"auth_type\":\"Application\",\"throttling_tier\":\"Unlimited\",\""
                + "method\":\"\",\"parameters\":[]}]}],\"resourcePath\":\"\\/test\",\"authorizations\":"
                + "{\"oauth2\":{\"scopes\":[],\"type\":\"oauth2\"}},\"info\":{\"license\":\"\",\"licenseUrl\""
                + ":\"\",\"termsOfServiceUrl\":\"\",\"contact\":\"\",\"description\":\"\",\"title\":\"\"}},"
                + "\"description\":\"\"}],\"authorizations\":{\"oauth2\":{\"scopes\":[],\"type\":\"oauth2\"}},"
                + "\"info\":{\"license\":\"\",\"licenseUrl\":\"\",\"termsOfServiceUrl\":\"\",\"contact\":\"\","
                + "\"description\":\"\",\"title\":\"\"}}";


        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(registry.get(Mockito.anyString())).thenReturn(resource);

        byte[] sink = apiConfig.getBytes();
        Mockito.when(resource.getContent()).thenReturn(sink);

        String result = apiDefinitionFromSwagger12.getAPIDefinition(apiIdentifier, registry);

        String expectedResult = "{\"apiVersion\":\"1.0.0\",\"swaggerVersion\":\"1.2\",\"apis\":[{\"path\":\"\\/test\","
                + "\"file\":{\"apiVersion\":\"1.0.0\",\"swaggerVersion\":\"1.2\",\"apis\":[{\"path\":\"\\/test\",\"file"
                + "\":{\"apiVersion\":\"1.0.0\",\"swaggerVersion\":\"1.2\",\"apis\":[{\"path\":\"\\/test\\/\",\""
                + "operations\":[{\"auth_type\":\"Application\",\"throttling_tier\":\"Unlimited\",\"method\":\"\",\""
                + "parameters\":[]}]}],\"resourcePath\":\"\\/test\",\"authorizations\":{\"oauth2\":{\"scopes\":[],\""
                + "type\":\"oauth2\"}},\"info\":{\"license\":\"\",\"licenseUrl\":\"\",\"termsOfServiceUrl\":\"\",\""
                + "contact\":\"\",\"description\":\"\",\"title\":\"\"}},\"description\":\"\"}],\"authorizations\":{\""
                + "oauth2\":{\"scopes\":[],\"type\":\"oauth2\"}},\"info\":{\"license\":\"\",\"licenseUrl\":\"\",\""
                + "termsOfServiceUrl\":\"\",\"contact\":\"\",\"description\":\"\",\"title\":\"\"}},\"description\":\"\""
                + "}],\"authorizations\":{\"oauth2\":{\"scopes\":[],\"type\":\"oauth2\"}},\"info\":{\"license\":\"\",\""
                + "licenseUrl\":\"\",\"termsOfServiceUrl\":\"\",\"contact\":\"\",\"description\":\"\",\"title\":\"\"}}";
        Assert.assertEquals(expectedResult, result);
    }
}
