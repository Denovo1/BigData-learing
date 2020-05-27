package com.dqsy.sparkvisualization.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 广告黑名单
 *
 * @author liusinan
 */
public class AdBlacklist implements Serializable {

    private Integer userid;
    private String date;

    public AdBlacklist() {

    }

    public AdBlacklist(Integer userid, String date) {
        this.userid = userid;
        this.date = date;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
