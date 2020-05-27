package com.dqsy.sparkvisualization.entity;

/**
 * 各省top3热门广告
 *
 * @author liusinan
 */
public class AdProvinceTop3 {

    private String date;
    private String province;
    private Integer adid;
    private Integer clickCount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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
