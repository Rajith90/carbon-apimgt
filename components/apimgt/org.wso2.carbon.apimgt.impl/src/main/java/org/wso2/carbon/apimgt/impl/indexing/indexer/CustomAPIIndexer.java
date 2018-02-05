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

package org.wso2.carbon.apimgt.impl.indexing.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrException;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.governance.registry.extensions.indexers.RXTIndexer;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.indexing.AsyncIndexer;
import org.wso2.carbon.registry.indexing.IndexingManager;
import org.wso2.carbon.registry.indexing.solr.IndexDocument;

import static org.wso2.carbon.apimgt.impl.APIConstants.CUSTOM_API_INDEXER_PROPERTY;

/**
 * This is the custom indexer to add the API properties, to existing APIs.
 */
@SuppressWarnings("unused") public class CustomAPIIndexer extends RXTIndexer {
    public static final Log log = LogFactory.getLog(CustomAPIIndexer.class);

    public IndexDocument getIndexedDocument(AsyncIndexer.File2Index fileData) throws SolrException, RegistryException {
        Registry registry = GovernanceUtils
                .getGovernanceSystemRegistry(IndexingManager.getInstance().getRegistry(fileData.tenantId));
        String resourcePath = fileData.path.substring(RegistryConstants.GOVERNANCE_REGISTRY_BASE_PATH.length());
        Resource resource = null;

        if (registry.resourceExists(resourcePath)) {
            resource = registry.get(resourcePath);
        }
        if (log.isDebugEnabled()) {
            log.debug("CustomAPIIndexer is currently indexing the api at path " + resourcePath);
        }
        if (resource != null) {
            String publisherAccessControl = resource.getProperty(APIConstants.PUBLISHER_ROLES);

            if (publisherAccessControl == null || publisherAccessControl.trim().isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("API at " + resourcePath + "did not have property : " + APIConstants.PUBLISHER_ROLES
                            + ", hence adding the null value for that API resource.");
                }
                resource.setProperty(APIConstants.PUBLISHER_ROLES, "null");
                resource.setProperty(APIConstants.ACCESS_CONTROL, APIConstants.NO_ACCESS_CONTROL);
                setStoreViewRoles(registry, resource, publisherAccessControl);
                resource.setProperty(CUSTOM_API_INDEXER_PROPERTY, "true");
                registry.put(resourcePath, resource);
            }

            resource = registry.get(resourcePath);
            String storeViewRoles = resource.getProperty(APIConstants.STORE_VIEW_ROLES);

            if (storeViewRoles == null) {
                if (log.isDebugEnabled()) {
                    log.debug("API at " + resourcePath + "did not have property : " + APIConstants.STORE_VIEW_ROLES
                            + ", hence adding the values for that API resource.");
                }
                setStoreViewRoles(registry, resource, publisherAccessControl);
                resource.setProperty(CUSTOM_API_INDEXER_PROPERTY, "true");
                registry.put(resourcePath, resource);
            }
        }
        return super.getIndexedDocument(fileData);
    }

    /**
     * To set the store_view_roles property
     *
     * @param registry Registry
     * @param resource Resource
     * @param publisherAccessControl Publisher access control roles
     * @throws GovernanceException Throws when retrieving artifacts get failed
     */
    private void setStoreViewRoles(Registry registry, Resource resource, String publisherAccessControl) throws GovernanceException {
        String storeVisibility = null;
        String storeVisibleRoles = null;
        try {
            GenericArtifact artifact = APIUtil.getArtifactManager(registry, "api").getGenericArtifact(resource.getUUID());
            storeVisibility = artifact.getAttribute(APIConstants.API_OVERVIEW_VISIBILITY);
            storeVisibleRoles = artifact.getAttribute(APIConstants.API_OVERVIEW_VISIBLE_ROLES);
        } catch (APIManagementException e) {
            // We need to continue default indexing process although access control extension faces an error,
            // So not throwing an exception here.
            log.error("Error while retrieving API", e);
        }
        if (storeVisibility.equals(APIConstants.PUBLIC_STORE_VISIBILITY)) {
            resource.setProperty(APIConstants.STORE_VIEW_ROLES, "null");
        } else if (storeVisibility.equals(APIConstants.RESTRICTED_STORE_VISIBILITY)){
            resource.setProperty(APIConstants.STORE_VIEW_ROLES, storeVisibleRoles + "," + publisherAccessControl);
        }
    }
}
