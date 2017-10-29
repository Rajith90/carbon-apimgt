package org.wso2.carbon.apimgt.impl;

import org.mockito.Mockito;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

public class TenantManagerMockCreator {
    private TenantManager tenantManager;

    public TenantManagerMockCreator(int tenantId) throws UserStoreException {
        tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(tenantManager.getTenantId(Mockito.anyString())).thenReturn(tenantId);
    }

    TenantManager getMock() {
       return tenantManager;
    }

}
