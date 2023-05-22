package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class Function {

    public Function() {

    }

    public Function(String id, String coding, String name, String error, String sample_time, String value) {
        this.id = id;
        this.coding = coding;
        this.name = name;
        this.error = error;
        this.sample_time = sample_time;
        this.value = value;
    }

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "coding")
    private String coding;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "error")
    private String error;

    @XmlAttribute(name = "sample_time")
    private String sample_time;

    public String getSample_value() {
        return value;
    }

    @XmlValue
    private String value;
}
