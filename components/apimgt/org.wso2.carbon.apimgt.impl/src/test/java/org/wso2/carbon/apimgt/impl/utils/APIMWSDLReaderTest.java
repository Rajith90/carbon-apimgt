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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;

import org.apache.axiom.om.OMElement;
import org.apache.woden.wsdl20.Endpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.model.API;

@RunWith(PowerMockRunner.class) @PrepareForTest({ APIMWSDLReader.class, WSDLFactory.class, APIUtil.class,
        org.apache.woden.WSDLFactory.class, org.apache.woden.wsdl20.Service.class, URI.class, Endpoint.class })

public class APIMWSDLReaderTest {

    private APIMWSDLReader apimwsdlReader;
    private WSDLWriter wsdlWriter;
    private WSDLReader reader;
    private Definition definition;

    @Test public void testReadAndCleanWsdl() throws Exception {
        reader = Mockito.mock(WSDLReader.class);
        wsdlWriter = Mockito.mock(WSDLWriter.class);
        WSDLFactory wsdlFactory = Mockito.mock(WSDLFactory.class);
        definition = Mockito.mock(Definition.class);
        OMElement omElement = Mockito.mock(OMElement.class);
        PowerMockito.mockStatic(APIUtil.class);
        PowerMockito.mockStatic(WSDLFactory.class);
        PowerMockito.when(WSDLFactory.newInstance()).thenReturn(wsdlFactory);
        Mockito.when(wsdlFactory.newWSDLReader()).thenReturn(reader);
        Mockito.when(wsdlFactory.newWSDLWriter()).thenReturn(wsdlWriter);
        Mockito.doNothing().when(wsdlWriter)
                .writeWSDL(Mockito.any(Definition.class), Mockito.any(ByteArrayOutputStream.class));
        Mockito.when(APIUtil.buildOMElement(Mockito.any(InputStream.class))).thenReturn(omElement);
        API api = Mockito.mock(API.class);
        Mockito.when(reader.readWSDL(Mockito.anyString())).thenReturn(definition);
        apimwsdlReader = new APIMWSDLReader("http://localhost.com/");
        apimwsdlReader.readAndCleanWsdl(api);
        Mockito.verify(APIUtil.buildOMElement(Mockito.any(InputStream.class)), Mockito.times(1));
    }

}
