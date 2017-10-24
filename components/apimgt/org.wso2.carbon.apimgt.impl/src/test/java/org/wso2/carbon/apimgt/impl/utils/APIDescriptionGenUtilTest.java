/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.apimgt.impl.utils;

import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.wso2.carbon.apimgt.api.APIManagementException;

/**
 * 
 * Unit tests for {@link APIDescriptionGenUtil}
 *
 */
public class APIDescriptionGenUtilTest {

	String policyString = 
			"<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
			+ " xmlns:throttle=\"http://www.wso2.org/products/wso2commons/throttle\">" +
        "<throttle:ID throttle:type=\"ROLE\">Gold</throttle:ID>"+
        "<wsp:Policy>"+
            "<throttle:Control>"+
                "<wsp:Policy>"+
                    "<throttle:MaximumCount>20</throttle:MaximumCount>"+
                    "<throttle:UnitTime>60000</throttle:UnitTime>"+
                    "<wsp:Policy>"+
                    "<throttle:Attributes>"+                   
                       "<throttle:PaymentPlan>monthly</throttle:PaymentPlan>"+
                       "<throttle:Availability>FullTime</throttle:Availability>"+
                    "</throttle:Attributes>"+
                 "</wsp:Policy>"+  
                "</wsp:Policy>"+                         
            "</throttle:Control>"+
        "</wsp:Policy>"+
     "</wsp:Policy>";
	
	String policyStringMaxCount0 = 
			"<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
			+ " xmlns:throttle=\"http://www.wso2.org/products/wso2commons/throttle\">" +
        "<throttle:ID throttle:type=\"ROLE\">Gold</throttle:ID>"+
        "<wsp:Policy>"+
            "<throttle:Control>"+
                "<wsp:Policy>"+
                    "<throttle:MaximumCount>0</throttle:MaximumCount>"+
                    "<throttle:UnitTime>60000</throttle:UnitTime>"+
                "</wsp:Policy>"+
            "</throttle:Control>"+
        "</wsp:Policy>"+
     "</wsp:Policy>";
	
	String policyStringEmptyUnitTime = 
			"<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
			+ " xmlns:throttle=\"http://www.wso2.org/products/wso2commons/throttle\">" +
        "<throttle:ID throttle:type=\"ROLE\">Gold</throttle:ID>"+
        "<wsp:Policy>"+
            "<throttle:Control>"+
                "<wsp:Policy>"+
                    "<throttle:MaximumCount>20</throttle:MaximumCount>"+
                    "<throttle:UnitTime></throttle:UnitTime>"+
                "</wsp:Policy>"+
            "</throttle:Control>"+
        "</wsp:Policy>"+
     "</wsp:Policy>";
	
	String policyStringEmptyMaxCount = 
			"<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
			+ " xmlns:throttle=\"http://www.wso2.org/products/wso2commons/throttle\">" +
        "<throttle:ID throttle:type=\"ROLE\">Gold</throttle:ID>"+
        "<wsp:Policy>"+
            "<throttle:Control>"+
                "<wsp:Policy>"+
                    "<throttle:MaximumCount></throttle:MaximumCount>"+
                    "<throttle:UnitTime>60000</throttle:UnitTime>"+
                "</wsp:Policy>"+
            "</throttle:Control>"+
        "</wsp:Policy>"+
     "</wsp:Policy>";
	
	String policyStringNumberFormatError = 
			"<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
			+ " xmlns:throttle=\"http://www.wso2.org/products/wso2commons/throttle\">" +
        "<throttle:ID throttle:type=\"ROLE\">Gold</throttle:ID>"+
        "<wsp:Policy>"+
            "<throttle:Control>"+
                "<wsp:Policy>"+
                    "<throttle:MaximumCount>ssss</throttle:MaximumCount>"+
                    "<throttle:UnitTime>ssss</throttle:UnitTime>"+
                "</wsp:Policy>"+
            "</throttle:Control>"+
        "</wsp:Policy>"+
     "</wsp:Policy>";
	
	String policyStringNullAttributes = 
			"<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
			+ " xmlns:throttle=\"http://www.wso2.org/products/wso2commons/throttle\">" +
        "<throttle:ID throttle:type=\"ROLE\">Gold</throttle:ID>"+
        "<wsp:Policy>"+
            "<throttle:Control>"+
                "<wsp:Policy>"+
                    "<throttle:MaximumCount>20</throttle:MaximumCount>"+
                    "<throttle:UnitTime>60000</throttle:UnitTime>"+
                    "<wsp:Policy>"+                    
                    "</wsp:Policy>"+  
                "</wsp:Policy>"+                         
            "</throttle:Control>"+
        "</wsp:Policy>"+
     "</wsp:Policy>";
	
