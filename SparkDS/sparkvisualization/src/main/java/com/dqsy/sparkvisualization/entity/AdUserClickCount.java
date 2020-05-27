package com.dqsy.sparkvisualization.entity;

/**
 * 用户广告点击量
 *
 * @author liusinan
 */
public class AdUserClickCount {

    private String date;
    private Integer userid;
    private Integer adid;
    private Integer clickCount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getAdid() {
        return adid;
    }

    public void setAdid(Integer adid) {
        this.adid = adid;
    }

    public Integer getClickCount() {
        return clickCount;
    }

    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }
}
