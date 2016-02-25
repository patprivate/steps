package com.patfives.steps.model;

import io.realm.RealmObject;

public class MinuteRealm extends RealmObject {

    private long utcStart;
    private int steps;

    public long getUtcStart() {
        return utcStart;
    }

    public void setUtcStart(long utcStart) {
        this.utcStart = utcStart;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
