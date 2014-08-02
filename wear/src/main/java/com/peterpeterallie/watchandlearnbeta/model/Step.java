package com.peterpeterallie.watchandlearnbeta.model;

public class Step {

    private String text;
    private int countdown;
    private boolean countup;
    private String photo;

    public String getText() {
        return text;
    }

    public int getCountdown() {
        return countdown;
    }

    public boolean isCountup() {
        return countup;
    }

    public String getPhoto() {
        return photo;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void setCountup(boolean countup) {
        this.countup = countup;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
