/*
 *
 *    Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *    WSO2 Inc. licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except
 *    in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.wso2.carbon.apimgt.impl.utils;

import com.google.gson.Gson;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.Application;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerAnalyticsConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.dto.Environment;
import org.wso2.carbon.apimgt.impl.dto.ThrottleProperties;
import org.wso2.carbon.apimgt.impl.internal.APIManagerComponent;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.caching.impl.Util;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.config.Mount;
import org.wso2.carbon.registry.core.config.RegistryContext;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.service.TenantRegistryLoader;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.core.utils.RegistryUtils;
import org.wso2.carbon.registry.indexing.service.TenantIndexingLoader;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.ConfigurationContextService;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.eq;

/**
 * Test class for APIUtil lines 2924 - 3161, 4964- 5292, 6576- 6648.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LogFactory.class, ServiceReferenceHolder.class, RegistryUtils.class, DocumentBuilderFactory.class,
        APIManagerComponent.class, PrivilegedCarbonContext.class, ApiMgtDAO.class, ServerConfiguration.class,
        CarbonUtils.class, Util.class, CarbonContext.class/*, Caching.class*/})
public class APIUtilTestCase {
    private RegistryContext registryContext;
    private UserRegistry registry;
    private RegistryService registryService;
    private APIManagerConfigurationService apiMgtConfigurationService;
    private APIManagerConfiguration apiManagerConfiguration;
    private Environment environment;
    private PrivilegedCarbonContext privilegedCarbonContext;
    private ServerConfiguration serverConfiguration;
    private ConfigurationContextService configurationContextService;
    private AxisConfiguration axisConfiguration;
    private ApiMgtDAO apiMgtDAO;

    private Resource resource;
    private int tenantId = 1;
    private String tenantDomain = "abc.com";

    @Before
    public void setup() throws org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {
        System.setProperty("carbon.home", "");
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        registryService = Mockito.mock(RegistryService.class);
        apiMgtConfigurationService = Mockito.mock(APIManagerConfigurationService.class);
        apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        environment = Mockito.mock(Environment.class);
        registry = Mockito.mock(UserRegistry.class);
        UserRegistry superTenantRegistry = Mockito.mock(UserRegistry.class);
        resource = Mockito.mock(Resource.class);
        registryContext = Mockito.mock(RegistryContext.class);
        configurationContextService = Mockito.mock(ConfigurationContextService.class);
        axisConfiguration = Mockito.mock(AxisConfiguration.class);
        ConfigurationContext configurationContext = Mockito.mock(ConfigurationContext.class);
        TenantIndexingLoader tenantIndexingLoader = Mockito.mock(TenantIndexingLoader.class);
        TenantRegistryLoader tenantRegistryLoader = Mockito.mock(TenantRegistryLoader.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.mockStatic(APIManagerComponent.class);
        PowerMockito.mockStatic(RegistryUtils.class);
        PowerMockito.mockStatic(ServerConfiguration.class);
        PowerMockito.mockStatic(CarbonUtils.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService()).thenReturn(apiMgtConfigurationService);
        Mockito.when(serviceReferenceHolder.getContextService()).thenReturn(configurationContextService);
        Mockito.when(apiMgtConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
        Mockito.when(configurationContextService.getServerConfigContext()).thenReturn(configurationContext);
        Mockito.when(configurationContext.getAxisConfiguration()).thenReturn(axisConfiguration);
        Mockito.when(environment.getName()).thenReturn("prod");
        Map<String, Environment> environmentMap = new HashMap<String, Environment>();
        environmentMap.put("prod", environment);
        Mockito.when(apiManagerConfiguration.getApiGatewayEnvironments()).thenReturn(environmentMap);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);
        Mockito.when(registryService.getGovernanceSystemRegistry()).thenReturn(registry);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(-1234))).thenReturn(superTenantRegistry);
        Mockito.when(registry.newResource()).thenReturn(resource);
        Mockito.when(superTenantRegistry.newResource()).thenReturn(resource);
        PowerMockito.when(APIManagerComponent.getTenantRegistryLoader()).thenReturn(tenantRegistryLoader);
        Mockito.doNothing().when(resource).setContent(Matchers.anyString());
        Mockito.when(registryService.getConfigSystemRegistry(tenantId)).thenReturn(registry);

