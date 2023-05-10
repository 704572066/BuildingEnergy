package com.liqiang.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HeartResultValidate extends Validate {

    public HeartResultValidate() {

    }

    public HeartResultValidate(String operation, String heartResult) {
        super(operation);
        this.heartResult = heartResult;
    }

    private String heartResult;
}
