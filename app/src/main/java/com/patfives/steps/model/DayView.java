package com.patfives.steps.model;

public class DayView implements Comparable<DayView> {
    public String dateHeader;
    public int totalSteps;
    public int averageSteps;
    public long utcTimestamp;
    public double dailyPercentage;



    @Override
    public int compareTo(DayView another) {
        long time1 = (this == null) ? 0 : this.utcTimestamp;
        long time2 = (another == null) ? 0 : another.utcTimestamp;

        long delta = time1 - time2;
        if(delta > 0) return -1;
        if(delta < 0) return 1;

        return 0;
    }
}