package com.peterpeterallie.watchandlearnbeta.model;

import java.util.List;

public class Guide {

    private String id;
    private String title;
    private String photo;

    private List<Step> steps;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPhoto() {
        return photo;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
