package com.dqsy.sparkproject.model;

/**
 *  用户广告点击量查询结果
 * @author liusinan
 */
public class AdUserClickCountQueryResult {

    private int clickCount;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }
}
