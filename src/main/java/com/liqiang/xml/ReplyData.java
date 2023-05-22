package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "common")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class ReplyData extends Data {

    public ReplyData() {

    }

    public ReplyData(String sequence, String parser, String time, String operation, List<Meter> meterList) {
        super(operation);
        this.sequence = sequence;
        this.parser = parser;
        this.time = time;
        this.meterList = meterList;
    }

    private String sequence;

    private String parser;

    public String getSend_time() {
        return time;
    }

    private String time;

    @XmlElement(name ="meter")
    private List<Meter> meterList ;

//    @XmlAttribute(name = "operation")
//    private String operation;



}
