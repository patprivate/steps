package com.patfives.steps.api.apimodel;

public class StepData {

    public int steps;
    public long utcStart;
    public long utcEnd;

    public StepData(int steps, long utcStart, long utcEnd) {
        this.steps = steps;
        this.utcStart = utcStart;
        this.utcEnd = utcEnd;
    }
}
