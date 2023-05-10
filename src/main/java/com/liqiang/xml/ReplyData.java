package com.liqiang.xml;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "common")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReplyData extends Data {

    public ReplyData() {

    }

    public ReplyData(String sequence, String parse, String time, String operation, List<Meter> meterList) {
        super(operation);
        this.sequence = sequence;
        this.parse = parse;
        this.time = time;
        this.meterList = meterList;
    }

    private String sequence;

    private String parse;

    private String time;

    @XmlElement(name ="data")
    private List<Meter> meterList ;

//    @XmlAttribute(name = "operation")
//    private String operation;

}
