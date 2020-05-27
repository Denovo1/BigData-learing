package com.dqsy.sparkvisualization.entity;

/**
 * 各区域top3热门商品
 *
 * @author Administrator
 */
public class AreaTop3Product {

    private long taskid;
    private String area;
    private String areaLevel;
    private long productid;
    private String cityInfos;
    private long clickCount;
    private String productName;
    private String productStatus;

    public AreaTop3Product(long taskid, String area, String areaLevel, long productid, String cityInfos, long clickCount, String productName, String productStatus) {
        this.taskid = taskid;
        this.area = area;
        this.areaLevel = areaLevel;
        this.productid = productid;
        this.cityInfos = cityInfos;
        this.clickCount = clickCount;
        this.productName = productName;
        this.productStatus = productStatus;
    }

    public long getTaskid() {
        return taskid;
    }

    public void setTaskid(long taskid) {
        this.taskid = taskid;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(String areaLevel) {
        this.areaLevel = areaLevel;
    }

    public long getProductid() {
        return productid;
    }

    public void setProductid(long productid) {
        this.productid = productid;
    }

    public String getCityInfos() {
        return cityInfos;
    }

    public void setCityInfos(String cityInfos) {
        this.cityInfos = cityInfos;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

}
