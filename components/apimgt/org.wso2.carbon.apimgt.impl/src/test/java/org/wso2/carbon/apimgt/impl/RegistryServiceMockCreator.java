package org.wso2.carbon.apimgt.impl;

import org.mockito.Mockito;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;

public class RegistryServiceMockCreator {
    private RegistryService registryService;
    private UserRegistryMockCreator registryMockCreator;

    public RegistryServiceMockCreator(boolean isResourceExists, Object content) throws RegistryException {
        registryService = Mockito.mock(RegistryService.class);
        registryMockCreator = new UserRegistryMockCreator(isResourceExists, content);

        Mockito.when(registryService.getConfigSystemRegistry(Mockito.anyInt())).thenReturn(registryMockCreator.getMock());
        Mockito.when(registryService.getConfigSystemRegistry()).thenReturn(registryMockCreator.getMock());
    }

    public UserRegistryMockCreator getRegistryMockCreator() {
        return registryMockCreator;
    }

    RegistryService getMock() {
        return registryService;
    }
}
