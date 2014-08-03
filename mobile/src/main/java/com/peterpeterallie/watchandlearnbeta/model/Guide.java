package com.peterpeterallie.watchandlearnbeta.model;

import com.google.gson.Gson;

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

    public String getTitle() {
        return this.title;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Step getStep(int index) {
        return steps.get(index);
    }

    public List<Step> getSteps() {
        return steps;
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

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Guide fromJson(String string) {
        Gson gson = new Gson();
        return gson.fromJson(string, Guide.class);
    }

}
