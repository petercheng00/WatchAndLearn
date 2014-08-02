package com.peterpeterallie.watchandlearnbeta.model;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by chepeter on 8/2/14.
 */
public class Guide {
    private String id;
    private String title;
    private String photo;

    private List<Step> steps;

    public Guide() {
        id = UUID.randomUUID().toString();
        steps = new LinkedList<Step>();
    }

    public String getId(){
        return this.id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Step getStep(int index) {
        return steps.get(index);
    }

    public void setStep(int index, Step step) {
        if (index < steps.size()) {
            steps.set(index, step);
        }
        else if (index == steps.size()) {
            steps.add(step);
        }
        else {
            throw new IndexOutOfBoundsException();
        }
    }

    public int getNumSteps() {
        return steps.size();
    }

}
