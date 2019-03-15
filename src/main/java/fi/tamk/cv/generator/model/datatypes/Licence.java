package fi.tamk.cv.generator.model.datatypes;

public class Licence extends DataType{
    private String value;
    private String name;

    public Licence(String type, String value, String name) {
        super(type);
        this.value = value;
        this.name = name;
    }

    public Licence(String type, long id, boolean visible, String value, String name) {
        super(type, id, visible);
        this.value = value;
        this.name = name;
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
