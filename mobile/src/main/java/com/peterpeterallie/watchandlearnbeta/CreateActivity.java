package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.model.Step;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chepeter on 8/2/14.
 *
 * Activity for creating guides
 */
public class CreateActivity extends Activity {

    public static final String GUIDE_FILENAME_PREFIX = "guide_";

    private Guide guide;

    private Step currentStep;
    private int currentStepIndex = 0;

    private Button prevButton;
    private Button photoButton;


    private ImageView imageThumb;
    private TextView stepIndex;
    private EditText stepText;
    private CheckBox stopWatch;
    private EditText timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        prevButton = (Button) this.findViewById(R.id.btn_prevstep);
        Button nextButton = (Button) this.findViewById(R.id.btn_nextstep);
        Button saveButton = (Button) this.findViewById(R.id.btn_saveguide);
        photoButton = (Button) this.findViewById(R.id.btn_photo);
        stepIndex = (TextView) this.findViewById(R.id.step_index);
        stepText = (EditText) this.findViewById(R.id.step_text);
        imageThumb = (ImageView) this.findViewById(R.id.image_thumb);
        stopWatch = (CheckBox) this.findViewById(R.id.step_stopwatch);
        timer = (EditText) this.findViewById(R.id.step_timer);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPrevStep();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextStep();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGuideAndExit();
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    // take photo and save to file
                    File imageFile = PhotoUtils.createImageFile();
                    currentStep.setPhoto(imageFile.getAbsolutePath());
                    PhotoUtils.getPhoto(CreateActivity.this, imageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // initialize a guide
        guide = new Guide();
        currentStep = new Step();
        currentStepIndex = 0;
        loadStep(currentStep);
        prevButton.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoUtils.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // photo was taken
            loadCurrentPhoto();
        }
    }

    /**
     * display photo for current step
     */
    private void loadCurrentPhoto() {
        Bitmap bitmap = PhotoUtils.decodeSampledBitmapFromFile(currentStep.getPhoto(), 150, 150);
        imageThumb.setImageBitmap(bitmap);
        imageThumb.setVisibility(View.VISIBLE);
        photoButton.setText(R.string.change_photo);
    }

    /**
     * Go back one step
     *
     */
    private void gotoPrevStep() {
        if (currentStepIndex <= 0) {
            return;
        }
        saveCurrentStep();
        --currentStepIndex;
        currentStep = guide.getStep(currentStepIndex);
        if (currentStepIndex == 0) {
            prevButton.setEnabled(false);
        }
        loadStep(currentStep);
    }


    /**
     * go to the next step
     */
    private void gotoNextStep() {
        if (stepText.getText().toString().isEmpty()) {
            return;
        }
        saveCurrentStep();
        guide.setStep(currentStepIndex, currentStep);
        ++currentStepIndex;
        if (currentStepIndex < guide.getNumSteps()) {
            currentStep = guide.getStep(currentStepIndex);
        }
        else {
            currentStep = new Step();
        }
        loadStep(currentStep);
        prevButton.setEnabled(true);
    }

    /**
     * read in a step and display its data
     * @param step step to load
     */
    private void loadStep(Step step) {
        // math.max since if step is new, it will not be added to guide yet
        this.stepIndex.setText("Editing Step " + (currentStepIndex + 1) + "/" + Math.max(currentStepIndex + 1, guide.getNumSteps()));
        stepText.setText(step.getText());
        stopWatch.setChecked(step.getCountup());
        timer.setText(Integer.toString(step.getCountdown()));
        if (step.getPhoto() == null) {
            photoButton.setText(R.string.add_photo);
            imageThumb.setVisibility(View.GONE);
        }
        else {
            loadCurrentPhoto();
        }
    }

    /**
     * Save currently displayed step
     */
    private void saveCurrentStep() {
        currentStep.setText(stepText.getText().toString());
        currentStep.setCountup(stopWatch.isChecked());
        currentStep.setCountdown(Integer.parseInt(timer.getText().toString()));
    }

    /**
     * Save guide and exit
     */
    private void saveGuideAndExit() {
        saveCurrentStep();
        TextView guideTitleView = (TextView) this.findViewById(R.id.guide_title);
        guide.setTitle(guideTitleView.getText().toString());
        Gson gson = new Gson();
        String json = gson.toJson(guide);
        String filename = GUIDE_FILENAME_PREFIX + guide.getId();
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
