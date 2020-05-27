package com.dqsy.sparkvisualization.entity;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName Tmp.java
 * @Description TODO
 * @createTime 2020年04月01日 21:10:00
 */
public class Tmp {

    private String name;
    private Object value;

    public Tmp(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
