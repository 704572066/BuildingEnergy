package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class MD5Validate extends Validate {

    public MD5Validate() {

    }

    public MD5Validate(String operation, String md5) {
        super(operation);
        this.md5 = md5;
    }

    private String md5;
}
