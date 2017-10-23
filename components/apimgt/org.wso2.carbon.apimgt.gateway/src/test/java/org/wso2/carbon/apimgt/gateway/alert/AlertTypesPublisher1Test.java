/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.apimgt.gateway.alert;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIManagerAnalyticsConfiguration;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.usage.publisher.APIMgtUsageDataPublisher;
import org.wso2.carbon.apimgt.usage.publisher.DataPublisherUtil;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.sql.SQLException;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {APIManagerAnalyticsConfiguration.class, DataPublisherUtil.class, APIUtil.class})
public class AlertTypesPublisher1Test {
    @Test
    public void saveAndPublishAlertTypesEvent() throws Exception {
        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        APIMgtUsageDataPublisher apiMgtUsageDataPublisher = Mockito.mock(APIMgtUsageDataPublisher.class);
        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration = Mockito.mock
                (APIManagerAnalyticsConfiguration.class);
        PowerMockito.mockStatic(APIManagerAnalyticsConfiguration.class);
        PowerMockito.mockStatic(DataPublisherUtil.class);
        BDDMockito.given(APIManagerAnalyticsConfiguration.getInstance()).willReturn(apiManagerAnalyticsConfiguration);
        BDDMockito.given(DataPublisherUtil.getApiManagerAnalyticsConfiguration()).willReturn
                (apiManagerAnalyticsConfiguration);
        AlertTypesPublisher alertTypesPublisher = new AlertTypesPublisherWrapper(apiMgtDAO);
        alertTypesPublisher.publisher = apiMgtUsageDataPublisher;
        alertTypesPublisher.enabled = true;
        alertTypesPublisher.skipEventReceiverConnection = false;
        alertTypesPublisher.saveAndPublishAlertTypesEvent("abc", "abc@de.com", "admin", "subscriber", "aa");
        alertTypesPublisher.saveAndPublishAlertTypesEvent("abc", "abc@de.com", "admin", "admin-dashboard", "aa");
        alertTypesPublisher.saveAndPublishAlertTypesEvent("abc", "abc@de.com", "admin", "publisher", "aa");
    }

    @Test
    public void saveAlertWhileDatabaseConnectionFailed() throws APIManagementException, SQLException {
        String checkedAlertList = "health-availability";
        String emailList = "abc@de.com";
        String userName = "admin@carbon.super";
        String agent = "publisher";
        String checkedAlertListValues = "true";
        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        APIMgtUsageDataPublisher apiMgtUsageDataPublisher = Mockito.mock(APIMgtUsageDataPublisher.class);
        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration = Mockito.mock
                (APIManagerAnalyticsConfiguration.class);
        PowerMockito.mockStatic(APIManagerAnalyticsConfiguration.class);
        PowerMockito.mockStatic(DataPublisherUtil.class);
        BDDMockito.given(APIManagerAnalyticsConfiguration.getInstance()).willReturn(apiManagerAnalyticsConfiguration);
        BDDMockito.given(DataPublisherUtil.getApiManagerAnalyticsConfiguration()).willReturn
                (apiManagerAnalyticsConfiguration);
        AlertTypesPublisher alertTypesPublisher = new AlertTypesPublisherWrapper(apiMgtDAO);
        alertTypesPublisher.publisher = apiMgtUsageDataPublisher;
        alertTypesPublisher.enabled = true;
        alertTypesPublisher.skipEventReceiverConnection = false;
        Mockito.doThrow(SQLException.class).when(apiMgtDAO).addAlertTypesConfigInfo(userName, emailList,
                checkedAlertList, agent);
        try {
            alertTypesPublisher.saveAndPublishAlertTypesEvent(checkedAlertList, emailList, userName, agent,
                    checkedAlertListValues);
        } catch (Exception e) {
            if (e instanceof SQLException) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        }
    }

