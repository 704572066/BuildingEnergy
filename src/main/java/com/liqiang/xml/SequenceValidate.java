package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class SequenceValidate extends Validate {

    public SequenceValidate() {

    }

    public SequenceValidate(String operation, String sequence) {
        super(operation);
        this.sequence = sequence;
    }

    private String sequence;
}
