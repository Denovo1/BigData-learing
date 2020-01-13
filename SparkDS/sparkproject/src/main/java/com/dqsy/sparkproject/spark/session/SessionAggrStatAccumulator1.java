package com.dqsy.sparkproject.spark.session;

import com.dqsy.sparkproject.constant.Constants;
import com.dqsy.sparkproject.util.StringUtils;
import org.apache.spark.util.AccumulatorV2;

/**
 * session聚合统计Accumulator
 * <p>
 * 大家可以看到
 * 其实使用自己定义的一些数据格式，比如String，甚至说，我们可以自己定义model，自己定义的类（必须可序列化）
 * 然后呢，可以基于这种特殊的数据格式，可以实现自己复杂的分布式的计算逻辑
 * 各个task，分布式在运行，可以根据你的需求，task给Accumulator传入不同的值
 * 根据不同的值，去做复杂的逻辑
 * <p>
 * Spark Core里面很实用的高端技术
 *
 * @author Administrator
 */
public class SessionAggrStatAccumulator1 extends AccumulatorV2<String, String> {

    private static final long serialVersionUID = 6311074555136039130L;

    private String result = Constants.SESSION_COUNT + "=0|"
            + Constants.TIME_PERIOD_1s_3s + "=0|"
            + Constants.TIME_PERIOD_4s_6s + "=0|"
            + Constants.TIME_PERIOD_7s_9s + "=0|"
            + Constants.TIME_PERIOD_10s_30s + "=0|"
            + Constants.TIME_PERIOD_30s_60s + "=0|"
            + Constants.TIME_PERIOD_1m_3m + "=0|"
            + Constants.TIME_PERIOD_3m_10m + "=0|"
            + Constants.TIME_PERIOD_10m_30m + "=0|"
            + Constants.TIME_PERIOD_30m + "=0|"
            + Constants.STEP_PERIOD_1_3 + "=0|"
            + Constants.STEP_PERIOD_4_6 + "=0|"
            + Constants.STEP_PERIOD_7_9 + "=0|"
            + Constants.STEP_PERIOD_10_30 + "=0|"
            + Constants.STEP_PERIOD_30_60 + "=0|"
            + Constants.STEP_PERIOD_60 + "=0";

    @Override
    public boolean isZero() {
        String newResult = Constants.SESSION_COUNT + "=0|"
                + Constants.TIME_PERIOD_1s_3s + "=0|"
                + Constants.TIME_PERIOD_4s_6s + "=0|"
                + Constants.TIME_PERIOD_7s_9s + "=0|"
                + Constants.TIME_PERIOD_10s_30s + "=0|"
                + Constants.TIME_PERIOD_30s_60s + "=0|"
                + Constants.TIME_PERIOD_1m_3m + "=0|"
                + Constants.TIME_PERIOD_3m_10m + "=0|"
                + Constants.TIME_PERIOD_10m_30m + "=0|"
                + Constants.TIME_PERIOD_30m + "=0|"
                + Constants.STEP_PERIOD_1_3 + "=0|"
                + Constants.STEP_PERIOD_4_6 + "=0|"
                + Constants.STEP_PERIOD_7_9 + "=0|"
                + Constants.STEP_PERIOD_10_30 + "=0|"
                + Constants.STEP_PERIOD_30_60 + "=0|"
                + Constants.STEP_PERIOD_60 + "=0";
        return this.result == newResult;
    }

    @Override
    public AccumulatorV2<String, String> copy() {
        SessionAggrStatAccumulator1 sessionAggrStatAccumulator = new SessionAggrStatAccumulator1();
        sessionAggrStatAccumulator.result = this.result;
        return sessionAggrStatAccumulator;
    }

    @Override
    public void reset() {
        String newResult = Constants.SESSION_COUNT + "=0|"
                + Constants.TIME_PERIOD_1s_3s + "=0|"
                + Constants.TIME_PERIOD_4s_6s + "=0|"
                + Constants.TIME_PERIOD_7s_9s + "=0|"
                + Constants.TIME_PERIOD_10s_30s + "=0|"
                + Constants.TIME_PERIOD_30s_60s + "=0|"
                + Constants.TIME_PERIOD_1m_3m + "=0|"
                + Constants.TIME_PERIOD_3m_10m + "=0|"
                + Constants.TIME_PERIOD_10m_30m + "=0|"
                + Constants.TIME_PERIOD_30m + "=0|"
                + Constants.STEP_PERIOD_1_3 + "=0|"
                + Constants.STEP_PERIOD_4_6 + "=0|"
                + Constants.STEP_PERIOD_7_9 + "=0|"
                + Constants.STEP_PERIOD_10_30 + "=0|"
                + Constants.STEP_PERIOD_30_60 + "=0|"
                + Constants.STEP_PERIOD_60 + "=0";
        this.result = newResult;
    }

    @Override
    public void add(String v) {
        String v1 = this.result;
        String v2 = v;
        // 校验：v1为空的话，直接返回v2
        if (StringUtils.isEmpty(v2)) {
            return;
        } else {
            String newResult = "";
            String oldValue = StringUtils.getFieldFromConcatString(v1, "\\|", v2);
            if (oldValue != null) {
                // 将范围区间原有的值，累加1
                int newValue = Integer.valueOf(oldValue) + 1;
                // 使用StringUtils工具类，将v1中，v2对应的值，设置成新的累加后的值
                newResult = StringUtils.setFieldInConcatString(v1, "\\|", v2, String.valueOf(newValue));
            }
            result = newResult;
        }
    }

    @Override
    public void merge(AccumulatorV2<String, String> other) {
        result = other.value();
    }

    @Override
    public String value() {
        return this.result;
    }

}