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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.ibm.wsdl.extensions.http.HTTPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12AddressImpl;
import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.apache.woden.WSDLSource;
import org.apache.woden.internal.wsdl20.EndpointImpl;
import org.apache.woden.internal.wsdl20.ServiceImpl;
import org.apache.woden.wsdl20.Endpoint;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.xml.sax.InputSource;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ APIMWSDLReader.class, WSDLFactory.class, APIUtil.class, org.apache.woden.WSDLFactory.class,
        org.apache.woden.wsdl20.Service.class, URI.class, Endpoint.class, DocumentBuilderFactory.class })

public class APIMWSDLReaderExceptionTest {

    @Test(expected = APIManagementException.class)
    public void testUpdateWsdl2Exception() throws Exception {
        byte[] byteArray = "sampleWsdl".getBytes();
        DocumentBuilderFactory documentBuilderFactory = Mockito.mock(DocumentBuilderFactory.class);
        PowerMockito.mockStatic(DocumentBuilderFactory.class);
        PowerMockito.when(DocumentBuilderFactory.newInstance()).thenReturn(documentBuilderFactory);
        DocumentBuilder documentBuilder = Mockito.mock(DocumentBuilder.class);
        Mockito.when(documentBuilderFactory.newDocumentBuilder()).thenReturn(documentBuilder);
        org.apache.woden.WSDLFactory wsdlFactory = Mockito.mock(org.apache.woden.WSDLFactory.class);
        PowerMockito.mockStatic(org.apache.woden.WSDLFactory.class);
        PowerMockito.when(org.apache.woden.WSDLFactory.newInstance()).thenReturn(wsdlFactory);
        org.apache.woden.WSDLReader wsdlReader = Mockito.mock(org.apache.woden.WSDLReader.class);
        Mockito.when(wsdlFactory.newWSDLReader()).thenReturn(wsdlReader);
        org.apache.woden.WSDLWriter wsdlWriter = Mockito.mock(org.apache.woden.WSDLWriter.class);
        Mockito.when(wsdlFactory.newWSDLWriter()).thenReturn(wsdlWriter);
        Mockito.doNothing().when(wsdlWriter)
                .writeWSDL(Mockito.any(DescriptionElement.class), Mockito.any(OutputStream.class));
        Document document = Mockito.mock(Document.class);
        Mockito.when(documentBuilder.parse(Mockito.any(InputStream.class))).thenReturn(document);
        Element element = Mockito.mock(Element.class);
        Mockito.when(document.getDocumentElement()).thenReturn(element);
        WSDLSource wsdlSource = Mockito.mock(WSDLSource.class);
        Mockito.when(wsdlReader.createWSDLSource()).thenReturn(wsdlSource);
        org.apache.woden.wsdl20.Description description = Mockito.mock(org.apache.woden.wsdl20.Description.class);
        Mockito.when(wsdlReader.readWSDL(Mockito.any(WSDLSource.class))).thenReturn(description);

        ServiceImpl service = Mockito.spy(ServiceImpl.class);
        EndpointImpl endpoint = new EndpointImpl();
        endpoint.setAddress(URI.create("http://localhost"));
        Endpoint[] endpoints = { endpoint };
        Mockito.when(service.getEndpoints()).thenReturn(endpoints);
        org.apache.woden.wsdl20.Service[] serviceMap = { service };
        Mockito.when(description.getServices()).thenReturn(serviceMap);
        DescriptionElement descriptionElement = Mockito.mock(DescriptionElement.class);
        Mockito.when(description.toElement()).thenReturn(descriptionElement);

        APIIdentifier identifier = new APIIdentifier("P1_API1_v1.0.0");
        API api = new API(identifier);
        api.setTransports("HTTP");
        PowerMockito.mockStatic(APIUtil.class);
        PowerMockito.when(APIUtil.getGatewayendpoint(Mockito.anyString())).thenThrow(Exception.class);
        APIMWSDLReader apimwsdlReader = new APIMWSDLReader("some-invalid-url");
        apimwsdlReader.updateWSDL2(byteArray, api);
    }

