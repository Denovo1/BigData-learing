package com.dqsy.sparkvisualization.entity;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName AdProvinceTop3Temp.java
 * @Description TODO
 * @createTime 2020年04月04日 15:19:00
 */
public class AdProvinceTop3Temp {

    private String province;
    private String adid;

    public AdProvinceTop3Temp(String province, String adid) {
        this.province = province;
        this.adid = adid;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }
}
