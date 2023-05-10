package com.liqiang.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class HeartResult {

    public HeartResult() {

    }

    public HeartResult(Common common, HeartResultValidate id_validate) {
        this.common = common;
        this.id_validate = id_validate;
    }

    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "id_validate")
    private HeartResultValidate id_validate;
}