    @Test( expected = APIManagementException.class)
    public void testUpdateWsdlException() throws Exception {
        byte[] byteArray = "sampleWsdl".getBytes();
        SOAP12AddressImpl soap12Address = Mockito.mock(SOAP12AddressImpl.class);
        SOAPAddressImpl soapAddress = Mockito.mock(SOAPAddressImpl.class);
        HTTPAddressImpl httpAddress = Mockito.mock(HTTPAddressImpl.class);
        Port port = Mockito.mock(Port.class);
        List<ExtensibilityElement> extensibilityElementList = new ArrayList<ExtensibilityElement>();
        extensibilityElementList.add(soap12Address);
        extensibilityElementList.add(soapAddress);
        extensibilityElementList.add(httpAddress);
        Map serviceMap = new HashMap();
        Map portMap = new HashMap();
        javax.wsdl.Service service = Mockito.mock(javax.wsdl.Service.class);
        serviceMap.put("service", service);
        portMap.put("port", port);

        WSDLReader reader = Mockito.mock(WSDLReader.class);
        WSDLWriter writer = Mockito.mock(WSDLWriter.class);
        WSDLFactory wsdlFactory = Mockito.mock(WSDLFactory.class);
        PowerMockito.mockStatic(WSDLFactory.class);
        PowerMockito.mockStatic(org.apache.woden.WSDLFactory.class);
        PowerMockito.when(WSDLFactory.newInstance()).thenReturn(wsdlFactory);
        Mockito.when(wsdlFactory.newWSDLReader()).thenReturn(reader);
        Mockito.when(wsdlFactory.newWSDLWriter()).thenReturn(writer);
        Definition definition = Mockito.mock(Definition.class);
        Mockito.doReturn(definition).when(reader).readWSDL(Mockito.anyString(), Mockito.any(InputSource.class));
        Mockito.when(definition.getAllServices()).thenReturn(serviceMap);
        Mockito.doNothing().when(writer).writeWSDL(Mockito.any(Definition.class), Mockito.any(OutputStream.class));
        PowerMockito.mockStatic(APIUtil.class);
        PowerMockito.when(APIUtil.getGatewayendpoint(Mockito.anyString())).thenReturn("endpoint");
        PowerMockito.when(APIUtil.buildOMElement(Mockito.any(ByteArrayInputStream.class)))
                .thenThrow(APIManagementException.class);

        Mockito.when(service.getPorts()).thenReturn(portMap);
        Mockito.when(port.getExtensibilityElements()).thenReturn(extensibilityElementList);
        Mockito.when(soap12Address.getLocationURI()).thenReturn("http://localhost.com/");
        Mockito.when(soapAddress.getLocationURI()).thenReturn("http://localhost.com/");
        Mockito.when(httpAddress.getLocationURI()).thenReturn("http://localhost.com/");
        APIIdentifier identifier = new APIIdentifier("P1_API1_v1.0.0");
        API api = new API(identifier);
        api.setTransports("HTTP");
        APIMWSDLReader apimwsdlReader = new APIMWSDLReader("some-invalid-url");
        apimwsdlReader.updateWSDL(byteArray, api);
    }

    @Test (expected = APIManagementException.class)
    public void testReadAndCleanWsdlException() throws Exception {
        WSDLReader reader = Mockito.mock(WSDLReader.class);
        WSDLFactory wsdlFactory = Mockito.mock(WSDLFactory.class);
        PowerMockito.mockStatic(WSDLFactory.class);
        PowerMockito.when(WSDLFactory.newInstance()).thenReturn(wsdlFactory);
        Mockito.when(wsdlFactory.newWSDLReader()).thenReturn(reader);
        Mockito.when(reader.readWSDL(Mockito.anyString())).thenThrow(WSDLException.class);
        PowerMockito.mockStatic(APIUtil.class);
        API api = Mockito.mock(API.class);
        APIMWSDLReader apimwsdlReader = new APIMWSDLReader("some-invalid-url");
        apimwsdlReader.readAndCleanWsdl(api);
    }

    @Test (expected = APIManagementException.class)
    public void testReadAndCleanWsdl2Exception() throws Exception {
        WSDLReader reader = Mockito.mock(WSDLReader.class);
        WSDLFactory wsdlFactory = Mockito.mock(WSDLFactory.class);
        PowerMockito.mockStatic(WSDLFactory.class);
        PowerMockito.mockStatic(org.apache.woden.WSDLFactory.class);
        PowerMockito.when(WSDLFactory.newInstance()).thenReturn(wsdlFactory);
        Mockito.when(wsdlFactory.newWSDLReader()).thenReturn(reader);
        org.apache.woden.WSDLFactory wFactory = Mockito.mock(org.apache.woden.WSDLFactory.class);
        PowerMockito.when(org.apache.woden.WSDLFactory.newInstance()).thenReturn(wFactory);
        org.apache.woden.WSDLReader wReader = Mockito.mock(org.apache.woden.WSDLReader.class);
        Mockito.when(wFactory.newWSDLReader()).thenThrow(WSDLException.class);
        API api = Mockito.mock(API.class);
        APIMWSDLReader apimwsdlReader = new APIMWSDLReader("some-invalid-url");
        apimwsdlReader.readAndCleanWsdl2(api);
    }
}
