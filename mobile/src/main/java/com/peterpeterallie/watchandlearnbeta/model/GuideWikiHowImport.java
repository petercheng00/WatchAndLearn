package com.peterpeterallie.watchandlearnbeta.model;

import android.app.Activity;
import android.content.res.Resources;

import com.peterpeterallie.watchandlearnbeta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chepeter on 8/3/14.
 */
public class GuideWikiHowImport {

    public static List<Guide> parseWikiHowGuides(Activity activity) {
        List<Guide> guides = new ArrayList<Guide>();
        for (int resource : new int[]{R.raw.wikihow_import}) {
            String json = getStringFromResource(activity.getResources(), resource);
            if (json == null) {
                continue;
            }
            try {
                JSONObject root = new JSONObject(json);
                JSONArray guidesJson = root.getJSONArray("data");
                for (int i = 0; i < guidesJson.length(); ++i) {
                    Guide guide = new Guide();
                    JSONObject guideJson = guidesJson.getJSONObject(i);
                    guide.setTitle(guideJson.getJSONArray("title").getString(0));
                    JSONArray stepsJson = guideJson.getJSONArray("text");
                    JSONArray photosJson = guideJson.getJSONArray("photo");
                    for (int j = 0; j < Math.max(stepsJson.length(), photosJson.length()); ++j) {
                        Step step = new Step();
                        if (j < stepsJson.length()) {
                            step.setText(stepsJson.getString(j));
                        }
                        if (j < photosJson.length()) {
                            step.setPhoto(photosJson.getString(j));
                        }
                        guide.setStep(j, step);
                    }
                    guides.add(guide);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
}