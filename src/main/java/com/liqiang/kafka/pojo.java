package com.liqiang.kafka;

// 一定要包含空构造函数才行
public class pojo {
    public String user_id;
    public String name;
    public String sex;
    public String money;
 
    public pojo(String user_id, String name, String sex, String money) {
        this.user_id = user_id;
        this.name = name;
        this.sex = sex;
        this.money = money;
    }
    public pojo() {
 
    }
    public String getUser_id() {
        return user_id;
    }
 
    public String getName() {
        return name;
    }
 
    public String getSex() {
        return sex;
    }
 
    public String getMoney() {
        return money;
    }
 
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setSex(String sex) {
        this.sex = sex;
    }
 
    public void setMoney(String money) {
        this.money = money;
    }
 
}