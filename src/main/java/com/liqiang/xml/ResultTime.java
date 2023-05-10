package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class ResultTime {

    public ResultTime() {

    }

    public ResultTime(Common common, ResultValidate id_validate) {
        this.common = common;
        this.id_validate = id_validate;
    }
    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "id_validate")
    private ResultValidate id_validate;


}
