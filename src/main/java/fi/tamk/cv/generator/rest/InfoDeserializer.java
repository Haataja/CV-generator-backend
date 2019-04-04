package fi.tamk.cv.generator.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import fi.tamk.cv.generator.model.Info;
import fi.tamk.cv.generator.model.datatypes.Course;
import fi.tamk.cv.generator.model.datatypes.DataType;
import fi.tamk.cv.generator.model.datatypes.Education;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.IOException;

public class InfoDeserializer extends JsonDeserializer<Info> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Info deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = p.getCodec().readTree(p);
        log.debug(node.toString());
        Info info = new Info();
        info.setOrder((Integer) node.get("order").numberValue());
        info.setVisible(node.get("visible").booleanValue());
        ArrayNode arrayNode = (ArrayNode) node.get("data");
        for(JsonNode data:arrayNode){
            log.debug("Data type: {}, {}", data.get("type").toString(),data.toString());
            DataType dataType;
            if(data.get("type").toString().replace("\"","").equalsIgnoreCase("course")){
                dataType = mapper.readValue(data.toString(), Course.class);
            } else if(data.get("type").toString().replace("\"","").equalsIgnoreCase("education")){
                dataType = mapper.readValue(data.toString(), Education.class);
            } else {
                log.debug("null");
                dataType = null;
            }
            info.getData().add(dataType);
        }
        return info;
    }
}
