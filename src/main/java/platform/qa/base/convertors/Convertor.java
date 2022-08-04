/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.base.convertors;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class Convertor {

    public static XmlMapper getXmlObjectMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        xmlMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        return xmlMapper;
    }

    /**
     * Returns converted XML file to POJO.
     * XML file - not using whole XML file, taking just a part of it starting from {@param attributeName}
     * @param file - file to convert
     * @param attributeName - attribute name to start converting XML file from
     * @param clazzValue - converted POJO type
     * @return POJO representation of XML file
     */
    @SneakyThrows(IOException.class)
    public static <T> T convertPartOfXmlFileToObject(File file, String attributeName, Class<T> clazzValue) {
        JsonNode jsonNode = getXmlObjectMapper().readValue(file, JsonNode.class).get(attributeName);
        return getXmlObjectMapper().convertValue(jsonNode, clazzValue);
    }
}
