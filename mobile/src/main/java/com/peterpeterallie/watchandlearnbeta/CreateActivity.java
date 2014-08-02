package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.model.Step;

/**
 * Created by chepeter on 8/2/14.
 */
public class CreateActivity extends Activity {

    private Guide guide;

    private Step currStep;
    private int currStepIndex = 0;

    private Button prevButton;
    private Button nextButton;

    private TextView stepTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        prevButton = (Button) this.findViewById(R.id.btn_prevstep);
        nextButton = (Button) this.findViewById(R.id.btn_nextstep);
        stepTitle = (TextView) this.findViewById(R.id.step_index);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPrevStep();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                editNextStep();
            }
        });

        guide = new Guide();
        currStep = new Step();
        currStepIndex = 0;
        prevButton.setEnabled(false);
    }

    private void saveCurrentStep() {

    }

    private void editPrevStep() {
        saveCurrentStep();
        --currStepIndex;
        currStep = guide.getStep(currStepIndex);
        if (currStepIndex == 0) {
            prevButton.setEnabled(false);
        }
        loadStep(currStep);
    }

    private void editNextStep() {
        saveCurrentStep();
        guide.setStep(currStepIndex, currStep);
        ++currStepIndex;
        if (currStepIndex < guide.getNumSteps()) {
            currStep = guide.getStep(currStepIndex);
        }
        else {
            currStep = new Step();
        }
        loadStep(currStep);
        prevButton.setEnabled(true);
    }

    private void loadStep(Step step) {
        this.stepTitle.setText("Editing Step " + (currStepIndex+1) + "/" + guide.getNumSteps());
    }
}
