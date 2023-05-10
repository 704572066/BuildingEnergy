package com.liqiang.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Data {

    public Data() {

    }

    public Data(String operation) {
        this.operation = operation;
    }

    @XmlAttribute(name = "operation")
    private String operation;
}
