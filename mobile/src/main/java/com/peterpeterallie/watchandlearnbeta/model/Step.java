package com.peterpeterallie.watchandlearnbeta.model;

/**
 * Created by chepeter on 8/2/14.
 */
public class Step {
    private String text;
    private int time;
    private boolean stopwatch;
    private String photo;

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    public void setTime(int time){
        this.time = time;
    }
    public int getTime(){
        return this.time;
    }
    public void setStopwatch(boolean stopwatch) {
        this.stopwatch = stopwatch;
    }
    public boolean getStopwatch(){
        return this.stopwatch;
    }

    public void setPhoto(String photo){
        this.photo = photo;
    }
    public String getPhoto(){
        return this.photo;
    }
}
