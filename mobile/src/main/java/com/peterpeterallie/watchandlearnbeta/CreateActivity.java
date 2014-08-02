package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.model.Step;

import java.io.FileOutputStream;

/**
 * Created by chepeter on 8/2/14.
 *
 * TODO: Only text is hooked up
 */
public class CreateActivity extends Activity {

    private Guide guide;

    private Step currentStep;
    private int currentStepIndex = 0;

    private Button prevButton;
    private Button nextButton;
    private Button saveButton;
    private Button photoButton;

    private TextView stepTitle;
    private TextView stepText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        prevButton = (Button) this.findViewById(R.id.btn_prevstep);
        nextButton = (Button) this.findViewById(R.id.btn_nextstep);
        saveButton = (Button) this.findViewById(R.id.btn_saveguide);
        photoButton = (Button) this.findViewById(R.id.btn_photo);
        stepTitle = (TextView) this.findViewById(R.id.step_index);
        stepText = (TextView) this.findViewById(R.id.step_text);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPrevStep();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gotoNextStep();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGuide();
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getPhoto();
            }
        });

        guide = new Guide();
        currentStep = new Step();
        currentStepIndex = 0;
        loadStep(currentStep);
        prevButton.setEnabled(false);
    }

    private void getPhoto() {
        int REQUEST_IMAGE_CAPTURE = 1;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void gotoPrevStep() {
        saveCurrentStep();
        --currentStepIndex;
        currentStep = guide.getStep(currentStepIndex);
        if (currentStepIndex == 0) {
            prevButton.setEnabled(false);
        }
        loadStep(currentStep);
    }

    private void gotoNextStep() {
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

    private void loadStep(Step step) {
        // math.max since if step is new, it will not be added to guide yet
        this.stepTitle.setText("Editing Step " + (currentStepIndex +1) + "/" + Math.max(currentStepIndex + 1, guide.getNumSteps()));

        stepText.setText(step.getText());
    }

    private void saveCurrentStep() {
        currentStep.setText((String) stepText.getText().toString());
    }

    private void saveGuide() {
        saveCurrentStep();
        TextView guideTitleView = (TextView) this.findViewById(R.id.guide_title);
        guide.setTitle(guideTitleView.getText().toString());
        Gson gson = new Gson();
        String json = gson.toJson(guide);
        String filename = "guide_" + guide.getId();
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
