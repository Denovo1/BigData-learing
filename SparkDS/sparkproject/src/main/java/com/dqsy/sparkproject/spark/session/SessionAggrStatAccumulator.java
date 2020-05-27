package com.dqsy.sparkproject.spark.session;

import com.dqsy.sparkproject.constant.Constants;
import com.dqsy.sparkproject.util.StringUtils;
import org.apache.spark.util.AccumulatorV2;

/**
 * session聚合统计Accumulator
 * <p>
 * 可以使用自己定义的一些数据格式，比如String，也可以是自己定义model，自己定义的类（必须可序列化）
 * 各个task，分布式在运行，可以根据需求，task给Accumulator传入不同的值
 *
 * @author liusinan
 */
public class SessionAggrStatAccumulator extends AccumulatorV2<String, String> {

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
        SessionAggrStatAccumulator sessionAggrStatAccumulator = new SessionAggrStatAccumulator();
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

    /**
     * session统计计算逻辑
     *
     * @param v 添加的字段
     */
    @Override
    public void add(String v) {
        String v1 = value();
        String oldValue = StringUtils.getFieldFromConcatString(v1, "\\|", v);
        if (oldValue != null) {
            // 将范围区间原有的值，累加1
            int newValue = Integer.valueOf(oldValue) + 1;
            String addedRes = StringUtils.setFieldInConcatString(v1, "\\|", v, String.valueOf(newValue));
            this.result = addedRes;
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