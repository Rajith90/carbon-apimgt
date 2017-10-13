package org.wso2.carbon.apimgt.gateway.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.Application;
import org.wso2.carbon.apimgt.api.model.Subscriber;
import org.wso2.carbon.apimgt.gateway.handlers.security.APISecurityException;
import org.wso2.carbon.apimgt.gateway.handlers.security.APISecurityUtils;
import org.wso2.carbon.apimgt.gateway.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.gateway.throttling.publisher.ThrottleDataPublisher;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerAnalyticsConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dto.APIKeyValidationInfoDTO;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.keymgt.stub.validator.APIKeyValidationServiceStub;
import org.wso2.carbon.apimgt.usage.publisher.APIMgtUsageDataBridgeDataPublisher;
import org.wso2.carbon.apimgt.usage.publisher.DataPublisherUtil;
import org.wso2.carbon.apimgt.usage.publisher.internal.UsageComponent;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.databridge.agent.DataPublisher;
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
        UsageComponent.class, PrivilegedCarbonContext.class, ServiceReferenceHolder.class, Caching.class,
        APISecurityUtils.class, WebsocketUtil.class, ThrottleDataPublisher.class, APIUtil.class})
@PowerMockIgnore("javax.net.ssl.SSLContext")
public class WebsocketInboundHandlerTestCase {
    private String TENANT_URL = "https://localhost/t/abc.com/1.0";
    private String SUPER_TENANT_URL = "https://localhost/abc/1.0";
    private String TENANT_DOMAIN = "abc.com";
    private String SUPER_TENANT_DOMAIN = "carbon.super";
    private String AUTHORIZATION = "Authorization: 587hfbt4i8ydno87ywq";
    private String USER_AGENT = "Mozilla";
    private String TOKEN_CACHE_EXPIRY = "900";
    private String API_KEY_VALIDATOR_URL = "https://localhost:9000/";
    private String API_KEY_VALIDATOR_USERNAME = "IsharaC";
    private String API_KEY_VALIDATOR_PASSWORD = "abc123";
    private String GATEWAY_TOKEN_CACHE_ENABLED = "true";
    private String API_KEY = "587hfbt4i8ydno87ywq";
    private ChannelHandlerContext channelHandlerContext;
    private FullHttpRequest fullHttpRequest;
    private APIManagerConfiguration apiManagerConfiguration;
    private HttpHeaders headers;
    private Cache gatewayCache;

