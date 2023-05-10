package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class Meter {

    public Meter() {

    }

    public Meter(String id, String name, String conn, List<Function> functionList) {
        this.id = id;
        this.name = name;
        this.conn = conn;
        this.functionList = functionList;
    }

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "conn")
    private String conn;

    @XmlElement(name ="function")
    private List<Function> functionList ;
}
