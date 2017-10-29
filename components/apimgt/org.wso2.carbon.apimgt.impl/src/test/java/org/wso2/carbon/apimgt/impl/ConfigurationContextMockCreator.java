package org.wso2.carbon.apimgt.impl;

import org.apache.axis2.context.ConfigurationContext;
import org.mockito.Mockito;

public class ConfigurationContextMockCreator {
    private ConfigurationContext context;
    private AxisConfigurationMockCreator configurationMockCreator;

    public ConfigurationContextMockCreator() {
        context = Mockito.mock(ConfigurationContext.class);
        configurationMockCreator = new AxisConfigurationMockCreator();
        Mockito.when(context.getAxisConfiguration()).thenReturn(configurationMockCreator.getMock());
    }

    public ConfigurationContext getMock() {
        return context;
    }

    public AxisConfigurationMockCreator getConfigurationMockCreator() {
        return configurationMockCreator;
    }
}
