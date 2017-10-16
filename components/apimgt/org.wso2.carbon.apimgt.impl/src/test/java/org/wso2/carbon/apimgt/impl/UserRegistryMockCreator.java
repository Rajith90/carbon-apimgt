package org.wso2.carbon.apimgt.impl;

import org.mockito.Mockito;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;

public class UserRegistryMockCreator {
    private UserRegistry registry;
    private ResourceMockCreator resourceMockCreator;

    public UserRegistryMockCreator(boolean isResourceExists, Object content) throws RegistryException {
        registry = Mockito.mock(UserRegistry.class);
        resourceMockCreator = new ResourceMockCreator(content);

        Mockito.when(registry.resourceExists(Mockito.anyString())).thenReturn(isResourceExists);
        Mockito.when(registry.get(Mockito.anyString())).thenReturn(resourceMockCreator.getMock());
        Mockito.when(registry.newResource()).thenReturn(resourceMockCreator.getMock());
    }

    UserRegistry getMock() {
        return registry;
    }
}
