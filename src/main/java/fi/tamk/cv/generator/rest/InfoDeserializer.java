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
import fi.tamk.cv.generator.model.datatypes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
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
        log.debug(node.toString());
        Info info = new Info();
        info.setOrder((Integer) node.get("order").numberValue());
        info.setVisible(node.get("visible").booleanValue());
        ArrayNode arrayNode = (ArrayNode) node.get("data");
        for(JsonNode data:arrayNode){
            DataType dataType;
            if(data.get("type").toString().replace("\"","").equalsIgnoreCase("course")){
                //log.debug("Data type: Course, {}",data.toString());
                dataType = mapper.readValue(data.toString(), Course.class);
            } else if(data.get("type").toString().replace("\"","").equalsIgnoreCase("education")){
                //log.debug("Data type: Education, {}",data.toString());
                dataType = mapper.readValue(data.toString(), Education.class);
            } else {
                if(data.get("name") != null && data.get("value") != null){
                    //log.debug("Data type: Misc, {}",data.toString());
                    dataType = mapper.readValue(data.toString(), Misc.class);
                } else if (data.get("completion_date") != null){
                    //log.debug("Data type: Project, {}",data.toString());
                    dataType = mapper.readValue(data.toString(), Project.class);
                } else if(data.get("title") != null && data.size() <= 4){
                    //log.debug("Data type: Title, {}",data.toString());
                    dataType = mapper.readValue(data.toString(), Title.class);
                } else if (data.get("name") != null && (data.get("contact_email") != null || data.get("contact_phone") != null)){
                    //log.debug("Data type: Person, {}",data.toString());
                    dataType = mapper.readValue(data.toString(), Person.class);
                } else if(data.get("title") != null && data.get("startdate") != null) {
                    //log.debug("Data type: Experience, {}",data.toString());
                    dataType = mapper.readValue(data.toString(), Experience.class);
                } else {
                    log.debug("Data type: Unknown, {}",data.toString());
                    dataType = mapper.readValue(data.toString(), Misc.class);
                }
            }
            info.getData().add(dataType);
        }
        return info;
    }
}