    @Test
    public void unSubscribe() throws Exception {
        String userName = "admin@carbon.super";
        String agent = "publisher";
        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration = Mockito.mock
                (APIManagerAnalyticsConfiguration.class);
        PowerMockito.mockStatic(APIManagerAnalyticsConfiguration.class);
        PowerMockito.mockStatic(DataPublisherUtil.class);
        BDDMockito.given(APIManagerAnalyticsConfiguration.getInstance()).willReturn(apiManagerAnalyticsConfiguration);
        BDDMockito.given(DataPublisherUtil.getApiManagerAnalyticsConfiguration()).willReturn
                (apiManagerAnalyticsConfiguration);
        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        Mockito.doNothing().when(apiMgtDAO).unSubscribeAlerts(userName, agent);
        Mockito.doNothing().when(apiMgtDAO).unSubscribeAlerts(userName, "subscriber");
        Mockito.doNothing().when(apiMgtDAO).unSubscribeAlerts(userName, "admin-dashboard");
        APIMgtUsageDataPublisher apiMgtUsageDataPublisher = Mockito.mock(APIMgtUsageDataPublisher.class);
        AlertTypesPublisher alertTypesPublisher = new AlertTypesPublisherWrapper(apiMgtDAO);
        alertTypesPublisher.enabled = true;
        alertTypesPublisher.skipEventReceiverConnection = false;
        alertTypesPublisher.publisher = apiMgtUsageDataPublisher;
        alertTypesPublisher.unSubscribe(userName, agent);
        alertTypesPublisher.unSubscribe(userName, "subscriber");
        alertTypesPublisher.unSubscribe(userName, "admin-dashboard");
    }
    @Test
    public void testInitializeDataPublisherWhenAnalyticsDisable() throws Exception {
        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration = Mockito.mock
                (APIManagerAnalyticsConfiguration.class);
        PowerMockito.mockStatic(APIManagerAnalyticsConfiguration.class);
        PowerMockito.mockStatic(DataPublisherUtil.class);
        BDDMockito.given(APIManagerAnalyticsConfiguration.getInstance()).willReturn(apiManagerAnalyticsConfiguration);
        BDDMockito.given(DataPublisherUtil.getApiManagerAnalyticsConfiguration()).willReturn
                (apiManagerAnalyticsConfiguration);
        ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
        AlertTypesPublisher alertTypesPublisher = new AlertTypesPublisherWrapper(apiMgtDAO);
        alertTypesPublisher.enabled = false;
        alertTypesPublisher.skipEventReceiverConnection = false;
        alertTypesPublisher.initializeDataPublisher();
    }

    @Test
    public void testInitializeDataPublisher() throws Exception {
        System.setProperty("carbon.home", AlertTypesPublisher1Test.class.getResource("/").getFile());
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername("admin");
            APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration = Mockito.mock
                    (APIManagerAnalyticsConfiguration.class);
            Mockito.when(apiManagerAnalyticsConfiguration.getPublisherClass()).thenReturn("org.wso2.carbon.apimgt" +
                    ".usage.publisher.APIMgtUsageDataBridgeDataPublisher");
            PowerMockito.mockStatic(APIManagerAnalyticsConfiguration.class);
            PowerMockito.mockStatic(DataPublisherUtil.class);
            BDDMockito.given(APIManagerAnalyticsConfiguration.getInstance()).willReturn
                    (apiManagerAnalyticsConfiguration);
            BDDMockito.given(DataPublisherUtil.getApiManagerAnalyticsConfiguration()).willReturn
                    (apiManagerAnalyticsConfiguration);
            ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
            AlertTypesPublisher alertTypesPublisher = new AlertTypesPublisherWrapper(apiMgtDAO);
            alertTypesPublisher.enabled = true;
            alertTypesPublisher.skipEventReceiverConnection = false;
            alertTypesPublisher.initializeDataPublisher();
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    @Test
    public void testInitializeDataPublisherWhileClassNotGetLoad() throws IllegalAccessException,
            InstantiationException, ClassNotFoundException {
        PowerMockito.mockStatic(APIUtil.class);
        System.setProperty("carbon.home", AlertTypesPublisher1Test.class.getResource("/").getFile());
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername("admin");
            APIManagerAnalyticsConfiguration apiManagerAnalyticsConfiguration = Mockito.mock
                    (APIManagerAnalyticsConfiguration.class);
            Mockito.when(apiManagerAnalyticsConfiguration.getPublisherClass()).thenReturn("org.wso2.carbon.apimgt" +
                    ".usage.publisher.APIMgtUsageDataBridgeDataPublisher");
            PowerMockito.when(APIUtil.getClassForName("org.wso2.carbon.apimgt.usage.publisher" +
                    ".APIMgtUsageDataBridgeDataPublisher")).thenThrow(ClassNotFoundException.class).thenThrow
                    (InstantiationException.class).thenThrow(IllegalAccessException.class);
            PowerMockito.mockStatic(APIManagerAnalyticsConfiguration.class);
            PowerMockito.mockStatic(DataPublisherUtil.class);
            BDDMockito.given(APIManagerAnalyticsConfiguration.getInstance()).willReturn
                    (apiManagerAnalyticsConfiguration);
            BDDMockito.given(DataPublisherUtil.getApiManagerAnalyticsConfiguration()).willReturn
                    (apiManagerAnalyticsConfiguration);

            ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
            AlertTypesPublisher alertTypesPublisher = new AlertTypesPublisherWrapper(apiMgtDAO);
            alertTypesPublisher.enabled = true;
            alertTypesPublisher.skipEventReceiverConnection = false;
            alertTypesPublisher.initializeDataPublisher();
            alertTypesPublisher.initializeDataPublisher();
            alertTypesPublisher.initializeDataPublisher();

        } finally{
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

}