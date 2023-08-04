package com.liqiang.xml.yaquan;

import com.liqiang.xml.Common;
import com.liqiang.xml.HeartBeat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class YaquanHeartResult {

    public YaquanHeartResult() {

    }

    public YaquanHeartResult(Common common, HeartBeat heart_beat) {
        this.common = common;
        this.heart_beat = heart_beat;
    }

    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "id_validate")
    private HeartBeat heart_beat;
}
