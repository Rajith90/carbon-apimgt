package org.wso2.carbon.apimgt.gateway.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.gateway.handlers.security.APISecurityException;
import org.wso2.carbon.apimgt.gateway.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerAnalyticsConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.keymgt.stub.validator.APIKeyValidationServiceStub;
import org.wso2.carbon.apimgt.usage.publisher.DataPublisherUtil;
import org.wso2.carbon.apimgt.usage.publisher.internal.UsageComponent;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.cache.Cache;
import javax.cache.CacheBuilder;
import javax.cache.CacheConfiguration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.fail;

/**
 * Test class for WebsocketInboundHandler
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WebsocketInboundHandlerTestCase.class, MultitenantUtils.class, DataPublisherUtil.class,
        UsageComponent.class, PrivilegedCarbonContext.class, ServiceReferenceHolder.class, Caching.class})
public class WebsocketInboundHandlerTestCase {
    private String TENANT_URL = "https://localhost/t/abc.com/1.0";
    private String SUPER_TENANT_URL = "https://localhost/abc/1.0";
    private String TENANT_DOMAIN = "abc.com";
    private String AUTHORIZATION = "Authorization: 587hfbt4i8ydno87ywq";
    private String USER_AGENT = "Mozilla";
    private String TOKEN_CACHE_EXPIRY = "900";
    private String API_KEY_VALIDATOR_URL = "https://localhost:9000/";
    private String API_KEY_VALIDATOR_USERNAME = "IsharaC";
    private String API_KEY_VALIDATOR_PASSWORD = "abc123";
    private String GATEWAY_TOKEN_CACHE_ENABLED = "true";


    @Before
    public void setup() {
        System.setProperty("carbon.home", "jhkjn");
    }

    @Test
    public void testChannelReadForTenant() throws AxisFault {
        //test when the request is a handshake
        WebsocketInboundHandler websocketInboundHandler = new WebsocketInboundHandler();
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        FullHttpRequest fullHttpRequest = Mockito.mock(FullHttpRequest.class);
        try {
            websocketInboundHandler.channelRead(channelHandlerContext, fullHttpRequest);
        } catch (Exception e) {
//            test for exception
        }

//        try {
        PowerMockito.mockStatic(MultitenantUtils.class);
        PowerMockito.mockStatic(DataPublisherUtil.class);
        PowerMockito.mockStatic(UsageComponent.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.mockStatic(Caching.class);
        APIManagerConfigurationService apiManagerConfigurationService = Mockito.mock(APIManagerConfigurationService.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration =
                Mockito.mock(APIManagerAnalyticsConfiguration.class);
        Cache gatewayCache = Mockito.mock(Cache.class);
        CacheManager cacheManager = Mockito.mock(CacheManager.class);
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        PrivilegedCarbonContext privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        CacheBuilder cacheBuilder = Mockito.mock(CacheBuilder.class);
        PowerMockito.when(MultitenantUtils.getTenantDomainFromUrl(TENANT_URL)).thenReturn(TENANT_DOMAIN);
        PowerMockito.when(UsageComponent.getAmConfigService()).thenReturn(apiManagerConfigurationService);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        PowerMockito.when(Caching.getCacheManager(APIConstants.API_MANAGER_CACHE_MANAGER)).thenReturn(cacheManager);
        PowerMockito.when(cacheManager.createCacheBuilder(APIConstants.GATEWAY_KEY_CACHE_NAME)).thenReturn(cacheBuilder);
        Mockito.when(fullHttpRequest.getUri()).thenReturn(TENANT_URL);
        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);
        Mockito.when(headers.get(org.apache.http.HttpHeaders.AUTHORIZATION)).thenReturn(AUTHORIZATION);
        Mockito.when(headers.get(org.apache.http.HttpHeaders.USER_AGENT)).thenReturn(USER_AGENT);
        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);
        Mockito.when(apiManagerConfigurationService.getAPIAnalyticsConfiguration()).thenReturn(apiManagerAnalyticsConfiguration);
        PowerMockito.when(serviceReferenceHolder.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
        WebsocketInboundHandler websocketInboundHandler1 = new WebsocketInboundHandler();
        CacheConfiguration.Duration duration = new CacheConfiguration.Duration(TimeUnit.SECONDS,
                Long.parseLong(TOKEN_CACHE_EXPIRY));
        Mockito.when(cacheBuilder.setExpiry(CacheConfiguration.ExpiryType.MODIFIED, duration)).thenReturn(cacheBuilder);
        Mockito.when(cacheBuilder.setExpiry(CacheConfiguration.ExpiryType.ACCESSED, duration)).thenReturn(cacheBuilder);
        Mockito.when(cacheBuilder.setStoreByValue(false)).thenReturn(cacheBuilder);
        Mockito.when(cacheBuilder.build()).thenReturn(gatewayCache);
        //test for Invalid Credentials error
        try {
            websocketInboundHandler1.channelRead(channelHandlerContext, fullHttpRequest);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof APISecurityException) {
                Assert.assertTrue(e.getMessage().startsWith("Invalid Credentials"));
            } else {
                fail(e.getMessage());
            }
        }

        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_URL)).
                thenReturn(API_KEY_VALIDATOR_URL);
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_USERNAME)).
                thenReturn(API_KEY_VALIDATOR_USERNAME);
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_PASSWORD)).
                thenReturn(API_KEY_VALIDATOR_PASSWORD);
        Mockito.when(headers.get(org.apache.http.HttpHeaders.AUTHORIZATION)).thenReturn("Bearer 587hfbt4i8ydno87ywq");
        //test when CONSUMER_KEY_SEGMENT is not present
        Mockito.when(headers.contains(org.apache.http.HttpHeaders.AUTHORIZATION)).thenReturn(true);

        try {
            websocketInboundHandler1.channelRead(channelHandlerContext, fullHttpRequest);
        } catch (Exception e) {
            if (e instanceof APISecurityException) {
                e.printStackTrace();
                Assert.assertTrue(e.getMessage().startsWith("Error while accessing backend services for API key " +
                        "validation"));
            } else {
                fail(e.getMessage());
            }
        }

        ConfigurationContext ctx = ConfigurationContextFactory
                .createConfigurationContextFromFileSystem(null, null);
        APIKeyValidationServiceStub apiKeyValidationServiceStub = PowerMockito.mock(APIKeyValidationServiceStub.class);
        try {
            PowerMockito.whenNew(APIKeyValidationServiceStub.class).withArguments(ctx, API_KEY_VALIDATOR_URL +
                    "APIKeyValidationService").thenReturn(apiKeyValidationServiceStub);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testDoThrottle() {
        //todo
    }
}
