package com.dqsy.sparkvisualization.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 广告点击趋势
 *
 * @author liusinan
 */
@Data
public class AdTmp implements Serializable {

    private Object hour;
    private Object minute;
    private Object clickCount;

    public AdTmp(Object hour, Object minute, Object clickCount) {
        this.hour = hour;
        this.minute = minute;
        this.clickCount = clickCount;
    }

    public AdTmp() {
    }

    /*public Object getHour() {
        return hour;
    }

    public void setHour(Object hour) {
        this.hour = hour;
    }

    public Object getMinute() {
        return minute;
    }

    public void setMinute(Object minute) {
        this.minute = minute;
    }

    public Object getClickCount() {
        return clickCount;
    }

    public void setClickCount(Object clickCount) {
        this.clickCount = clickCount;
    }*/

    @Override
    public String toString() {
        return "{" +
                "hour=" + hour +
                ", minute=" + minute +
                ", clickCount=" + clickCount +
                '}';
    }
}
