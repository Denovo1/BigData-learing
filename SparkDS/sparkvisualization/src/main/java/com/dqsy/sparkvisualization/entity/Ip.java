package com.dqsy.sparkvisualization.entity;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName Ip.java
 * @Description TODO
 * @createTime 2020年04月23日 14:34:00
 */
public class Ip {
    private String code;
    private String ip;
    private String address;
    private String date;

    public Ip() {
    }

    public Ip(String code, String ip, String address, String date) {
        this.code = code;
        this.ip = ip;
        this.address = address;
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
