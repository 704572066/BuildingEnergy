package com.liqiang.kafka;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStreaming {

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(DataStreaming.class);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        EnvironmentSettings build = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, build);
        String sql =
                "CREATE TABLE t_user (\n" +
                        "user_id STRING,\n" +
                        "name STRING,\n" +
                        "sex STRING,\n" +
                        "money STRING\n" +
                        ")\n" +
                        "WITH\n" +
                        "(\n" +
                        "'connector' = 'kafka',\n" +
                        "'topic' = 'flink_streaming',\n" +
                        "'properties.bootstrap.servers' = 'localhost:9092',\n" +
                        "'scan.startup.mode' = 'latest-offset',\n" +
                        "'properties.group.id' = 'group_1',\n" +
                        "'format' = 'json')\n";
        tableEnv.executeSql(sql);
//        Table t_user = tableEnv.from("t_user");
//        t_user.printSchema();
//        DataStream<pojo> pojoDataStream = tableEnv.toAppendStream(t_user,pojo.class);
//        pojoDataStream.print();
        String sql2 =
                "CREATE TABLE mysql_user (\n" +
                        "user_id STRING,\n" +
                        "name STRING,\n" +
                        "sex STRING,\n" +
                        "money STRING,\n" +
                        "PRIMARY KEY (user_id) NOT ENFORCED\n"+
                        ")\n" +
                        " WITH\n" +
                        "(\n" +
                        "'connector' = 'jdbc',\n" +
                        "'url' = 'jdbc:mysql://localhost:3306/db_energy?serverTimezone=Asia/Shanghai&zeroDaeTimeBehavior=convertToNull&useSSL=false',\n" +
                        "'driver' = 'com.mysql.jdbc.Driver',\n" +
                        "'username' = 'root',\n" +
                        "'password' = '123456',\n" +
                        "'table-name' = 'mysql_user',\n" +
                        "'lookup.cache.max-rows' = '100',\n"+
                        "'lookup.cache.ttl' = '60000'\n"+
                        ")";
        tableEnv.executeSql(sql2);
//        Table mysql_user = tableEnv.from("mysql_user");
//        mysql_user.printSchema();
        String insert = "insert into mysql_user select * from t_user ";
        tableEnv.executeSql(insert).print();
        try {
            env.execute("flink_running");
        } catch (Exception e) {
            log.info("抛出异常！");
            System.out.println(e.getMessage());
        }
    }

}
