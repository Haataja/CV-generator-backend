/*
Copyright 2019 Hanna Haataja <hanna.haataja@tuni.fi>. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package fi.tamk.cv.generator.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fi.tamk.cv.generator.model.Info;
import fi.tamk.cv.generator.model.datatypes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class will take care of the deserialization of {@link DataType} to different classes.
 * If countering errors about receiving posts with {@link Info} as a body, check for errors here.
 */
public class InfoDeserializer extends JsonDeserializer<Info> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Info deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = p.getCodec().readTree(p);
        log.trace(node.toString());
        Info info = new Info();
        info.setOrder((Integer) node.get("order").numberValue());
        info.setVisible(node.get("visible").booleanValue());
        ArrayNode arrayNode = (ArrayNode) node.get("data");
        for (JsonNode data : arrayNode) {
            DataType dataType;
            if (data.get("type").toString().replace("\"", "").equalsIgnoreCase("course")) {
                //log.debug("Data type: Course, {}",data.toString());
                dataType = mapper.readValue(data.toString(), Course.class);
            } else if (data.get("type").toString().replace("\"", "").equalsIgnoreCase("education")) {
                //log.debug("Data type: Education, {}",data.toString());
                dataType = mapper.readValue(data.toString(), Education.class);
            } else if (data.get("type").toString().replace("\"", "").equalsIgnoreCase("title")) {
                dataType = mapper.readValue(data.toString(), Title.class);
            } else {
                if (data.get("name") != null && data.get("value") != null) {
                    //log.debug("Data type: Misc, {}",data.toString());
                    dataType = mapper.readValue(data.toString(), Misc.class);
                    log.debug("MISC: name: {}, value: {}", ((Misc) dataType).getName(), ((Misc) dataType).getValue());
                } else if (data.get("completion_date") != null) {
                    //log.debug("Data type: Project, {}",data.toString());
                    dataType = mapper.readValue(data.toString(), Project.class);
                } else if (data.get("name") != null && (data.get("contact_email") != null || data.get("contact_phone") != null)) {
                    //log.debug("Data type: Person, {}",data.toString());
                    dataType = mapper.readValue(data.toString(), Person.class);
                } else if (data.get("title") != null && data.get("startdate") != null) {
                    //log.debug("Data type: Experience, {}",data.toString());
                    dataType = mapper.readValue(data.toString(), Experience.class);
                } else {
                    log.debug("Data type: Unknown, {}", data.toString());
                    dataType = mapper.readValue(data.toString(), Misc.class);
                    log.debug("name: {}, value: {}", ((Misc) dataType).getName(), ((Misc) dataType).getValue());
                }
            }
            info.getData().add(dataType);
        }
        return info;
    }
}
