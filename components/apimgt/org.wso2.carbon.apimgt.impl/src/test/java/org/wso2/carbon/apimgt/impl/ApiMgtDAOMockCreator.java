package org.wso2.carbon.apimgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.UserStoreException;

public class ApiMgtDAOMockCreator {
    private ServiceReferenceHolderMockCreator serviceReferenceHolderMockCreator;
    private Log log = Mockito.mock(Log.class);

    public ApiMgtDAOMockCreator(int tenantId) throws RegistryException, UserStoreException {
        serviceReferenceHolderMockCreator = new ServiceReferenceHolderMockCreator(tenantId);
    }

    public ApiMgtDAO getMock() {
        PowerMockito.mockStatic(LogFactory.class);
        Mockito.when(LogFactory.getLog(Mockito.any(Class.class))).thenReturn(log);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolderMockCreator.getMock());

        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        PowerMockito.mockStatic(ApiMgtDAO.class);
        Mockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);

        return apiMgtDAO;
    }

    public ServiceReferenceHolderMockCreator getServiceReferenceHolderMockCreator() {
        return serviceReferenceHolderMockCreator;
    }
    
}
