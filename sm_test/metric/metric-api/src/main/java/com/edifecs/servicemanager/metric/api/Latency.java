package com.edifecs.servicemanager.metric.api;

/**
 * Created with IntelliJ IDEA.
 * User: sandeep.kath
 * Date: 2/28/14
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class Latency {
    private long count;
    private double max;
    private double mean;
    private double min;
    private double standardDeviation;
    private double percentile50;
    private double percentile75;
    private double percentile95;
    private double percentile98;
    private double percentile99;
    private double percentile999;
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

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public double getPercentile50() {
        return percentile50;
    }

    public void setPercentile50(double percentile50) {
        this.percentile50 = percentile50;
    }

    public double getPercentile75() {
        return percentile75;
    }

    public void setPercentile75(double percentile75) {
        this.percentile75 = percentile75;
    }

    public double getPercentile95() {
        return percentile95;
    }

    public void setPercentile95(double percentile95) {
        this.percentile95 = percentile95;
    }

    public double getPercentile98() {
        return percentile98;
    }

    public void setPercentile98(double percentile98) {
        this.percentile98 = percentile98;
    }

    public double getPercentile99() {
        return percentile99;
    }

    public void setPercentile99(double percentile99) {
        this.percentile99 = percentile99;
    }

    public double getPercentile999() {
        return percentile999;
    }

    public void setPercentile999(double percentile999) {
        this.percentile999 = percentile999;
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
        return "Latency{" +
                "count=" + count +
                ", max=" + max +
                ", mean=" + mean +
                ", min=" + min +
                ", standardDeviation=" + standardDeviation +
                ", percentile50=" + percentile50 +
                ", percentile75=" + percentile75 +
                ", percentile95=" + percentile95 +
                ", percentile98=" + percentile98 +
                ", percentile99=" + percentile99 +
                ", percentile999=" + percentile999 +
                ", meanRate=" + meanRate +
                ", oneMinuteRate=" + oneMinuteRate +
                ", fiveMinuteRate=" + fiveMinuteRate +
                ", fifteenMinuteRate=" + fifteenMinuteRate +
                '}';
    }


}
