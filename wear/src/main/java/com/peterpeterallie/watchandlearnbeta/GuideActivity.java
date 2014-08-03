package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;

import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.parser.JsonDeserializer;
import com.peterpeterallie.watchandlearnbeta.util.AssetsProvider;

public class GuideActivity extends Activity {

    private static final String TAG = MyActivity.class.getSimpleName();
    public static final String FILENAME = "guideFileName";

    private GridViewPager gridViewPager;
    private StepsAdapter stepsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        gridViewPager = (GridViewPager) findViewById(R.id.gridViewPager);

        stepsAdapter = new StepsAdapter(GuideActivity.this, getGuide());
        gridViewPager.setAdapter(stepsAdapter);
    }

    private Guide getGuide() {
        String filename = getIntent().getStringExtra(FILENAME);
        String jsonGuide = AssetsProvider.openFileAsString(this, filename);
        try {
            return JsonDeserializer.getGuide(jsonGuide);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
