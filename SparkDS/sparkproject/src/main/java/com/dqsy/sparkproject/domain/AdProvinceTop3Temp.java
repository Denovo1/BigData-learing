package com.dqsy.sparkproject.domain;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName AdProvinceTop3Temp.java
 * @Description TODO
 * @createTime 2020年04月04日 15:19:00
 */
public class AdProvinceTop3Temp {

    /**
     * 要删除的广告类型数
     */
    private int delCnt;

    /**
     * 省份
     */
    private String province;

    public int getDelCnt() {
        return delCnt;
    }

    public void setDelCnt(int delCnt) {
        this.delCnt = delCnt;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
