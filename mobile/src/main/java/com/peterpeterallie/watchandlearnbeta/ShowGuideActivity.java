package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.model.Step;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chepeter on 8/2/14.
 */
public class ShowGuideActivity extends Activity {

    private TextView guideTitle;

    private TextView stepIndex;
    private TextView stepText;
    private ImageView stepImage;

    private Button prevButton;
    private Button nextButton;

    private Guide guide;

    private int currentStepIndex = 0;

    private Map<String, Bitmap> cachedBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        cachedBitmaps = new HashMap<String, Bitmap>();

        guideTitle = (TextView) this.findViewById(R.id.guide_title);
        stepIndex = (TextView) this.findViewById(R.id.step_index);
        stepText = (TextView) this.findViewById(R.id.step_text);
        stepImage = (ImageView) this.findViewById(R.id.step_image);

        prevButton = (Button) this.findViewById(R.id.btn_prevstep);
        nextButton = (Button) this.findViewById(R.id.btn_nextstep);

        Gson gson = new Gson();
        guide = gson.fromJson(this.getIntent().getStringExtra("guide"), Guide.class);

        guideTitle.setText(guide.getTitle());

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevStep();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });
        showStep(0);
    }

    private void prevStep() {
        if (currentStepIndex <= 0) {
            return;
        }
        showStep(--currentStepIndex);
    }

    private void nextStep() {
        if (currentStepIndex >= guide.getNumSteps()-1) {
            return;
        }
        showStep(++currentStepIndex);
    }

    private void showStep(int index) {
        Step step = guide.getStep(index);
        stepIndex.setText("Step " + (index+1) + "/" + guide.getNumSteps());
        stepText.setText(step.getText());

        if (step.getPhoto() != null && step.getPhoto().contains("http")) {
            Bitmap bitmap = cachedBitmaps.get(step.getPhoto());
            if (bitmap == null) {
                new DownloadImageTask(stepImage, this.findViewById(R.id.loading_icon), cachedBitmaps).execute(step.getPhoto());
            }
            else {
                stepImage.setImageBitmap(bitmap);
            }
        }
        else if (step.getPhoto() != null && new File(step.getPhoto()).isFile()) {
            Bitmap bitmap = PhotoUtils.decodeSampledBitmapFromFile(step.getPhoto(), 100, 100);
            stepImage.setImageBitmap(bitmap);
        }
    }


}
