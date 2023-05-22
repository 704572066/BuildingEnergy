package com.liqiang.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeValidate extends Validate {

    public TimeValidate() {

    }

    public TimeValidate(String operation, String time) {
        super(operation);
        this.time = time;
    }

    private String time;
}
