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

    public HeartResult(Common common, HeartBeat heart_beat) {
        this.common = common;
        this.heart_beat = heart_beat;
    }

    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "heart_beat")
    private HeartBeat heart_beat;
}
