package com.dqsy.sparkproject.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * 广告黑名单
 *
 * @author liusinan
 */
public class AdBlacklist implements Serializable {

    private long userid;

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public AdBlacklist(long userid) {
        this.userid = userid;
    }

    public AdBlacklist() {
    }

    //定制AdBlackList实例的相等规则
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdBlacklist that = (AdBlacklist) o;
        return userid == that.userid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid);
    }

    @Override
    public String toString() {
        return "AdBlacklist{" +
                "userid=" + userid +
                '}';
    }
}
