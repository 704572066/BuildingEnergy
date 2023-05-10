package com.liqiang.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class Notify {

    public Notify() {

    }
    public Notify(Common common) {
        this.common = common;
    }

    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "heart_beat")
    private HeartBeat heart_beat;
}
