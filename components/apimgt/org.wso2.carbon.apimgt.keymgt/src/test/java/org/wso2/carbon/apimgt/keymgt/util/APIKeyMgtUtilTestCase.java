/*
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.apimgt.keymgt.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.ws.wssecurity.impl.AttributedStringImpl;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.impl.XSAnyImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dto.APIKeyValidationInfoDTO;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.keymgt.APIKeyMgtException;
import org.wso2.carbon.apimgt.keymgt.handlers.ResourceConstants;
import org.wso2.carbon.apimgt.keymgt.internal.ServiceReferenceHolder;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.security.AuthenticatorsConfiguration;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationRequestDTO;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;

import javax.cache.*;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(PowerMockRunner.class) @PrepareForTest({ Caching.class, APIKeyMgtDataHolder.class, IdentityDatabaseUtil.class,
        PrivilegedCarbonContext.class, APIUtil.class, ServiceReferenceHolder.class, AuthenticatorsConfiguration.class })
public class APIKeyMgtUtilTestCase {
    @Before
    public void init() {
        System.setProperty("carbon.home", "");
        PrivilegedCarbonContext carbonContext;
        carbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);

        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(carbonContext);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId()).thenReturn(-1234);
        PowerMockito.doNothing().when(carbonContext).setTenantDomain("carbon.super", true);

        APIIdentifier mockedIdentifier = new APIIdentifier("mockProviderName", "mockApiName",
                "mockVersion"); //PowerMockito.mock(APIIdentifier.class);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId()).thenReturn(-1234);
    }

    @Test
    public void testConstructParameterMap() throws Exception {

        OAuth2TokenValidationRequestDTO.TokenValidationContextParam param1 = new OAuth2TokenValidationRequestDTO()
                .new TokenValidationContextParam();
        param1.setKey("Key1");
        param1.setValue("Value1");
        OAuth2TokenValidationRequestDTO.TokenValidationContextParam param2 = new OAuth2TokenValidationRequestDTO()
                .new TokenValidationContextParam();
        param2.setKey("Key2");
        param2.setValue("Value2");
        OAuth2TokenValidationRequestDTO.TokenValidationContextParam[] params = {param1, param2};

        Map<String, String> paramMap = APIKeyMgtUtil.constructParameterMap(params);
        Assert.assertEquals(2, paramMap.size());
        Assert.assertEquals("Value1", paramMap.get("Key1"));
        Assert.assertEquals("Value2", paramMap.get("Key2"));
    }

    @Test
    public void testConstructParameterMapForNull() throws Exception {

        Map<String, String> paramMap = APIKeyMgtUtil.constructParameterMap(null);
        Assert.assertNull(paramMap);
    }

    @Test
    public void testGetTenantDomainFromTenantId() throws Exception {

        PowerMockito.mockStatic(APIKeyMgtDataHolder.class);
        RealmService realmService = Mockito.mock(RealmService.class);
        PowerMockito.when(APIKeyMgtDataHolder.getRealmService()).thenReturn(realmService);

        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(tenantManager.getDomain(Mockito.anyInt())).thenReturn("carbon.super");

        Assert.assertNotNull(APIKeyMgtUtil.getTenantDomainFromTenantId(-1234));
    }


    @Test(expected = APIKeyMgtException.class)
    public void testGetTenantDomainFromTenantIdForException() throws Exception {

        PowerMockito.mockStatic(APIKeyMgtDataHolder.class);
        RealmService realmService = Mockito.mock(RealmService.class);
        PowerMockito.when(APIKeyMgtDataHolder.getRealmService()).thenReturn(realmService);

        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.doThrow(UserStoreException.class).when(tenantManager).getDomain(Mockito.anyInt());

        APIKeyMgtUtil.getTenantDomainFromTenantId(-1234);
    }

    @Test
    public void testGetDBConnection() throws Exception {

        PowerMockito.mockStatic(IdentityDatabaseUtil.class);
        Connection connection = Mockito.mock(Connection.class);
        PowerMockito.when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        Assert.assertNotNull(APIKeyMgtUtil.getDBConnection());
    }

    @Test public void testGetAttributeSeparator() throws Exception {
        Assertion mockedAssertion = PowerMockito.mock(Assertion.class);
        System.setProperty("carbon.home", "");
        PrivilegedCarbonContext carbonContext;
        carbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);

        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(carbonContext);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId()).thenReturn(-1234);
        PowerMockito.doNothing().when(carbonContext).setTenantDomain("carbon.super", true);

        AttributeStatement mockAttributeStatement = PowerMockito.mock(AttributeStatement.class);
        List<AttributeStatement> attributeStatementList = Collections.singletonList(mockAttributeStatement);
        PowerMockito.when(mockedAssertion.getAttributeStatements()).thenReturn(attributeStatementList);

        Attribute mockAttribute = PowerMockito.mock(Attribute.class);
        List<Attribute> attributesList = Collections.singletonList(mockAttribute);
        PowerMockito.when(mockAttributeStatement.getAttributes()).thenReturn(attributesList);

        XMLObject rawAttribute = PowerMockito.mock(XMLObject.class);
        PowerMockito.when(rawAttribute.toString()).thenReturn("sampleRole");
        List<XMLObject> mockedAttributeValues = Collections.singletonList(rawAttribute);
        List<XMLObject> multiMockedAttributeValues = Arrays.asList(rawAttribute, PowerMockito.mock(XMLObject.class));
        PowerMockito.when(mockAttribute.getAttributeValues()).thenReturn(mockedAttributeValues, multiMockedAttributeValues);

        PowerMockito.when(mockAttribute.getName()).thenReturn("http://wso2.org/claims/role");

        String[] roles = APIKeyMgtUtil.getRolesFromAssertion(mockedAssertion);

        Assert.assertTrue(roles[0].equals("sampleRole"));
    }

    @Test
    public void testGetRolesFromAssertion() throws Exception {
        Assertion mockedAssertion = PowerMockito.mock(Assertion.class);
        System.setProperty("carbon.home", "");
        PrivilegedCarbonContext carbonContext;
        carbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);

        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(carbonContext);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId()).thenReturn(-1234);
        PowerMockito.doNothing().when(carbonContext).setTenantDomain("carbon.super", true);

        AttributeStatement mockAttributeStatement = PowerMockito.mock(AttributeStatement.class);
        List<AttributeStatement> attributeStatementList = Collections.singletonList(mockAttributeStatement);
        PowerMockito.when(mockedAssertion.getAttributeStatements()).thenReturn(attributeStatementList);

        Attribute mockAttribute = PowerMockito.mock(Attribute.class);
        List<Attribute> attributesList = Collections.singletonList(mockAttribute);
        PowerMockito.when(mockAttributeStatement.getAttributes()).thenReturn(attributesList);

        XMLObject rawAttribute = PowerMockito.mock(XMLObject.class);
        PowerMockito.when(rawAttribute.toString()).thenReturn("sampleRole");
        List<XMLObject> mockedAttributeValues = Collections.singletonList(rawAttribute);
        AttributedStringImpl mockedAttributedStringImpl = new AttributedStringImpl("nameSpaceURI", "elementLocalName",
                "namespacePrefix");
        String sampleAttrValue = "MockedAuthParamSampleAttribute";
        mockedAttributedStringImpl.setValue(sampleAttrValue);
        List<XMLObject> mockedXSSAttributeValues = Collections.singletonList((XMLObject) mockedAttributedStringImpl);
        XSAnyImpl mockedXSAnyImpl = Mockito.mock(XSAnyImpl.class);
        PowerMockito.when(mockedXSAnyImpl.getTextContent()).thenReturn(sampleAttrValue);
        List<XMLObject> mockedXSAnyImplAttributeValues = Collections.singletonList((XMLObject) mockedXSAnyImpl);
        List<XMLObject> multiMockedAttributeValues = Arrays.asList(rawAttribute, PowerMockito.mock(XMLObject.class));
        AuthenticatorsConfiguration.AuthenticatorConfig mockedAuthenticatorConfig = Mockito
                .mock(AuthenticatorsConfiguration.AuthenticatorConfig.class);
        PowerMockito.when(mockAttribute.getAttributeValues())
                .thenReturn(mockedAttributeValues, multiMockedAttributeValues, mockedXSSAttributeValues,
                        mockedXSAnyImplAttributeValues);

        PowerMockito.mockStatic(AuthenticatorsConfiguration.class);
        AuthenticatorsConfiguration mockedAuthenticatorsConfiguration = PowerMockito
                .mock(AuthenticatorsConfiguration.class);
        PowerMockito.when(AuthenticatorsConfiguration.getInstance()).thenReturn(mockedAuthenticatorsConfiguration);
        Map<String, String> mockedConfigParameters = new HashMap<String, String>();
        mockedConfigParameters.put(ResourceConstants.ATTRIBUTE_VALUE_SEPARATOR, "MockedAuthParam");
        PowerMockito.when(mockedAuthenticatorConfig.getParameters()).thenReturn(mockedConfigParameters);
        PowerMockito.when(mockedAuthenticatorsConfiguration
                .getAuthenticatorConfig(ResourceConstants.SAML2_SSO_AUTHENTICATOR_NAME))
                .thenReturn(mockedAuthenticatorConfig);

        PowerMockito.when(mockAttribute.getName()).thenReturn("http://wso2.org/claims/role");

        String[] roles = APIKeyMgtUtil.getRolesFromAssertion(mockedAssertion);
        String[] multiRoles = APIKeyMgtUtil.getRolesFromAssertion(mockedAssertion);
        String[] rolesXSS = APIKeyMgtUtil.getRolesFromAssertion(mockedAssertion);
        String[] rolesXSAnyImpl = APIKeyMgtUtil.getRolesFromAssertion(mockedAssertion);

        Assert.assertTrue(roles[0].equals("sampleRole"));
        Assert.assertTrue(rolesXSS[1].equals("SampleAttribute"));
        Assert.assertTrue(rolesXSAnyImpl[1].equals("SampleAttribute"));
        Assert.assertTrue(multiRoles.length == 2);
    }

    @Test
    public void testGetAPIWhenArtifactIdNull() throws Exception {
        APIIdentifier mockedIdentifier = new APIIdentifier("mockProviderName", "mockApiName", "mockVersion");
        PowerMockito.mockStatic(APIKeyMgtDataHolder.class);
        PowerMockito.mockStatic(APIUtil.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry mockedUserRegistry = Mockito.mock(UserRegistry.class);

        PowerMockito.when(APIKeyMgtDataHolder.getRegistryService()).thenReturn(registryService);
        PowerMockito.when(mockedUserRegistry.get(Mockito.anyString())).thenReturn(PowerMockito.mock(Resource.class));
        PowerMockito.when(registryService.getGovernanceSystemRegistry()).thenReturn(mockedUserRegistry);
        PowerMockito.when(APIUtil.getArtifactManager(mockedUserRegistry, APIConstants.API_KEY))
                .thenReturn(Mockito.mock(GenericArtifactManager.class));
        try {
            APIKeyMgtUtil.getAPI(mockedIdentifier);
            Assert.fail("APIManagementException not thrown when artifactId is null");
        } catch (APIManagementException e) {
            Assert.assertTrue(e.getMessage().contains("artifact id is null for "));
        }
    }

    @Test
    public void testGetAPIWhenRegistryGetFails() throws Exception {
        APIIdentifier mockedIdentifier = new APIIdentifier("mockProviderName", "mockApiName", "mockVersion");
        PowerMockito.mockStatic(APIKeyMgtDataHolder.class);
        PowerMockito.mockStatic(APIUtil.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry mockedUserRegistry = Mockito.mock(UserRegistry.class);

        PowerMockito.when(APIKeyMgtDataHolder.getRegistryService()).thenReturn(registryService);
        PowerMockito.when(mockedUserRegistry.get(Mockito.anyString())).thenThrow(RegistryException.class);
        PowerMockito.when(registryService.getGovernanceSystemRegistry()).thenReturn(mockedUserRegistry);
        PowerMockito.when(APIUtil.getArtifactManager(mockedUserRegistry, APIConstants.API_KEY))
                .thenReturn(Mockito.mock(GenericArtifactManager.class));

        API nullApi = APIKeyMgtUtil.getAPI(mockedIdentifier);
        Assert.assertNull(nullApi);
    }

    @Test
    public void testGetAPI() throws Exception {
        APIIdentifier mockedIdentifier = new APIIdentifier("mockProviderName", "mockApiName",
                "mockVersion");
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId()).thenReturn(-1234);

        PowerMockito.mockStatic(APIKeyMgtDataHolder.class);
        PowerMockito.mockStatic(APIUtil.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry mockedUserRegistry = Mockito.mock(UserRegistry.class);
        GenericArtifact mockedGenericArtifact = Mockito.mock(GenericArtifact.class);
        PowerMockito.when(APIKeyMgtDataHolder.getRegistryService()).thenReturn(registryService);
        Resource mockedResource = PowerMockito.mock(Resource.class);
        PowerMockito.when(mockedUserRegistry.get(Mockito.anyString())).thenReturn(mockedResource);
        PowerMockito.when(registryService.getGovernanceSystemRegistry()).thenReturn(mockedUserRegistry);
        PowerMockito.when(mockedResource.getUUID()).thenReturn(UUID.randomUUID().toString());
        GenericArtifactManager mockedGenericArtifactManager = Mockito.mock(GenericArtifactManager.class);
        PowerMockito.when(APIUtil.getArtifactManager(mockedUserRegistry, APIConstants.API_KEY))
                .thenReturn(mockedGenericArtifactManager);
        PowerMockito.when(mockedGenericArtifactManager.getGenericArtifact(Mockito.anyString()))
                .thenReturn(mockedGenericArtifact);
        PowerMockito.when(APIUtil.getAPI(mockedGenericArtifact, mockedUserRegistry))
                .thenReturn(Mockito.mock(API.class));

        API returnedApi = APIKeyMgtUtil.getAPI(mockedIdentifier);
        Assert.assertTrue(returnedApi instanceof API);
    }

    @Test
    public void testGetFromKeyManagerCache() {
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        APIManagerConfigurationService amConfigService = Mockito.mock(APIManagerConfigurationService.class);
        APIManagerConfiguration amConfig = Mockito.mock(APIManagerConfiguration.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);

        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService()).thenReturn(amConfigService);
        Mockito.when(amConfigService.getAPIManagerConfiguration()).thenReturn(amConfig);
        String cacheExpTime = "9000";
        String sampleCacheKey = UUID.randomUUID().toString();
        PowerMockito.when(amConfig.getFirstProperty(APIConstants.TOKEN_CACHE_EXPIRY)).thenReturn(cacheExpTime,null);
        Cache mockedCache = PowerMockito.mock(Cache.class);
        PowerMockito.mockStatic(Caching.class);
        CacheManager mockedCacheManager = PowerMockito.mock(CacheManager.class);
        Mockito.when(Caching.getCacheManager(APIConstants.API_MANAGER_CACHE_MANAGER)).thenReturn(mockedCacheManager);
        CacheBuilder mockedCacheBuilder = PowerMockito.mock(CacheBuilder.class);
        Mockito.when(mockedCacheManager.createCacheBuilder(APIConstants.KEY_CACHE_NAME)).thenReturn(mockedCacheBuilder);
        Mockito.when(mockedCacheManager.getCache(APIConstants.KEY_CACHE_NAME)).thenReturn(mockedCache);
        Mockito.when(mockedCacheBuilder.build()).thenReturn(mockedCache);
        Mockito.when(mockedCacheBuilder.setStoreByValue(Mockito.anyBoolean())).thenReturn(mockedCacheBuilder);
        PowerMockito.when(mockedCache.get(sampleCacheKey)).thenReturn(Mockito.mock(APIKeyValidationInfoDTO.class));
        Mockito.when(mockedCacheBuilder.setExpiry(CacheConfiguration.ExpiryType.MODIFIED,
                new CacheConfiguration.Duration(TimeUnit.SECONDS, Long.parseLong(cacheExpTime))))
                .thenReturn(mockedCacheBuilder);
        Mockito.when(mockedCacheBuilder.setExpiry(CacheConfiguration.ExpiryType.ACCESSED,
                new CacheConfiguration.Duration(TimeUnit.SECONDS, Long.parseLong(cacheExpTime))))
                .thenReturn(mockedCacheBuilder);

        APIKeyValidationInfoDTO cacheInfo = APIKeyMgtUtil.getFromKeyManagerCache(sampleCacheKey);
        APIKeyValidationInfoDTO cacheInfoWithoutKeyCacheInistialized = APIKeyMgtUtil.getFromKeyManagerCache(sampleCacheKey);
        Assert.assertNotNull(cacheInfo);
    }
}