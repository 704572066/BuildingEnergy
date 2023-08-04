package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class Device extends Validate {

    public Device() {

    }

    public Device(String operation, String device_ack) {
        super(operation);
        this.device_ack = device_ack;
    }

    private String device_ack;
}
