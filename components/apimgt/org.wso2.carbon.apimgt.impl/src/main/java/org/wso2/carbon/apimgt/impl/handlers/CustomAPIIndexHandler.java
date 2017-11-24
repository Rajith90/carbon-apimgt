package org.wso2.carbon.apimgt.impl.handlers;

import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.jdbc.handlers.RequestContext;
import org.wso2.carbon.registry.indexing.IndexingHandler;

public class CustomAPIIndexHandler extends IndexingHandler {
    public void put(RequestContext requestContext) throws RegistryException {
        if (requestContext.getResource().getProperty(APIConstants.CUSTOM_API_INDEXER_PROPERTY) != null) {
            return;
        }
        super.put(requestContext);
    }
}
