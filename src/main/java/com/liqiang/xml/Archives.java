package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class Archives {

    public Archives() {

    }

    public Archives(Common common, Validate validate) {
        this.common = common;
        this.validate = validate;
    }
    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "instruction")
    private Validate validate;
}
