package com.patfives.steps;

import android.content.Context;

import com.patfives.steps.api.ApiClient;
import com.patfives.steps.api.StepsApiInterface;
import com.patfives.steps.api.apimodel.StepsResponse;
import com.patfives.steps.model.DayView;
import com.patfives.steps.model.MinuteRealm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DataManager {

    private static final int DAILY_GOAL = 10000;
    private static final int TOTAL_MINUTES_IN_DAY = 60 * 24;
    Context context;
    StorageManager storageManager;

    public DataManager(Context context){
        this.context = context;
        storageManager = new StorageManager(context);
    }

    public Observable loadSteps(){

        //load from local storage
        Observable<List<DayView>> stepsListDb = Observable.create(new Observable.OnSubscribe<List<DayView>>() {
            @Override
            public void call(Subscriber<? super List<DayView>> subscriber) {
                subscriber.onNext(buildDayViews());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());

        //load from the endpoint
        StepsApiInterface stepsApiInterface = ApiClient.getStepsApiInterface();
        Observable<List<DayView>> stepsListApi =  stepsApiInterface.getFeed()
                .map(new Func1<StepsResponse, List<DayView>>() {
                    @Override
                    public List<DayView> call(StepsResponse stepsResponse) {
                        //save data
                        storageManager.saveData(stepsResponse);

                        List<DayView> dayViews = buildDayViews();
                        return dayViews;
                    }
                }).subscribeOn(Schedulers.computation());

        return Observable.concat(stepsListDb, stepsListApi).observeOn(AndroidSchedulers.mainThread());
    }

    private List<DayView> buildDayViews(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, EEEE");
        simpleDateFormat.setTimeZone(TimeZone.getDefault()); //TimeZone.getTimeZone("UTC"));

        Map<String, DayView> dayViewMap = new HashMap<>();

        List<MinuteRealm> minuteRealms = storageManager.getLocalMinutes();
        if(minuteRealms != null && !minuteRealms.isEmpty()){
            //iterate through all the minute records we have
            for(MinuteRealm minuteRealm : minuteRealms){
                if(minuteRealm.getSteps() > 0){
                    //get the date in local time for the start of this current minute
                    long minuteStart = minuteRealm.getUtcStart();
                    String localDateHeader = simpleDateFormat.format(new Date(minuteStart * 1000));

                    //check if that date is in the map, if it is, add this minute's steps to the total for that day
                    //otherwise make a new record and put it in the map with the current minute's steps
                    if(dayViewMap.containsKey(localDateHeader)){
                        DayView dayView = dayViewMap.get(localDateHeader);
                        int currentTotalSteps = dayView.totalSteps;
                        dayView.totalSteps = currentTotalSteps + minuteRealm.getSteps();
                    }else{
                        DayView dayView = new DayView();
                        dayView.dateHeader = localDateHeader;
                        dayView.totalSteps = minuteRealm.getSteps();
                        dayView.utcTimestamp = minuteStart;

                        dayViewMap.put(localDateHeader, dayView);
                    }
                }
            }
        }

        //build a list from our map, calculate the final average and dailypercentage
        List<DayView> dayViews = new ArrayList<>();
        for(DayView dayView : dayViewMap.values()){
            dayView.dailyPercentage = calculateDailyPercentage(dayView.totalSteps);
            dayView.averageSteps = calculateAverageSteps(dayView.totalSteps);
            dayViews.add(dayView);
        }

        //sort the list by day
        Collections.sort(dayViews);

        return dayViews;
    }

    private int calculateAverageSteps(int totalSteps){
        if(totalSteps <= 0){
            return 0;
        }
        return (int) Math.round((1.0) * totalSteps / TOTAL_MINUTES_IN_DAY);
    }

    private double calculateDailyPercentage(int totalSteps){
        if(totalSteps <= 0){
            return 0;
        }
        double dailyPercentage = ((1.0) * totalSteps) / DAILY_GOAL;
        return dailyPercentage;
    }

}