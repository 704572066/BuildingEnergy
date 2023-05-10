package com.liqiang.xml;

import lombok.Getter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "common")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class Common {

    public Common() {

    }
    public Common(String building_id, String gateway_id, List<String> typeList) {
        this.building_id = building_id;
        this.gateway_id = gateway_id;
        this.typeList = typeList;
    }

    private String building_id = "330400A001";

    private String gateway_id = "01";

//    private String type = "request";

    @XmlElement(name ="type")
    private List<String> typeList ;


//    private String id_validate;
}
