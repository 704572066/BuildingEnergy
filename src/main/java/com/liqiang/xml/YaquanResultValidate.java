package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class YaquanResultValidate extends Validate {

    public YaquanResultValidate() {

    }

    public YaquanResultValidate(String operation, String result) {
        super(operation);
        this.result = result;
    }

    private String result;
}
