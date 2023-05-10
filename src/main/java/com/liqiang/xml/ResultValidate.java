package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class ResultValidate extends Validate {

    public ResultValidate() {

    }

    public ResultValidate(String operation, String result, String time) {
        super(operation);
        this.result = result;
        this.time = time;
    }

    private String result;

    private String time;
}
