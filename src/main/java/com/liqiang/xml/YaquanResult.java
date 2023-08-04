package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class YaquanResult {

    public YaquanResult() {

    }

    public YaquanResult(Common common, YaquanResultValidate id_validate) {
        this.common = common;
        this.id_validate = id_validate;
    }
    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "id_validate")
    private YaquanResultValidate id_validate;


}
