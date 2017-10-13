package org.wso2.carbon.apimgt.gateway.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.context.PrivilegedCarbonContext;

/**
 * Test class for WebsocketHandler
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PrivilegedCarbonContext.class)
public class WebsocketHandlerTestCase {

    @Before
    public void setup() {
        System.setProperty("carbon.home", "jhkjn");
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        PrivilegedCarbonContext privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        PowerMockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);

    }

    /*
    * This method tests write() when msg is not a WebSocketFrame
    * */
    @Test
    public void testWrite() throws Exception {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        ChannelPromise channelPromise = Mockito.mock(ChannelPromise.class);
        Object msg = "msg";
        WebsocketHandler websocketHandler = new WebsocketHandler();
        websocketHandler.write(channelHandlerContext, msg, channelPromise);
        System.out.println("");
    }

    /*
   * This method tests write() when msg is a WebSocketFrame
   * */
    @Test
    public void testWrite1() throws Exception {
        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        ChannelPromise channelPromise = Mockito.mock(ChannelPromise.class);
        WebSocketFrame msg = Mockito.mock(WebSocketFrame.class);
        ByteBuf content = Mockito.mock(ByteBuf.class);
        Mockito.when(msg.content()).thenReturn(content);
        WebsocketHandler websocketHandler = new WebsocketHandler() {
            @Override
            protected boolean isThrottled(ChannelHandlerContext ctx, WebSocketFrame msg) throws APIManagementException {
                return true;
            }
        };
        WebsocketInboundHandler websocketInboundHandler = Mockito.mock(WebsocketInboundHandler.class);
        Mockito.when(websocketInboundHandler.doThrottle(channelHandlerContext, msg)).thenReturn(true);
        websocketHandler.write(channelHandlerContext, msg, channelPromise);

    }
}
