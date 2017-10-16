/*
*  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.apimgt.hostobjects.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

public class JsonDeserializationTest {

    @Test
    public void testDeserializePetStoreFile() throws Exception {
  //This need to uncomment after adding changes.
        /*String path =
                "src" + File.separator + "test" + File.separator + "resources" + File.separator + "swagger"
                        + File.separator + "Pet.json";
        ObjectMapper objectMapper =ObjectMapperFactory.createJson();
        JsonParser jsonParser = new JsonFactory().createParser(new File(path));
        ObjectCodec objectCodec = Mockito.mock(ObjectCodec.class);
        JsonNode jsonNode = Mockito.mock(JsonNode.class);
        Mockito.when(objectCodec.readTree(jsonParser)).thenReturn();
        PropertyDeserializer propertyDeserializer = new PropertyDeserializer();
        DefaultDeserializationContext deserializationContext = Mockito.mock(DefaultDeserializationContext.class);
        DeserializationContext ctxt = new DefaultDeserializationContext(deserializationContext) {
            @Override
            public DefaultDeserializationContext with(DeserializerFactory deserializerFactory) {
                return null;
            }

            @Override
            public DefaultDeserializationContext createInstance(DeserializationConfig deserializationConfig, JsonParser jsonParser, InjectableValues injectableValues) {
                return null;
            }
        };
        propertyDeserializer.deserialize(jsonParser, ctxt);
    */
    }
}