	@Test
	public void testGenerateDescriptionFromPolicy() throws Exception {

		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyString))
				.getDocumentElement();
		Assert.assertEquals("Allows 20 request(s) per minute.",
				APIDescriptionGenUtil.generateDescriptionFromPolicy(policyElement));

	}
	
	@Test(expected = APIManagementException.class)
	public void testGenerateDescriptionWithError() throws Exception {
		String errorElement = "<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
				+ " xmlns:throttle=\"http://www.wso2.org/products/wso2commons/throttle\"> </wsp:Policy>";
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(errorElement))
				.getDocumentElement();
		APIDescriptionGenUtil.generateDescriptionFromPolicy(policyElement);
	}
	
	@Test
	public void testGenerateDescriptionFromPolicyFor0ReqPerMin() throws Exception {
		
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.
				toInputStream(policyStringMaxCount0)).getDocumentElement();
		Assert.assertEquals("Allows [1] request(s) per minute.",
				APIDescriptionGenUtil.generateDescriptionFromPolicy(policyElement));	
	}
	
	
	@Test(expected = APIManagementException.class)
	public void testGenerateDescriptionWithMaxCountEmpty() throws Exception {
		
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyStringEmptyMaxCount))
				.getDocumentElement();
		APIDescriptionGenUtil.generateDescriptionFromPolicy(policyElement);
	}
	
	@Test(expected = APIManagementException.class)
	public void testGenerateDescriptionWithTimeUnitEmpty() throws Exception {
		
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyStringEmptyUnitTime))
				.getDocumentElement();
		APIDescriptionGenUtil.generateDescriptionFromPolicy(policyElement);
	}
	
	@Test
	public void testGetAllowedRequestConuntFromPolicy() throws Exception {

		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyString))
				.getDocumentElement();
		Assert.assertEquals(20, APIDescriptionGenUtil.getAllowedRequestCount(policyElement));
	}
	
	@Test(expected = APIManagementException.class)
	public void testGetAllowedRequestConuntWithMaxCountEmpty() throws Exception {
		
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyStringEmptyMaxCount))
				.getDocumentElement();
		APIDescriptionGenUtil.getAllowedRequestCount(policyElement);
	}
	
	@Test(expected = Exception.class)
	public void testGetAllowedRequestConuntWithError() throws Exception {
		
		String errorElement = "<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
				+ " xmlns:throttle=\"http://www.wso2.org/products/wso2commons/throttle\"> </wsp:Policy>";
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(errorElement))
				.getDocumentElement();
		APIDescriptionGenUtil.getAllowedRequestCount(policyElement);
	}
	
	@Test(expected = Exception.class)
	public void testGetAllowedRequestConuntWithNumberFormatError() throws Exception {
		
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyStringNumberFormatError))
				.getDocumentElement();
		APIDescriptionGenUtil.getAllowedRequestCount(policyElement);
	}
		
	@Test
	public void testGetTimeDurationFromPolicy() throws Exception {

		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyString))
				.getDocumentElement();
		Assert.assertEquals(60000, APIDescriptionGenUtil.getTimeDuration(policyElement));
	}
	
	@Test(expected = Exception.class)
	public void testGetTimeDurationWithNumberFormatError() throws Exception {
		
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyStringNumberFormatError))
				.getDocumentElement();
		APIDescriptionGenUtil.getTimeDuration(policyElement);
	}
	
	@Test(expected = Exception.class)
	public void testGetTimeDurationWithEmptyValue() throws Exception {
		
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyStringEmptyUnitTime))
				.getDocumentElement();
		APIDescriptionGenUtil.getTimeDuration(policyElement);
	}
	
	@Test
	public void testGetAllowedRequestConuntPerMinFromPolicy() throws Exception {

		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyString))
				.getDocumentElement();
		Assert.assertEquals(20, APIDescriptionGenUtil.getAllowedCountPerMinute(policyElement));
	}
	
	@Test(expected = APIManagementException.class)
	public void testGetAllowedRequestConuntPerMinWithMaxCountEmpty() throws Exception {
		
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyStringEmptyMaxCount))
				.getDocumentElement();
		APIDescriptionGenUtil.getAllowedCountPerMinute(policyElement);
	}
	
	@Test(expected = Exception.class)
	public void testGetAllowedRequestConuntPerMinWithError() throws Exception {
		
		String errorElement = "<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
				+ " xmlns:throttle=\"http://www.wso2.org/products/wso2commons/throttle\"> </wsp:Policy>";
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(errorElement))
				.getDocumentElement();
		APIDescriptionGenUtil.getAllowedCountPerMinute(policyElement);
	}
	
	@Test(expected = Exception.class)
	public void testGetAllowedRequestConuntPerMinWithNumberFormatError() throws Exception {
		
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyStringNumberFormatError))
				.getDocumentElement();
		APIDescriptionGenUtil.getAllowedCountPerMinute(policyElement);
	}
	
	@Test
	public void testGetAttributesFromPolicy() throws Exception {

		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyString))
				.getDocumentElement();
		Map<String, Object> policyAttributes = APIDescriptionGenUtil.getTierAttributes(policyElement);
	    Assert.assertTrue(policyAttributes.containsKey("PaymentPlan"));
	}
	
	@Test(expected = Exception.class)
	public void testGetAttributesWithError() throws Exception {
		
		String errorElement = "<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
				+ " xmlns:throttle=\"http://www.wso2.org/products/wso2commons/throttle\"> </wsp:Policy>";
		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(errorElement))
				.getDocumentElement();
		APIDescriptionGenUtil.getTierAttributes(policyElement);
	}
	
	@Test
	public void testGetAttributesEmptyAttributesFromPolicy() throws Exception {

		OMElement policyElement = OMXMLBuilderFactory.createOMBuilder(IOUtils.toInputStream(policyStringNullAttributes))
				.getDocumentElement();
		Map<String, Object> policyAttributes = APIDescriptionGenUtil.getTierAttributes(policyElement);
	    Assert.assertTrue(policyAttributes.isEmpty());
	}
}
