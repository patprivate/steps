package com.patfives.steps.api.apimodel;

import java.util.List;

public class StepsResponse {

    public List<StepData> stepRecords;

    public StepsResponse(List<StepData> stepRecords){
        this.stepRecords = stepRecords;
    }

}
