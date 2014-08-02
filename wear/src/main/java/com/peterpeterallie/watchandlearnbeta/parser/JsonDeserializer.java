package com.peterpeterallie.watchandlearnbeta.parser;

import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonDeserializer {
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String PHOTO = "photo";
    private static final String STEPS = "steps";


    public static Guide getGuide(String jsonString) throws JSONException {

        JSONObject jsonGuide = new JSONObject(jsonString);
        Guide guide = new Guide();
        guide.setId(jsonGuide.optString(ID, ""));
        guide.setTitle(jsonGuide.optString(TITLE, ""));
        guide.setPhoto(jsonGuide.optString(PHOTO, ""));

        if (jsonGuide.has(STEPS)) {
            List<Step> steps = new ArrayList<Step>();
            JSONArray jsonArray = jsonGuide.getJSONArray(STEPS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonStep = jsonArray.getJSONObject(i);
                steps.add(getStep(jsonStep));
            }

            guide.setStepList(steps);
        }

        return guide;
    }

    private static final String TEXT = "text";
    private static final String COUNTDOWN = "countdown";
    private static final String COUNTUP = "countup";

    private static Step getStep(JSONObject jsonStep) {
        Step step = new Step();
        step.setText(jsonStep.optString(TEXT, ""));
        step.setCountdown(jsonStep.optInt(COUNTDOWN, 0));
        step.setCountup(jsonStep.optBoolean(COUNTUP, false));
        step.setPhoto(jsonStep.optString(PHOTO, ""));
        return step;
    }
}
