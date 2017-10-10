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

package org.wso2.carbon.apimgt.gateway.handlers.analytics;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.apache.synapse.config.Entry;
import org.apache.synapse.config.SynapseConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wso2.carbon.apimgt.usage.publisher.APIMgtUsagePublisherConstants;

import javax.xml.namespace.QName;

/**
 * Test class for APIMgtGoogleAnalyticsTrackingHandler
 */
public class APIMgtGoogleAnalyticsTrackingHandlerTestCase {

    @Test
    public void testHandleRequest() {
        MessageContext msgCtx1 = Mockito.mock(MessageContext.class);
        APIMgtGoogleAnalyticsTrackingHandler apiMgtGoogleAnalyticsTrackingHandler
                = new APIMgtGoogleAnalyticsTrackingHandler() {
            @Override
            protected APIMgtGoogleAnalyticsTrackingHandler.GoogleAnalyticsConfig getGoogleAnalyticsConfig(OMElement entryValue) {
                APIMgtGoogleAnalyticsTrackingHandler.GoogleAnalyticsConfig config =
                        Mockito.mock(APIMgtGoogleAnalyticsTrackingHandler.GoogleAnalyticsConfig.class);
                Mockito.when(config.isEnabled()).thenReturn(true);
                return config;
            }
        };
        try {
            apiMgtGoogleAnalyticsTrackingHandler.handleRequest(msgCtx1);
        } catch (Exception e) {
//             test for exception, hence ignoring
        }
        MessageContext msgCtx = Mockito.mock(MessageContext.class);
        SynapseConfiguration synapseConfiguration = Mockito.mock(SynapseConfiguration.class);
        Mockito.when(msgCtx.getConfiguration()).thenReturn(synapseConfiguration);
        Mockito.when(synapseConfiguration.getEntryDefinition("abc")).thenReturn(null);

        apiMgtGoogleAnalyticsTrackingHandler.setConfigKey("abc");
        //test when entry is null
        Assert.assertTrue(apiMgtGoogleAnalyticsTrackingHandler.handleRequest(msgCtx));

        //test when entry is not null
        Entry entry = new Entry();
        Mockito.when(synapseConfiguration.getEntryDefinition("abc")).thenReturn(entry);
        Assert.assertTrue(apiMgtGoogleAnalyticsTrackingHandler.handleRequest(msgCtx));

        //test when entry is dynamic
        entry.setType(3);
        Assert.assertTrue(apiMgtGoogleAnalyticsTrackingHandler.handleRequest(msgCtx));

        //test when version == 0
        entry.setVersion(1);
        Assert.assertTrue(apiMgtGoogleAnalyticsTrackingHandler.handleRequest(msgCtx));

        OMElement entryvalue = Mockito.mock(OMElement.class);
        Mockito.when(msgCtx.getEntry("abc")).thenReturn(entryvalue);
        Assert.assertTrue(apiMgtGoogleAnalyticsTrackingHandler.handleRequest(msgCtx));

        //test when config.enable = true
        Mockito.when(entryvalue.getFirstChildWithName(new QName(
                APIMgtUsagePublisherConstants.API_GOOGLE_ANALYTICS_TRACKING_ENABLED))).thenReturn(entryvalue);
        Mockito.when(entryvalue.getText()).thenReturn("true");
        APIMgtGoogleAnalyticsTrackingHandler.GoogleAnalyticsConfig config = apiMgtGoogleAnalyticsTrackingHandler.
                getGoogleAnalyticsConfig(entryvalue);
        // Test exception thrown from trackPageView
        Assert.assertTrue(apiMgtGoogleAnalyticsTrackingHandler.handleRequest(msgCtx));

    }
}
