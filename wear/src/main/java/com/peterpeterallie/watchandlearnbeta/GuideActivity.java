package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;

import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.parser.JsonDeserializer;
import com.peterpeterallie.watchandlearnbeta.util.FileUtil;

import org.json.JSONException;

import java.io.File;

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
        String filename = this.getFilesDir() + "/" + getIntent().getStringExtra(FILENAME);

        File file = new File(filename);
        if (file.isFile()) {
            try {
                Guide guide = JsonDeserializer.getGuide(FileUtil.fileToString(file, this));
                return guide;
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
