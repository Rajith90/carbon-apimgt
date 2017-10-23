/*
 *  Copyright WSO2 Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.apimgt.gateway.throttling.publisher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.gateway.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.dto.ThrottleProperties;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ThrottleDataPublisher.class})
public class ThrottleDataPublisherPoolTest {

    @Test
    public void testGetThrottleDataPublisher() throws Exception {
        ThrottleProperties throttleProperties = new ThrottleProperties();
        ThrottleProperties.DataPublisherPool dataPublisherPool = new ThrottleProperties.DataPublisherPool();
        ThrottleProperties.DataPublisher dataPublisher1 = new ThrottleProperties.DataPublisher();
        dataPublisher1.setAuthUrlGroup("ssl://localhost:9711");
        dataPublisher1.setEnabled(true);
        dataPublisher1.setUsername("admin");
        dataPublisher1.setPassword("admin");
        dataPublisherPool.setMaxIdle(10);
        dataPublisher1.setReceiverUrlGroup("tcp://localhost:9611");
        dataPublisherPool.setInitIdleCapacity(10);
        throttleProperties.setDataPublisher(dataPublisher1);
        ThrottleProperties.DataPublisherThreadPool dataPublisherThreadPool = new ThrottleProperties
                .DataPublisherThreadPool();
        dataPublisherThreadPool.setCorePoolSize(10);
        dataPublisherThreadPool.setKeepAliveTime(10);
        dataPublisherThreadPool.setMaximumPoolSize(10);
        throttleProperties.setDataPublisherThreadPool(dataPublisherThreadPool);
        throttleProperties.setDataPublisherPool(dataPublisherPool);
        ServiceReferenceHolder.getInstance().setThrottleProperties(throttleProperties);
        DataPublisher dataPublisher = Mockito.mock(DataPublisher.class);
        PowerMockito.whenNew(DataPublisher.class).withArguments(Mockito.anyString(), Mockito.anyString(), Mockito
                .anyString(), Mockito.anyString(), Mockito.anyString()).thenReturn(dataPublisher).thenThrow
                (DataEndpointAgentConfigurationException.class).thenThrow(DataEndpointException.class).thenThrow
                (DataEndpointConfigurationException.class).thenThrow(DataEndpointAuthenticationException.class)
                .thenThrow(TransportException.class);
        ThrottleDataPublisher throttleDataPublisher = new ThrottleDataPublisher();
        throttleDataPublisher.publishNonThrottledEvent("","","","","","","","","","","","","","",null,null);
        ThrottleDataPublisherPool throttleDataPublisherPool = ThrottleDataPublisherPool.getInstance();
        DataProcessAndPublishingAgent dataProcessAndPublishingAgent = throttleDataPublisherPool.get();
        throttleDataPublisherPool.release(dataProcessAndPublishingAgent);
        throttleDataPublisherPool.cleanup();
        ThrottleDataPublisher throttleDataPublisher1 = new ThrottleDataPublisher();
        ThrottleDataPublisher throttleDataPublisher2 = new ThrottleDataPublisher();
        ThrottleDataPublisher throttleDataPublisher3 = new ThrottleDataPublisher();
        ThrottleDataPublisher throttleDataPublisher4 = new ThrottleDataPublisher();
        ThrottleDataPublisher throttleDataPublisher5 = new ThrottleDataPublisher();
    }
}