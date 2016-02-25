package com.patfives.steps;

import android.content.Context;

import com.patfives.steps.api.apimodel.StepData;
import com.patfives.steps.api.apimodel.StepsResponse;
import com.patfives.steps.model.MinuteRealm;
import com.patfives.steps.model.StepRealm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

public class StorageManager {

    private static final double SECONDS_PER_MINUTE = 60.0;
    private Context context;

    public StorageManager(Context context) {
        this.context = context;
    }

    public List<MinuteRealm> getLocalMinutes() {
        Realm realm = Realm.getInstance(context);
        RealmQuery<MinuteRealm> query = realm.where(MinuteRealm.class);

        List<MinuteRealm> minuteRealms = query.findAll();
        return minuteRealms;
    }

    public void saveData(StepsResponse stepsResponse) {
        List<StepData> stepRecords = stepsResponse.stepRecords;
        if (stepRecords != null && !stepRecords.isEmpty()) {
            deletaAll();
            saveRawData(stepRecords);
            saveMinutes(stepRecords);
        }
    }

    private void saveMinutes(List<StepData> stepRecords) {

        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        for (StepData stepData : stepRecords) {

            if (stepData.steps == 0 || stepData.utcStart == 0 || stepData.utcEnd == 0) {
                continue;
            }

            long timespan = stepData.utcEnd - stepData.utcStart;

            //if this timespan is less than a minute, just insert the new minute with the steps now and move on to the next
            if (timespan <= SECONDS_PER_MINUTE) {
                MinuteRealm minuteRealm = realm.createObject(MinuteRealm.class);
                minuteRealm.setUtcStart(stepData.utcStart);
                minuteRealm.setSteps(stepData.steps);
                continue;
            }

            //total whole minutes in this timeframe
            int minutes = (int) (timespan / SECONDS_PER_MINUTE);

            //the steps each minute obect gets for this timespan
            int stepsPerMinute = (int) ((SECONDS_PER_MINUTE / timespan) * stepData.steps);

            for (int m = 0; m < minutes; m++) {
                MinuteRealm minuteRealm = realm.createObject(MinuteRealm.class);

                //add 60seconds to the start of this timeframe for every minute after the first
                long minuteStart = stepData.utcStart + (int)(SECONDS_PER_MINUTE * m);
                minuteRealm.setUtcStart(minuteStart);
                minuteRealm.setSteps(stepsPerMinute);
            }

            int remaining = (int) (timespan % SECONDS_PER_MINUTE);
            if (remaining > 0) {

                //calculate the extra time's percentage of steps
                int remainingSteps = (int) (((1.0 * remaining) / timespan) * stepData.steps);
                MinuteRealm minuteRealm = realm.createObject(MinuteRealm.class);
                minuteRealm.setUtcStart(stepData.utcStart + (int)(SECONDS_PER_MINUTE * remaining));
                minuteRealm.setSteps(remainingSteps);
            }
        }
        realm.commitTransaction();
        realm.close();
    }

    private void deletaAll() {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        realm.clear(StepRealm.class);
        realm.clear(MinuteRealm.class);
        realm.commitTransaction();
    }

    private void saveRawData(List<StepData> stepRecords) {
        Realm realm = Realm.getInstance(context);
        if (stepRecords != null && !stepRecords.isEmpty()) {
            realm.beginTransaction();
            for (StepData stepData : stepRecords) {
                StepRealm stepRealm = realm.createObject(StepRealm.class);
                stepRealm.setSteps(stepData.steps);
                stepRealm.setUtcEnd(stepData.utcEnd);
                stepRealm.setUtcStart(stepData.utcStart);
            }
            realm.commitTransaction();
        }

        realm.close();
    }


}
