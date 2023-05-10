package com.liqiang.xml;
import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Validate {

    public Validate() {

    }

    public Validate(String operation) {
        this.operation = operation;
    }

    @XmlAttribute(name = "operation")
    private String operation = "request";
}
