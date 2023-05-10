package com.liqiang.xml;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class Request {

    public Request() {

    }

    public Request(Common common, Validate id_validate) {
        this.common = common;
        this.id_validate = id_validate;
    }
    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "id_validate")
    private Validate id_validate;
}
