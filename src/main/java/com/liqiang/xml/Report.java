package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class Report {

    public Report() {

    }

    public Report(Common common, ReplyData data) {
        this.common = common;
        this.data = data;
    }
    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "data")
    private ReplyData data;
}
