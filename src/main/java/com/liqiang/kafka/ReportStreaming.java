package com.liqiang.kafka;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class ReportStreaming {

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(ReportStreaming.class);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(10000);
        env.setParallelism(1);
        EnvironmentSettings build = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, build);
        tableEnv.createTemporarySystemFunction("parserJsonArrayTest", new ParserJsonArrayTest());
        String sql =
                "CREATE TABLE t_user (\n" +
                        "common ROW<building_id STRING,gateway_id STRING>,\n" +
                        "data ROW<parser STRING,send_time STRING,sequence int,meterList STRING>\n" +
                        ")\n" +
                        "WITH\n" +
                        "(\n" +
                        "'connector' = 'kafka',\n" +
                        "'topic' = 'flink_streaming_ali_kafka',\n" +
                        "'properties.bootstrap.servers' = '101.37.32.216:9092',\n" +
                        "'scan.startup.mode' = 'latest-offset',\n" +
                        "'properties.group.id' = 'group_2',\n" +
                        "'format' = 'json')\n";
        tableEnv.executeSql(sql);
//        Table t_user = tableEnv.from("t_user");
//        t_user.printSchema();
//        DataStream<pojo> pojoDataStream = tableEnv.toAppendStream(t_user,pojo.class);
//        pojoDataStream.print();
        String sql2 =
                "CREATE TABLE t_meter_record (\n" +
                        "id VARCHAR(40) NOT NULL PRIMARY KEY,\n" +
                        "common_building_id STRING,\n" +
                        "common_gateway_id STRING,\n" +

                        "data_parser STRING,\n" +
                        "data_sequence int,\n" +
                        "data_time TIMESTAMP,\n" +

                        "data_meter_conn STRING,\n" +
                        "data_meter_id int,\n" +
                        "data_meter_name STRING,\n" +

                        "data_meter_function_coding STRING,\n" +
                        "data_meter_function_error int,\n" +
                        "data_meter_function_id STRING,\n" +
                        "data_meter_function_name STRING,\n" +
                        "data_meter_function_sample_time TIMESTAMP,\n" +
                        "data_meter_function_value double\n" +
//                        "receive_time TIMESTAMP,\n" +
//                        "PRIMARY KEY (id) NOT ENFORCED\n"+
                        ")\n" +
                        " WITH\n" +
                        "(\n" +
                        "'connector' = 'jdbc',\n" +
                        "'url' = 'jdbc:mysql://localhost:3306/db_energy?serverTimezone=Asia/Shanghai&zeroDaeTimeBehavior=convertToNull&useSSL=false',\n" +
                        "'driver' = 'com.mysql.jdbc.Driver',\n" +
                        "'username' = 'root',\n" +
                        "'password' = '123456',\n" +
                        "'table-name' = 't_meter_record',\n" +
                        "'lookup.cache.max-rows' = '100',\n"+
                        "'lookup.cache.ttl' = '60000'\n"+
                        ")";
        tableEnv.executeSql(sql2);
//        Table mysql_user = tableEnv.from("mysql_user");
//        mysql_user.printSchema();
//        TO_TIMESTAMP(timestamp3,'yyyyMMddHHmmss')as var3
//        String insert = "insert into t_meter_record_copy(common_building_id,common_gateway_id,data_meter_name,data_meter_function_name) select common.building_id as common_building_id,common.gateway_id as common_gateway_id,data.meterList[1].name as data_meter_name,data.meterList[1].functionList[1].name as data_meter_function_name from t_user ";
        String insert = "insert into t_meter_record(id,common_building_id,common_gateway_id,data_parser,data_sequence,data_time,data_meter_conn,data_meter_id,data_meter_name,data_meter_function_coding,data_meter_function_error,data_meter_function_id,data_meter_function_name,data_meter_function_sample_time,data_meter_function_value) " +
                "select UUID(),common.building_id as common_building_id,common.gateway_id as common_gateway_id,data.parser as data_parser,data.sequence as data_sequence,TO_TIMESTAMP(data.send_time,'yyyyMMddHHmmss') as data_time,data_meter_conn,data_meter_id,data_meter_name,data_meter_function_coding,data_meter_function_error,data_meter_function_id,data_meter_function_name,TO_TIMESTAMP(data_meter_function_sample_time,'yyyyMMddHHmmss') as data_meter_function_sample_time,data_meter_function_value " +
                "from t_user,lateral TABLE (ParserJsonArrayTest(data.meterList)) AS t (data_meter_conn,data_meter_id,data_meter_name,data_meter_function_coding,data_meter_function_error,data_meter_function_id,data_meter_function_name,data_meter_function_sample_time,data_meter_function_value)";

        tableEnv.executeSql(insert).print();
        try {
            env.execute("flink_running");
        } catch (Exception e) {
            log.info("抛出异常！");
            System.out.println(e.getMessage());
        }
    }



}
