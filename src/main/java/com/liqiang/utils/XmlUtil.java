package com.liqiang.utils;

//import org.apache.poi.ss.formula.functions.T;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlUtil {

    public static String beanToXml(Object obj, String encoding) throws JAXBException {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * xml字符串转javabean
     * @param xmlStr
     * @param clazz
     * @param <T>
     * @return
     * @throws JAXBException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> T  XMLToJavaBean(String xmlStr,Class<T> clazz) throws JAXBException, InstantiationException, IllegalAccessException {

        // JAXB的上下文环境
        JAXBContext context = JAXBContext.newInstance(clazz);
        // 创建 UnMarshaller 实例
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T)unmarshaller.unmarshal(new StringReader(xmlStr));
    }
}
