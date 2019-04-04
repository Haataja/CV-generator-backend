package fi.tamk.cv.generator.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import fi.tamk.cv.generator.model.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InfoDeserializer extends JsonDeserializer<Info> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Info deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        log.debug(node.toString());
        Info info = new Info();
        info.setOrder((Integer) node.get("order").numberValue());
        info.setVisible(node.get("visible").booleanValue());
        ArrayNode arrayNode = (ArrayNode) node.get("data");
        for(JsonNode data:arrayNode){
            log.debug(data.toString());
        }
        return info;
    }
}
