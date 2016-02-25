package com.patfives.steps.model;

import io.realm.RealmObject;

public class StepRealm extends RealmObject {

    private int steps;
    private long utcStart;
    private long utcEnd;

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public long getUtcStart() {
        return utcStart;
    }

    public void setUtcStart(long utcStart) {
        this.utcStart = utcStart;
    }

    public long getUtcEnd() {
        return utcEnd;
    }

    public void setUtcEnd(long utcEnd) {
        this.utcEnd = utcEnd;
    }
}
