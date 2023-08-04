package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class DeviceACK {

    public DeviceACK() {

    }

    public DeviceACK(Common common, Device device) {
        this.common = common;
        this.device = device;
    }
    @XmlElement(name = "common")
    private Common common;

    @XmlElement(name = "device")
    private Device device;
}