    @Before
    public void setup() {
        System.setProperty("carbon.home", "jhkjn");

        channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        fullHttpRequest = Mockito.mock(FullHttpRequest.class);
        PowerMockito.mockStatic(MultitenantUtils.class);
        PowerMockito.mockStatic(DataPublisherUtil.class);
        PowerMockito.mockStatic(UsageComponent.class);
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.mockStatic(Caching.class);
        Mockito.mock(APIMgtUsageDataBridgeDataPublisher.class);
        APIManagerConfigurationService apiManagerConfigurationService = Mockito.mock(APIManagerConfigurationService.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration =
                Mockito.mock(APIManagerAnalyticsConfiguration.class);
        gatewayCache = Mockito.mock(Cache.class);
        CacheManager cacheManager = Mockito.mock(CacheManager.class);
        headers = Mockito.mock(HttpHeaders.class);
        PrivilegedCarbonContext privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        CacheBuilder cacheBuilder = Mockito.mock(CacheBuilder.class);
        PowerMockito.when(UsageComponent.getAmConfigService()).thenReturn(apiManagerConfigurationService);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        PowerMockito.when(Caching.getCacheManager(APIConstants.API_MANAGER_CACHE_MANAGER)).thenReturn(cacheManager);
        PowerMockito.when(cacheManager.createCacheBuilder(APIConstants.GATEWAY_KEY_CACHE_NAME)).thenReturn(cacheBuilder);
        APIMgtUsageDataBridgeDataPublisher apiMgtUsageDataBridgeDataPublisher = Mockito.mock(APIMgtUsageDataBridgeDataPublisher.class);
        Mockito.when(fullHttpRequest.getUri()).thenReturn(TENANT_URL);
        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);
        Mockito.when(headers.get(org.apache.http.HttpHeaders.AUTHORIZATION)).thenReturn(AUTHORIZATION);
        Mockito.when(headers.get(org.apache.http.HttpHeaders.USER_AGENT)).thenReturn(USER_AGENT);
        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);
        Mockito.when(apiManagerConfigurationService.getAPIAnalyticsConfiguration()).thenReturn(apiManagerAnalyticsConfiguration);
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.TOKEN_CACHE_EXPIRY)).thenReturn("900");
        PowerMockito.when(serviceReferenceHolder.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
        CacheConfiguration.Duration duration = new CacheConfiguration.Duration(TimeUnit.SECONDS,
                Long.parseLong(TOKEN_CACHE_EXPIRY));
        Mockito.when(gatewayCache.get(API_KEY)).thenReturn("fhgvjhhhjkghj");
        Mockito.when(gatewayCache.get("587hfbt4i8ydno87ywq:https://localhost/t/abc.com/1.0")).thenReturn(null);
        Mockito.when(cacheManager.getCache(APIConstants.GATEWAY_TOKEN_CACHE_NAME)).thenReturn(gatewayCache);
        Mockito.when(cacheBuilder.setExpiry(CacheConfiguration.ExpiryType.MODIFIED, duration)).thenReturn(cacheBuilder);
        Mockito.when(cacheBuilder.setExpiry(CacheConfiguration.ExpiryType.ACCESSED, duration)).thenReturn(cacheBuilder);
        Mockito.when(cacheBuilder.setStoreByValue(false)).thenReturn(cacheBuilder);
        Mockito.when(cacheBuilder.build()).thenReturn(gatewayCache);
        PowerMockito.doNothing().when(apiMgtUsageDataBridgeDataPublisher).init();
        PowerMockito.mockStatic(ThrottleDataPublisher.class);
        DataPublisher dataPublisher = Mockito.mock(DataPublisher.class);
        PowerMockito.when(ThrottleDataPublisher.getDataPublisher()).thenReturn(dataPublisher);


    }

    /*
    * Tests channelRead method for tenant when msg is FullHttpRequest
    * */
    @Test
    public void testChannelRead() throws AxisFault {
        //test when the request is a handshake
        WebsocketInboundHandler websocketInboundHandler = new WebsocketInboundHandler();
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        FullHttpRequest fullHttpRequest = Mockito.mock(FullHttpRequest.class);
        try {
            websocketInboundHandler.channelRead(channelHandlerContext, fullHttpRequest);
            fail("Expected exception is not thrown. Hence test fails.");
        } catch (Exception e) {
//            test for exception
        }
        Mockito.when(fullHttpRequest.getUri()).thenReturn(TENANT_URL);
        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);
        Mockito.when(headers.get(org.apache.http.HttpHeaders.AUTHORIZATION)).thenReturn(AUTHORIZATION);
        Mockito.when(headers.get(org.apache.http.HttpHeaders.USER_AGENT)).thenReturn(USER_AGENT);
        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);
        WebsocketInboundHandler websocketInboundHandler1 = new WebsocketInboundHandler();
        CacheConfiguration.Duration duration = new CacheConfiguration.Duration(TimeUnit.SECONDS,
                Long.parseLong(TOKEN_CACHE_EXPIRY));
        Mockito.when(gatewayCache.get(API_KEY)).thenReturn("fhgvjhhhjkghj");
        Mockito.when(gatewayCache.get("587hfbt4i8ydno87ywq:https://localhost/t/abc.com/1.0")).thenReturn(null);
        PowerMockito.when(MultitenantUtils.getTenantDomainFromUrl(TENANT_URL)).thenReturn(TENANT_DOMAIN);

        //test for Invalid Credentials error
        try {
            websocketInboundHandler1.channelRead(channelHandlerContext, fullHttpRequest);
            fail("Expected APISecurityException is not thrown (Invalid Credentials)");
        } catch (Exception e) {
            if (e instanceof APISecurityException) {
                Assert.assertTrue(e.getMessage().startsWith("Invalid Credentials"));
            } else {
                e.printStackTrace();
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
            fail("Expected APISecurityException is not thrown(Error while accessing backend services for API key " +
                    "validation");
        } catch (Exception e) {
            if (e instanceof APISecurityException) {
                Assert.assertTrue(e.getMessage().startsWith("Error while accessing backend services for API key " +
                        "validation"));
            } else {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.GATEWAY_TOKEN_CACHE_ENABLED)).
                thenReturn(GATEWAY_TOKEN_CACHE_ENABLED);

        // Test when api key validation client type is invalid it should throw Invalid Credentials Exception
        APIKeyValidationInfoDTO apiKeyValidationInfoDTO = Mockito.mock(APIKeyValidationInfoDTO.class);
        Mockito.when(gatewayCache.get("587hfbt4i8ydno87ywq:https://localhost/t/abc.com/1.0")).thenReturn(apiKeyValidationInfoDTO);
        PowerMockito.mockStatic(APISecurityUtils.class);
        PowerMockito.when(APISecurityUtils.getKeyValidatorClientType()).thenReturn("invalid");
        ConfigurationContext ctx = ConfigurationContextFactory
                .createConfigurationContextFromFileSystem(null, null);
        APIKeyValidationServiceStub apiKeyValidationServiceStub = Mockito.mock(APIKeyValidationServiceStub.class);
        WebsocketWSClient websocketWSClient = Mockito.mock(WebsocketWSClient.class);
        try {
            PowerMockito.whenNew(APIKeyValidationServiceStub.class).withArguments(ctx, API_KEY_VALIDATOR_URL +
                    "APIKeyValidationService").thenReturn(apiKeyValidationServiceStub);
            PowerMockito.when(websocketWSClient.getAPIKeyData(TENANT_URL, "1.0", "587hfbt4i8ydno87ywq"))
                    .thenReturn(apiKeyValidationInfoDTO);
            PowerMockito.whenNew(WebsocketWSClient.class).withNoArguments().thenReturn(websocketWSClient);
            websocketInboundHandler1.channelRead(channelHandlerContext, fullHttpRequest);
            fail("Expected APISecurityException is not thrown (Invalid Credentials) when KeyValidatorClientType is provided.");

        } catch (Exception e) {
            if (e instanceof APISecurityException) {
                Assert.assertTrue(e.getMessage().startsWith("Invalid Credentials"));
            } else {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

    }

    /*
   * Tests channelRead method for tenant when msg is WebSocketFrame
   * */
    @Test
    public void testChannelRead1() throws AxisFault {
        //test when the request is a handshake
        WebsocketInboundHandler websocketInboundHandler = new WebsocketInboundHandler() {
            @Override
            protected String getRemoteIP(ChannelHandlerContext ctx) {
                return "192.168.0.100";
            }

            @Override
            protected Application getApplicationById(APIKeyValidationInfoDTO infoDTO) throws APIManagementException {
                Application application = Mockito.mock(Application.class);
                Subscriber subscriber = Mockito.mock(Subscriber.class);
                Mockito.when(application.getSubscriber()).thenReturn(subscriber);
                Mockito.when(subscriber.getName()).thenReturn("Ishara");
                return application;
            }
        };
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        WebSocketFrame webSocketFrame = Mockito.mock(WebSocketFrame.class);
        CacheConfiguration.Duration duration = new CacheConfiguration.Duration(TimeUnit.SECONDS,
                Long.parseLong(TOKEN_CACHE_EXPIRY));
        Mockito.when(gatewayCache.get(API_KEY)).thenReturn("fhgvjhhhjkghj");
        Mockito.when(gatewayCache.get("587hfbt4i8ydno87ywq:https://localhost/t/abc.com/1.0")).thenReturn(null);

        ByteBuf content = Mockito.mock(ByteBuf.class);
        Mockito.when(webSocketFrame.content()).thenReturn(content);
        PowerMockito.mockStatic(WebsocketUtil.class);
        PowerMockito.mockStatic(APIUtil.class);
        PowerMockito.when(APIUtil.isAnalyticsEnabled()).thenReturn(true);
        //test for Invalid Credentials error
        try {
            websocketInboundHandler.channelRead(channelHandlerContext, webSocketFrame);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /*
    * Tests channelRead method for super tenant
    * */
    @Test
    public void testChannelReadForSuperTenant() throws AxisFault {
        //test when the request is a handshake
        WebsocketInboundHandler websocketInboundHandler = new WebsocketInboundHandler();
        PowerMockito.when(MultitenantUtils.getTenantDomainFromUrl(SUPER_TENANT_URL)).thenReturn(SUPER_TENANT_DOMAIN);

        try {
            websocketInboundHandler.channelRead(channelHandlerContext, fullHttpRequest);
            fail("Expected exception is not thrown. Hence test fails.");
        } catch (Exception e) {
//            test for exception
        }

        WebsocketInboundHandler websocketInboundHandler1 = new WebsocketInboundHandler();
        //test for Invalid Credentials error
        try {
            websocketInboundHandler1.channelRead(channelHandlerContext, fullHttpRequest);
            fail("Expected APISecurityException is not thrown (Invalid Credentials)");
        } catch (Exception e) {
            if (e instanceof APISecurityException) {
                Assert.assertTrue(e.getMessage().startsWith("Invalid Credentials"));
            } else {
                e.printStackTrace();
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
            fail("Expected APISecurityException is not thrown(Error while accessing backend services for API key " +
                    "validation");
        } catch (Exception e) {
            if (e instanceof APISecurityException) {
                Assert.assertTrue(e.getMessage().startsWith("Error while accessing backend services for API key " +
                        "validation"));
            } else {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.GATEWAY_TOKEN_CACHE_ENABLED)).
                thenReturn(GATEWAY_TOKEN_CACHE_ENABLED);
        // Test when api key validation client type is invalid it should throw Invalid Credentials Exception
        APIKeyValidationInfoDTO apiKeyValidationInfoDTO = Mockito.mock(APIKeyValidationInfoDTO.class);
        Mockito.when(gatewayCache.get("587hfbt4i8ydno87ywq:https://localhost/t/abc.com/1.0")).thenReturn(apiKeyValidationInfoDTO);
        PowerMockito.mockStatic(APISecurityUtils.class);
        PowerMockito.when(APISecurityUtils.getKeyValidatorClientType()).thenReturn("invalid");
        ConfigurationContext ctx = ConfigurationContextFactory
                .createConfigurationContextFromFileSystem(null, null);
        APIKeyValidationServiceStub apiKeyValidationServiceStub = Mockito.mock(APIKeyValidationServiceStub.class);
        WebsocketWSClient websocketWSClient = Mockito.mock(WebsocketWSClient.class);
        try {
            PowerMockito.whenNew(APIKeyValidationServiceStub.class).withArguments(ctx, API_KEY_VALIDATOR_URL +
                    "APIKeyValidationService").thenReturn(apiKeyValidationServiceStub);
            PowerMockito.when(websocketWSClient.getAPIKeyData(SUPER_TENANT_URL, "1.0", "587hfbt4i8ydno87ywq"))
                    .thenReturn(apiKeyValidationInfoDTO);
            PowerMockito.whenNew(WebsocketWSClient.class).withNoArguments().thenReturn(websocketWSClient);
            websocketInboundHandler1.channelRead(channelHandlerContext, fullHttpRequest);
            fail("Expected APISecurityException is not thrown (Invalid Credentials) when KeyValidatorClientType is provided.");

        } catch (Exception e) {
            if (e instanceof APISecurityException) {
                Assert.assertTrue(e.getMessage().startsWith("Invalid Credentials"));
            } else {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

    }

    /*
    *  Test for doThrottle() happy path
    *
    * */
    @Test
    public void testDoThrottle() {
        //todo
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        WebSocketFrame webSocketFrame = Mockito.mock(WebSocketFrame.class);
        WebsocketInboundHandler websocketInboundHandler = new WebsocketInboundHandler() {
            @Override
            protected String getRemoteIP(ChannelHandlerContext ctx) {
                return "192.168.0.100";
            }
        };
        ByteBuf content = Mockito.mock(ByteBuf.class);
        Mockito.when(webSocketFrame.content()).thenReturn(content);
        PowerMockito.mockStatic(WebsocketUtil.class);

        try {
            websocketInboundHandler.doThrottle(channelHandlerContext, webSocketFrame);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown while exceuting doThrottle()");
        }
    }


    /*
    *  Test for NumberFormatException throws when remoteIP is mis formatted
    *
    * */
    @Test
    public void testDoThrottle1() {
        //todo
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        WebSocketFrame webSocketFrame = Mockito.mock(WebSocketFrame.class);
        WebsocketInboundHandler websocketInboundHandler = new WebsocketInboundHandler() {
            @Override
            protected String getRemoteIP(ChannelHandlerContext ctx) {
                return "localhost";
            }
        };
        try {
            websocketInboundHandler.doThrottle(channelHandlerContext, webSocketFrame);
            fail("Expected NumberFormatException is not thrown.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }
    }
}
