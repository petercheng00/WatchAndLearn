package com.peterpeterallie.watchandlearnbeta.model;

/**
 * Created by chepeter on 8/2/14.
 */
public class Step {
    private String text;
    private int countdown;
    private boolean countup;
    private String photo;

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    public void setCountdown(int countdown){
        this.countdown = countdown;
    }
    public int getCountdown(){
        return this.countdown;
    }
    public void setCountup(boolean countup) {
        this.countup = countup;
    }
    public boolean getCountup(){
        return this.countup;
    }

    public void setPhoto(String photo){
        this.photo = photo;
    }
    public String getPhoto(){
        return this.photo;
    }
}
