package com.peterpeterallie.watchandlearnbeta.model;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.JsonObject;
import com.peterpeterallie.watchandlearnbeta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chepeter on 8/2/14.
 */
public class GuideInstructables {

    public static List<Guide> parseInstructablesGuides(Activity activity) {
        List<Guide> guides = new ArrayList<Guide>();
        for (int resource : new int[]{R.raw.instructable_1, R.raw.instructable_2, R.raw.instructable_3,
        R.raw.instructable_4, R.raw.instructable_5}) {
            String json = getStringFromResource(activity.getResources(), resource);
            if (json == null) {
                continue;
            }
            JSONObject jsonGuide = null;
            try {
                jsonGuide = new JSONObject(json);
                guides.add(createGuideFromJson(jsonGuide));
            } catch (JSONException e) {
                continue;
            }
        }
        return guides;
    }

    private static String getStringFromResource(Resources resources, int res) {
        InputStream stream = resources.openRawResource(res);
        try {
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            stream.close();
            return new String(buffer, "UTF-8");      // you just need to specify the charsetName
        } catch (IOException e) {
            return null;
        }
    }

    private static Guide createGuideFromJson(JSONObject jsonGuide) throws JSONException {
        Guide newGuide = new Guide();
        JSONArray col1 = jsonGuide.getJSONObject("results").getJSONArray("collection1");
        newGuide.setTitle(col1.getJSONObject(0).getString("title"));
        if (col1.getJSONObject(0).has("title_photo")) {
            newGuide.setPhoto(col1.getJSONObject(0).getJSONObject("title_photo").getString("src"));
        }

        JSONArray col2 = jsonGuide.getJSONObject("results").getJSONArray("collection2");
        for (int i = 0; i < col2.length(); ++i) {
            Step newStep = new Step();
            JSONObject step = col2.getJSONObject(i);
            String stepTitle = step.getString("step");
            String stepText = step.getString("text");
            String photo = null;
            if (step.get("photo") instanceof JSONObject) {
                photo = step.getJSONObject("photo").getString("src");
            } else if (step.get("photo") instanceof JSONArray) {
                // TODO grab each photo as a separate step
                photo = step.getJSONArray("photo").getJSONObject(0).getString("src");
            }
            newStep.setText(stepTitle + "\n" + stepText);
            newStep.setPhoto(photo);
            newGuide.setStep(i, newStep);
        }
        return newGuide;
    }
}