        RealmService realmService = Mockito.mock(RealmService.class);
        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        UserRealm userRealm = Mockito.mock(UserRealm.class);

        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
        Mockito.when(serviceReferenceHolder.getIndexLoaderService()).thenReturn(tenantIndexingLoader);

        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(realmService.getTenantUserRealm(Mockito.anyInt())).thenReturn(userRealm);
        org.wso2.carbon.user.api.AuthorizationManager authManager
                = Mockito.mock(org.wso2.carbon.user.api.AuthorizationManager.class);
        Mockito.when(userRealm.getAuthorizationManager()).thenReturn(authManager);

        PowerMockito.mockStatic(CarbonContext.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        serverConfiguration = Mockito.mock(ServerConfiguration.class);

        PowerMockito.when(ServerConfiguration.getInstance()).thenReturn(serverConfiguration);
        Mockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);


    }

    @Test
    public void testLoadTenantGAConfig() throws APIManagementException,
            org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {

        PowerMockito.when(RegistryUtils.getAbsolutePath(registryContext,
                RegistryConstants.GOVERNANCE_REGISTRY_BASE_PATH) + APIConstants.GA_CONFIGURATION_LOCATION)
                .thenReturn("path");

        APIUtil.loadTenantGAConfig(tenantId);
    }


    @Test
    public void testLoadTenantWorkFlowExtensions() throws APIManagementException,
            org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {

        PowerMockito.when(RegistryUtils.getAbsolutePath(registryContext,
                RegistryConstants.GOVERNANCE_REGISTRY_BASE_PATH) + APIConstants.GA_CONFIGURATION_LOCATION)
                .thenReturn("path");

        APIUtil.loadTenantWorkFlowExtensions(tenantId);
    }

    @Test
    public void testLoadTenantSelfSignUpConfigurations() throws APIManagementException,
            org.wso2.carbon.registry.core.exceptions.RegistryException, UserStoreException {

        PowerMockito.when(RegistryUtils.getAbsolutePath(registryContext,
                RegistryConstants.GOVERNANCE_REGISTRY_BASE_PATH) + APIConstants.GA_CONFIGURATION_LOCATION)
                .thenReturn("path");
        APIUtil.loadTenantSelfSignUpConfigurations(tenantId);

        //test for super tenant
        APIUtil.loadTenantSelfSignUpConfigurations(org.wso2.carbon.utils.multitenancy.MultitenantConstants.SUPER_TENANT_ID);
        Assert.assertTrue(true);
    }

    @Test
    public void testLoadTenantConf() throws APIManagementException, RegistryException {

        APIUtil.loadTenantConf(tenantId);

        // test when fle is found in the path
        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        APIUtil.loadTenantConf(tenantId);

        Assert.assertTrue(true);
    }

    @Test
    public void testLoadTenantConfRegistryException() throws RegistryException, IOException {

        //test RegistryException
        Mockito.when(registry.newResource()).thenThrow(new RegistryException("")).thenReturn(resource);
        try {
            APIUtil.loadTenantConf(tenantId);
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().startsWith("Error while saving tenant conf to the registry"));
        }
    }

    @Test(expected = APIManagementException.class)
    public void testCreateSelfSignUpRolesExceptions() throws RegistryException, IOException, APIManagementException {

        //test when APIConstants.SELF_SIGN_UP_CONFIG_LOCATION does not exists in registry
        APIUtil.createSelfSignUpRoles(tenantId);

        //test when APIConstants.SELF_SIGN_UP_CONFIG_LOCATION exists in registry

        Mockito.when(registry.resourceExists(APIConstants.SELF_SIGN_UP_CONFIG_LOCATION)).thenReturn(true);
        Mockito.when(registry.get(APIConstants.SELF_SIGN_UP_CONFIG_LOCATION)).thenReturn(resource);
        InputStream content = Mockito.mock(InputStream.class);
        Mockito.when(resource.getContentStream()).thenReturn(content);

        APIUtil.createSelfSignUpRoles(tenantId);
    }

    @Test
    public void testIsSubscriberRoleCreationEnabled() throws APIManagementException, RegistryException, FileNotFoundException {

        String signupConfig = "{\n" +
                "  \"SelfSignUp\": {\n" +
                "    \"EnableSignup\": \"true\",\n" +
                "    \"SignUpDomain\": \"PRIMARY\",\n" +
                "    \"AdminUserName\": \"${admin.username}\",\n" +
                "    \"AdminPassword\": \"${admin.password}\",\n" +
                "    \"SignUpRoles\": {\n" +
                "      \"SignUpRole\": {\n" +
                "        \"RoleName\": \"subscriber\",\n" +
                "        \"IsExternalRole\": \"false\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Mockito.when(registryService.getConfigSystemRegistry(0)).thenReturn(registry);

        Mockito.when(registry.resourceExists("/apimgt/applicationdata/tenant-conf.json")).thenReturn(true);
        Mockito.when(registry.get("/apimgt/applicationdata/tenant-conf.json")).thenReturn(resource);
        Mockito.when(resource.getContent()).thenReturn(signupConfig.getBytes(Charset.forName("UTF-8")));
        //Sunscriber role is not enabled in tenant-conf.json
        Assert.assertFalse(APIUtil.isSubscriberRoleCreationEnabled(tenantId));

        //Sunscriber role is enabled in tenant-conf.json
        signupConfig = "{\n" +
                "  \"SelfSignUp\": {\n" +
                "    \"EnableSignup\": \"true\",\n" +
                "    \"SignUpDomain\": \"PRIMARY\",\n" +
                "    \"AdminUserName\": \"${admin.username}\",\n" +
                "    \"AdminPassword\": \"${admin.password}\",\n" +
                "    \"SignUpRoles\": {\n" +
                "      \"SignUpRole\": {\n" +
                "        \"RoleName\": \"subscriber\",\n" +
                "        \"IsExternalRole\": \"false\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                " \"DefaultRoles\": {\n" +
                "    \"SubscriberRole\": {\n" +
                "        \"RoleName\": \"subscriber\",\n" +
                "        \"IsExternalRole\": \"true\"\n" +
                "        \"CreateOnTenantLoad\": true\n" +
                "       }\n" +
                "    }\n" +
                "}";
        Mockito.when(resource.getContent()).thenReturn(signupConfig.getBytes(Charset.forName("UTF-8")));
        Assert.assertTrue(APIUtil.isSubscriberRoleCreationEnabled(tenantId));
    }

    @Test
    public void testGetMountedPath() {

        Mount mount = Mockito.mock(Mount.class);
        String path = "_system";
        String targetPath = "_system/apimgt";
        Mockito.when(mount.getPath()).thenReturn(path);
        Mockito.when(mount.getTargetPath()).thenReturn(targetPath);
        List<Mount> mounts = new ArrayList<Mount>(1);
        mounts.add(mount);
        Mockito.when(registryContext.getMounts()).thenReturn(mounts);
        Assert.assertEquals(targetPath, APIUtil.getMountedPath(registryContext, path));

    }

    @Test
    public void testGetDomainMappings() throws APIManagementException, RegistryException {

        String appType = "xyz";
        String customurl_abcdotcom = "{\n" +
                "  \"xyz\": {\n" +
                "    \"customUrlEnabled\": \"true\",\n" +
                "  }\n" +
                "}";
        Mockito.when(registry.resourceExists("/customurl/api-cloud/abc.com/urlMapping/abc.com")).thenReturn(false);

        //when resource does not exists in registry it wll return empty map
        Assert.assertEquals(0, APIUtil.getDomainMappings(tenantDomain, appType).size());

        //when resource exists in the regstry
        Mockito.when(registry.resourceExists("/customurl/api-cloud/abc.com/urlMapping/abc.com")).thenReturn(true);
        Mockito.when(registry.get("/customurl/api-cloud/abc.com/urlMapping/abc.com")).thenReturn(resource);

        Mockito.when(resource.getContent()).thenReturn(customurl_abcdotcom.getBytes(Charset.forName("UTF-8")));
        Assert.assertEquals(1, (APIUtil.getDomainMappings(tenantDomain, appType).size()));

    }

    @Test
    public void testGetDomainMappingsParserException() throws RegistryException {

        String appType = "xyz";
        Mockito.when(registry.resourceExists("/customurl/api-cloud/abc.com/urlMapping/abc.com")).thenReturn(true);
        Mockito.when(registry.get("/customurl/api-cloud/abc.com/urlMapping/abc.com")).thenReturn(resource);
        //test parser exception
        String customurl_abcdotcom = "\n" +
                "  \"xyz\": {\n" +
                "    \"customUrlEnabled\": \"true\",\n" +
                "  }\n" +
                "}";
        Mockito.when(resource.getContent()).thenReturn(customurl_abcdotcom.getBytes(Charset.forName("UTF-8")));

        try {
            APIUtil.getDomainMappings(tenantDomain, appType);
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().startsWith("Malformed JSON found in the gateway tenant domain mappings"));
        }
    }

    @Test
    public void testGetDomainMappingsClassCastException() throws RegistryException {

        String appType = "xyz";
        Mockito.when(registry.resourceExists("/customurl/api-cloud/abc.com/urlMapping/abc.com")).thenReturn(true);
        Mockito.when(registry.get("/customurl/api-cloud/abc.com/urlMapping/abc.com")).thenReturn(resource);
        //test parser exception
        String customurl_abcdotcom = "{\n" +
                "  \"xyz\": \"\n" +
                "\t\tabc\n" +
                "\t\"\n" +
                "}";
        Mockito.when(resource.getContent()).thenReturn(customurl_abcdotcom.getBytes(Charset.forName("UTF-8")));

        try {
            APIUtil.getDomainMappings(tenantDomain, appType);
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().startsWith("Invalid JSON found in the gateway tenant domain mappings"));
        }
    }

    @Test
    public void testGetDomainMappingsRegistryException() throws RegistryException {

        String appType = "xyz";
        Mockito.when(registry.resourceExists("/customurl/api-cloud/abc.com/urlMapping/abc.com")).thenReturn(true);
        Mockito.when(registry.get("/customurl/api-cloud/abc.com/urlMapping/abc.com")).thenThrow(new RegistryException(""));
        //test parser exception
        String customurl_abcdotcom = "{\n" +
                "  \"xyz\": {\n" +
                "    \"customUrlEnabled\": \"true\",\n" +
                "  }\n" +
                "}";
        Mockito.when(resource.getContent()).thenReturn(customurl_abcdotcom.getBytes(Charset.forName("UTF-8")));

        try {
            APIUtil.getDomainMappings(tenantDomain, appType);
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().startsWith("Error while retrieving gateway domain mappings from registry"));
        }
    }

    @Test
    public void testGetDocumentInvalidResourcePath() {
        String userName = "admin";
        String resourceUrl = "/_system/config/apimgt";
        try {
            APIUtil.getDocument(userName, resourceUrl, tenantDomain);
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().startsWith("Invalid resource Path"));
        }
    }

    @Test
    public void testGetDocumentResourceNotExists() throws RegistryException, APIManagementException {
        String userName = "admin";
        String resourceUrl = "/_system/governance/apimgt";
        Mockito.when(registryService.getGovernanceUserRegistry("admin", tenantId)).thenReturn(registry);
        Mockito.when(privilegedCarbonContext.getTenantId()).thenReturn(tenantId);

        APIUtil.getDocument(userName, resourceUrl, tenantDomain);
    }

    @Test
    public void testGetDocumentResourceExists() throws RegistryException, APIManagementException {
        String userName = "admin";
        String resourceUrl = "/_system/governance/apimgt";
        Mockito.when(registryService.getGovernanceUserRegistry("admin", tenantId)).thenReturn(registry);
        Mockito.when(privilegedCarbonContext.getTenantId()).thenReturn(tenantId);
        Mockito.when(registry.resourceExists("/apimgt")).thenReturn(true);
        Mockito.when(registry.get("/apimgt")).thenReturn(resource);
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(resource.getContentStream()).thenReturn(inputStream);
        Mockito.when(resource.getPath()).thenReturn("aaa/bbb");
        Mockito.when(resource.getMediaType()).thenReturn("application/json");

        Assert.assertEquals(3, APIUtil.getDocument(userName, resourceUrl, tenantDomain).size());
    }

    @Test
    public void testGetEnvironmentsOfAPI() {
        String userName = "admin";
        String resourceUrl = "admin";
        API api = Mockito.mock(API.class);
        Set<String> apiEnvs = new HashSet<String>();
        apiEnvs.add("prod");
        Mockito.when(api.getEnvironments()).thenReturn(apiEnvs);
        Assert.assertEquals(1, APIUtil.getEnvironmentsOfAPI(api).size());

        //

    }

    @Test
    public void testDoesApplicationExist() {
        String name = "abc";
        Application application = Mockito.mock(Application.class);
        Mockito.when(application.getName()).thenReturn(name);
        Application[] applications = new Application[]{application};

        Assert.assertTrue(APIUtil.doesApplicationExist(applications, name));

        //when application does not exist
        Assert.assertFalse(APIUtil.doesApplicationExist(applications, "xyz"));
    }

    @Test
    public void testGetGroupingExtractorImplementation() {
        String name = "abc";
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_STORE_GROUP_EXTRACTOR_IMPLEMENTATION))
                .thenReturn("");
        Assert.assertEquals("", APIUtil.getGroupingExtractorImplementation());
    }

    @Test
    public void testUpdatePermissionCache() throws UserStoreException {
        String useNname = "admin";
        APIUtil.updatePermissionCache(useNname);
    }

    @Test(expected = APIManagementException.class)
    public void testIsApplicationExistException() throws APIManagementException {
        String subscriber = "subscriber";
        String applicationName = "xyz";
        String groupID = "engineering";
        APIUtil.isApplicationExist(subscriber, applicationName, groupID);
    }

    @Test
    public void testIsApplicationExist() throws APIManagementException {
        String subscriber = "subscriber";
        String applicationName = "xyz";
        String groupID = "engineering";

        PowerMockito.mockStatic(ApiMgtDAO.class);
        apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        PowerMockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
        Assert.assertFalse(APIUtil.isApplicationExist("subscriber", "xyz", "engineering"));


        Mockito.when(apiMgtDAO.isApplicationExist(subscriber, applicationName, groupID)).thenReturn(true);

        APIUtil.isApplicationExist(subscriber, applicationName, groupID);
    }

    @Test
    public void testGetHostAddressLocal() throws APIManagementException {
        // when API_MANAGER_HOSTNAME property is not set local address has to be selected
        Assert.assertNotNull(APIUtil.getHostAddress());
    }

    //    @Test
    public void testGetHostAddress() throws APIManagementException {
        String hostAddress = "192.168.0.100:5002";
        Mockito.when(serverConfiguration.getFirstProperty(APIConstants.API_MANAGER_HOSTNAME)).thenReturn(hostAddress);
        // when API_MANAGER_HOSTNAME property is set
        Assert.assertEquals(hostAddress, APIUtil.getHostAddress());
    }

    @Test
    public void testIsStringArray() throws APIManagementException {
        String[] strArray = new String[]{"a", "b"};
        Assert.assertTrue(APIUtil.isStringArray(strArray));
    }

    @Test
    public void testAppendDomainWithUser() throws APIManagementException {
        String useNname = "admin";
        String useNnameWithDomain = "admin@" + tenantDomain;

        Assert.assertEquals(useNnameWithDomain, APIUtil.appendDomainWithUser(useNname, tenantDomain));

        //when email username enabled
        useNname = "isharac@wso2.com";
        Assert.assertEquals(useNname, APIUtil.appendDomainWithUser(useNname, tenantDomain));
    }

    @Test
    public void testConvertToString() throws APIManagementException {
        String jsonObject = "{\"abc\" : \"xyz\"}";
        Gson gson = new Gson();
        String actual = gson.toJson(jsonObject);
        Assert.assertEquals(actual, APIUtil.convertToString(jsonObject));
    }

    @Test
    public void testGetServerURL() throws APIManagementException {

        Mockito.when(serverConfiguration.getFirstProperty(APIConstants.PROXY_CONTEXT_PATH)).thenReturn("/abc");
        PowerMockito.when(CarbonUtils.getManagementTransport()).thenReturn("https");
        PowerMockito.when(CarbonUtils.getTransportProxyPort(axisConfiguration, "https")).thenReturn(-1);
        PowerMockito.when(CarbonUtils.getTransportProxyPort(axisConfiguration, "https")).thenReturn(9443);

        Assert.assertNotNull(APIUtil.getServerURL());

        Mockito.when(serverConfiguration.getFirstProperty(APIConstants.HOST_NAME)).thenReturn("192.168.0.100");
        Assert.assertEquals("https://192.168.0.100:9443/abc", APIUtil.getServerURL());

    }

    @Test
    public void testGetAPIProviderFromRESTAPI() {

        //apiversion does not contain "--" . Hence, returning null
        Assert.assertNull(APIUtil.getAPIProviderFromRESTAPI("1.0", ""));

        String apiVersion = "admin-AT-1.0--";
        Assert.assertEquals("admin@1.0@abc.com", APIUtil.getAPIProviderFromRESTAPI(apiVersion, tenantDomain));

    }

    @Test
    public void testIsAllowCredentials() {

        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.CORS_CONFIGURATION_ACCESS_CTL_ALLOW_CREDENTIALS))
                .thenReturn("true");
        Assert.assertTrue(APIUtil.isAllowCredentials());

    }

    @Test
    public void testIsCORSEnabled() {
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.CORS_CONFIGURATION_ENABLED))
                .thenReturn("true");
        Assert.assertTrue(APIUtil.isCORSEnabled());

    }

    @Test
    public void testGetAllAlertTypeByStakeHolder() throws APIManagementException {

        PowerMockito.mockStatic(ApiMgtDAO.class);
        apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        PowerMockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
        HashMap<Integer, String> map = new HashMap<Integer, String>(0);
        Mockito.when(apiMgtDAO.getAllAlertTypesByStakeHolder("")).thenReturn(map);
        Assert.assertEquals(0, APIUtil.getAllAlertTypeByStakeHolder("").size());

    }

    @Test
    public void testGetSavedAlertTypesIdsByUserNameAndStakeHolder() throws APIManagementException {

        PowerMockito.mockStatic(ApiMgtDAO.class);
        apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        PowerMockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
        List<Integer> list = new ArrayList<Integer>(0);
        Mockito.when(apiMgtDAO.getSavedAlertTypesIdsByUserNameAndStakeHolder("", "")).thenReturn(list);
        Assert.assertEquals(0, APIUtil.getSavedAlertTypesIdsByUserNameAndStakeHolder("", "")
                .size());

    }

    @Test
    public void testRetrieveSavedEmailList() throws APIManagementException {

        PowerMockito.mockStatic(ApiMgtDAO.class);
        apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        PowerMockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
        List<String> list = new ArrayList<String>(0);
        Mockito.when(apiMgtDAO.retrieveSavedEmailList("", "")).thenReturn(list);
        Assert.assertEquals(0, APIUtil.retrieveSavedEmailList("", "").size());

    }

    @Test
    public void testIsEnabledSubscriptionSpikeArrest() {

        ThrottleProperties throttleProperties = Mockito.mock(ThrottleProperties.class);
        Mockito.when(throttleProperties.isEnabledSubscriptionLevelSpikeArrest()).thenReturn(true);
        Mockito.when(apiManagerConfiguration.getThrottleProperties()).thenReturn(throttleProperties);
        Assert.assertTrue(APIUtil.isEnabledSubscriptionSpikeArrest());

    }

    @Test
    public void testGetFullLifeCycleData() throws XMLStreamException, RegistryException {

        Assert.assertNull(new APIUtil().getFullLifeCycleData(registry));

        Mockito.when(registry.resourceExists("/repository/components/org.wso2.carbon.governance/lifecycles/APILifeCycle"))
                .thenReturn(true);
        Mockito.when(registry.get("/repository/components/org.wso2.carbon.governance/lifecycles/APILifeCycle"))
                .thenReturn(resource);
        Assert.assertNull(new APIUtil().getFullLifeCycleData(registry));

    }

    @Test
    public void testIsStoreForumEnabled() {

        Assert.assertTrue(APIUtil.isStoreForumEnabled());

        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_STORE_FORUM_ENABLED)).thenReturn("false");
        Assert.assertFalse(APIUtil.isStoreForumEnabled());

    }

    @Test
    public void testLogAuditMessage() {
        APIUtil.logAuditMessage("entityType", "entityInfo", "action", "performedBy");
    }

    @Test
    public void testGetPortOffset() {
        //when port offset is not set
        Assert.assertEquals(0, APIUtil.getPortOffset());

        //test NumberFormatexception
        Mockito.when(serverConfiguration.getFirstProperty(APIConstants.PORT_OFFSET_CONFIG)).thenReturn("a");
        System.setProperty(APIConstants.PORT_OFFSET_SYSTEM_VAR, "a");
        Assert.assertEquals(0, APIUtil.getPortOffset());

        //when port offset is set to 1
        Mockito.when(serverConfiguration.getFirstProperty(APIConstants.PORT_OFFSET_CONFIG)).thenReturn("1");
        System.setProperty(APIConstants.PORT_OFFSET_SYSTEM_VAR, "1");

        Assert.assertEquals(1, APIUtil.getPortOffset());
    }

    @Test
    public void testIsQueryParamDataPublishingEnabled() {
        ThrottleProperties throttleProperties = Mockito.mock(ThrottleProperties.class);
        Mockito.when(throttleProperties.isEnableQueryParamConditions()).thenReturn(true);
        Mockito.when(apiManagerConfiguration.getThrottleProperties()).thenReturn(throttleProperties);

        Assert.assertTrue(APIUtil.isQueryParamDataPublishingEnabled());
    }

    @Test
    public void testIsHeaderDataPublishingEnabled() {
        ThrottleProperties throttleProperties = Mockito.mock(ThrottleProperties.class);
        Mockito.when(throttleProperties.isEnableHeaderConditions()).thenReturn(true);
        Mockito.when(apiManagerConfiguration.getThrottleProperties()).thenReturn(throttleProperties);

        Assert.assertTrue(APIUtil.isHeaderDataPublishingEnabled());
    }

    @Test
    public void testIsJwtTokenPublishingEnabled() {
        ThrottleProperties throttleProperties = Mockito.mock(ThrottleProperties.class);
        Mockito.when(throttleProperties.isEnableJwtConditions()).thenReturn(true);
        Mockito.when(apiManagerConfiguration.getThrottleProperties()).thenReturn(throttleProperties);

        Assert.assertTrue(APIUtil.isJwtTokenPublishingEnabled());
    }

    @Test
    public void testGetAnalyticsServerURL() {
        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration =
                Mockito.mock(APIManagerAnalyticsConfiguration.class);
        Mockito.when(apiMgtConfigurationService.getAPIAnalyticsConfiguration())
                .thenReturn(apiManagerAnalyticsConfiguration);
        Mockito.when(apiManagerAnalyticsConfiguration.getDasServerUrl())
                .thenReturn("das_server_url");
        Assert.assertEquals("das_server_url", APIUtil.getAnalyticsServerURL());
    }

    @Test
    public void testGetAnalyticsServerUserName() {
        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration =
                Mockito.mock(APIManagerAnalyticsConfiguration.class);
        Mockito.when(apiMgtConfigurationService.getAPIAnalyticsConfiguration())
                .thenReturn(apiManagerAnalyticsConfiguration);
        Mockito.when(apiManagerAnalyticsConfiguration.getDasReceiverServerUser())
                .thenReturn("das_receiver_usr");
        Assert.assertEquals("das_receiver_usr", APIUtil.getAnalyticsServerUserName());
    }

    @Test
    public void testGetAnalyticsServerPassword() {
        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration =
                Mockito.mock(APIManagerAnalyticsConfiguration.class);
        Mockito.when(apiMgtConfigurationService.getAPIAnalyticsConfiguration())
                .thenReturn(apiManagerAnalyticsConfiguration);
        Mockito.when(apiManagerAnalyticsConfiguration.getDasReceiverServerPassword())
                .thenReturn("das_receiver_pw");
        Assert.assertEquals("das_receiver_pw", APIUtil.getAnalyticsServerPassword());
    }

    @Test
    public void getCache() throws Exception {
        long defaultCacheTimeout = 54000;
        PowerMockito.mockStatic(Util.class);
        PowerMockito.mockStatic(CarbonContext.class);
        CarbonContext carbonContext = Mockito.mock(CarbonContext.class);
        PowerMockito.when(CarbonContext.getThreadLocalCarbonContext()).thenReturn(carbonContext);
        Mockito.when(carbonContext.getTenantDomain()).thenReturn(tenantDomain);
        PowerMockito.when(Util.getTenantDomain()).thenReturn(tenantDomain);
        Assert.assertEquals("name", APIUtil.getCache("managerName", "name", defaultCacheTimeout, defaultCacheTimeout).getName());
    }
}
