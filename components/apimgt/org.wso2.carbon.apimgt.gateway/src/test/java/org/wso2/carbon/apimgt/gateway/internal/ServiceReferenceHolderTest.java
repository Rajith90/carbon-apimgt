/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.apimgt.gateway.internal;


import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.engine.AxisConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentInstance;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.gateway.throttling.ThrottleDataHolder;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dto.ThrottleProperties;
import org.wso2.carbon.utils.ConfigurationContextService;

import java.util.Dictionary;


public class ServiceReferenceHolderTest {

    private static final ServiceReferenceHolderTest instance = new ServiceReferenceHolderTest();

    @Test
    public void testSetAPIManagerConfigurationService() {
        APIManagerConfigurationService amConfigService = Mockito.mock(APIManagerConfigurationService.class);
        ServiceReferenceHolder.getInstance().setAPIManagerConfigurationService(amConfigService);
    }

    @Test
    public void testSetConfigurationContextService() throws APIManagementException {
        AxisConfiguration axisConfiguration = new AxisConfiguration();
        ConfigurationContext context = new ConfigurationContext(axisConfiguration);
        ConfigurationContextService configurationContextService = Mockito.mock(ConfigurationContextService.class);
        Mockito.when(configurationContextService.getClientConfigContext()).thenReturn(context);
        ServiceReferenceHolder.getInstance().setConfigurationContextService(configurationContextService);
        ServiceReferenceHolder.getInstance().setThrottleDataHolder(Mockito.mock(ThrottleDataHolder.class));
        ServiceReferenceHolder.getInstance().getThrottleDataHolder();
        ServiceReferenceHolder.getInstance().getServerConfigurationContext();
        ServiceReferenceHolder.getInstance().getConfigurationContextService();
        //TODO need to add proper assertions
    }

    @Test
    public void testUnsetConfigurationContextService() {
        ExtendedAPIHandlerServiceComponent apiHandlerServiceComponent = new ExtendedAPIHandlerServiceComponent();
        ComponentContext componentContext = new ComponentContext() {
            @Override
            public Dictionary getProperties() {
                return null;
            }

            @Override
            public Object locateService(String s) {
                return null;
            }

            @Override
            public Object locateService(String s, ServiceReference serviceReference) {
                return null;
            }

            @Override
            public Object[] locateServices(String s) {
                return new Object[0];
            }

            @Override
            public BundleContext getBundleContext() {
                return Mockito.mock(BundleContext.class);
            }

            @Override
            public Bundle getUsingBundle() {
                return null;
            }

            @Override
            public ComponentInstance getComponentInstance() {
                return null;
            }

            @Override
            public void enableComponent(String s) {

            }

            @Override
            public void disableComponent(String s) {

            }

            @Override
            public ServiceReference getServiceReference() {
                return null;
            }
        };
        apiHandlerServiceComponent.activate(componentContext);
        apiHandlerServiceComponent.deactivate(componentContext);
        //TODO need to add proper assertions
    }
}
