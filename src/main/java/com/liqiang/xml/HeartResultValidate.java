package com.liqiang.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HeartResultValidate extends Validate {

    public HeartResultValidate() {

    }

    public HeartResultValidate(String operation, String heart_result) {
        super(operation);
        this.heart_result = heart_result;
    }

    private String heart_result;
}
