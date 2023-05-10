package com.liqiang.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class Query {

    public Query() {

    }

    public Query(Common common, Data data) {
        this.common = common;
        this.data = data;
    }
    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "data")
    private Data data;
}
