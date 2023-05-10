package com.liqiang.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class MD5 {

    public MD5() {

    }

    public MD5(Common common, MD5Validate id_validate) {
        this.common = common;
        this.id_validate = id_validate;
    }
    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "id_validate")
    private MD5Validate id_validate;
}
