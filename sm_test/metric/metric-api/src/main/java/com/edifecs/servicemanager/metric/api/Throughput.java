package com.edifecs.servicemanager.metric.api;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: sandeep.kath
 * Date: 2/27/14
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Throughput implements Serializable {
    private long count;
    private double meanRate;
    private double oneMinuteRate;
    private double fiveMinuteRate;
    private double fifteenMinuteRate;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getMeanRate() {
        return meanRate;
    }

    public void setMeanRate(double meanRate) {
        this.meanRate = meanRate;
    }

    public double getOneMinuteRate() {
        return oneMinuteRate;
    }

    public void setOneMinuteRate(double oneMinuteRate) {
        this.oneMinuteRate = oneMinuteRate;
    }

    public double getFiveMinuteRate() {
        return fiveMinuteRate;
    }

    public void setFiveMinuteRate(double fiveMinuteRate) {
        this.fiveMinuteRate = fiveMinuteRate;
    }

    public double getFifteenMinuteRate() {
        return fifteenMinuteRate;
    }

    public void setFifteenMinuteRate(double fifteenMinuteRate) {
        this.fifteenMinuteRate = fifteenMinuteRate;
    }


    @Override
    public String toString() {
        return "Throughput{" +
                "count=" + count +
                ", meanRate=" + meanRate +
                ", oneMinuteRate=" + oneMinuteRate +
                ", fiveMinuteRate=" + fiveMinuteRate +
                ", fifteenMinuteRate=" + fifteenMinuteRate +
                '}';
    }
}
