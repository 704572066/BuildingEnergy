package com.liqiang.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HeartBeat {

    public HeartBeat() {

    }

    public HeartBeat(String operation, String time) {
        this.operation = operation;
        this.time = time;
    }

    @XmlAttribute(name = "operation")
    private String operation = "notify";

    private String time;
}
