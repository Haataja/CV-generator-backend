package fi.tamk.cv.generator.model.datatypes;

import java.util.Arrays;
import java.util.List;

public class Misc extends DataType{
    private String value;
    private String name;

    public Misc(){
        super();
    }

    public Misc(String type, String value, String name) {
        super(type);
        this.value = value;
        this.name = name;
    }

    public Misc(String type, long id, boolean visible, String value, String name) {
        super(type, id, visible);
        this.value = value;
        this.name = name;
    }

    public List<Object> toList(){
        return Arrays.asList(getType(),getId(),isVisible(),getValue(),getName());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
