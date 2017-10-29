package org.wso2.carbon.apimgt.impl;

import org.mockito.Mockito;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;

public class RealmServiceMockCreator {
    private RealmService realmService;
    private TenantManagerMockCreator tenantManagerMockCreator;
    private UserRealmMockCreator userRealmMockCreator;

    public RealmServiceMockCreator(int tenantId) throws UserStoreException {
        userRealmMockCreator = new UserRealmMockCreator();
        tenantManagerMockCreator = new TenantManagerMockCreator(tenantId);
        realmService = Mockito.mock(RealmService.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManagerMockCreator.getMock());
        Mockito.when(realmService.getTenantUserRealm(Mockito.anyInt())).thenReturn(userRealmMockCreator.getMock());
    }

    public TenantManagerMockCreator getTenantManagerMockCreator() {
        return tenantManagerMockCreator;
    }

    public UserRealmMockCreator getUserRealmMockCreator() {
        return userRealmMockCreator;
    }

    RealmService getMock() {
        return realmService;
    }

}
