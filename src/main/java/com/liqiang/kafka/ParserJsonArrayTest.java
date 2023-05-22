package com.liqiang.kafka;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;

import java.util.Iterator;

@FunctionHint(output = @DataTypeHint("ROW<data_meter_conn STRING,data_meter_id int,data_meter_name STRING,data_meter_function_coding STRING,data_meter_function_error int,data_meter_function_id STRING,data_meter_function_name STRING,data_meter_function_sample_time STRING,data_meter_function_value double>"))
public class ParserJsonArrayTest extends TableFunction<Row> {

//        private static final Logger log = Logger.getLogger(ParserJsonArrayTest.class);

    public void eval(String value) {
        try {
            JSONArray meters = JSONArray.parseArray(value);
            Iterator<Object> iterator = meters.iterator();
            while (iterator.hasNext()) {
                JSONObject jsonObject = (JSONObject) iterator.next();
                String data_meter_name = jsonObject.getString("name");
                String data_meter_conn = jsonObject.getString("conn");
                int data_meter_id = jsonObject.getInteger("id");
//                    String url = jsonObject.getString("url");
                JSONArray functions = jsonObject.getJSONArray("functionList");
                Iterator<Object> function_iterator = functions.iterator();
                while (function_iterator.hasNext()) {
                    JSONObject function_jsonObject = (JSONObject) function_iterator.next();
                    String data_meter_function_name = function_jsonObject.getString("name");
                    String data_meter_function_coding = function_jsonObject.getString("coding");
                    int data_meter_function_error = function_jsonObject.getInteger("error");
                    String data_meter_function_id = function_jsonObject.getString("id");
                    String data_meter_function_sample_time = function_jsonObject.getString("sample_time");
                    double data_meter_function_value = function_jsonObject.getDoubleValue("sample_value");
                    collect(Row.of(data_meter_conn,data_meter_id,data_meter_name,data_meter_function_coding,data_meter_function_error,data_meter_function_id,data_meter_function_name,data_meter_function_sample_time,data_meter_function_value));
                }

            }
        } catch (Exception e) {
//                log.error("parser json failed :" + e.getMessage());
        }
    }
}
