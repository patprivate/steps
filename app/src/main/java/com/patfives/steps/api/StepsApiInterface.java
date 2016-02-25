package com.patfives.steps.api;

import com.patfives.steps.api.apimodel.StepsResponse;

import retrofit2.http.GET;
import rx.Observable;

public interface StepsApiInterface {

    @GET(ApiClient.STEPS_REQUEST)
    Observable<StepsResponse> getFeed();

}
