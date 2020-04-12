package com.boot.pojo;

public class TheAudio {

    private String uuidAudioName;
    private String theTime;

    public TheAudio(String uuidAudioName, String theTime) {
        this.uuidAudioName = uuidAudioName;
        this.theTime = theTime;
    }

    public String getUuidAudioName() {
        return uuidAudioName;
    }

    public void setUuidAudioName(String uuidAudioName) {
        this.uuidAudioName = uuidAudioName;
    }

    public String getTheTime() {
        return theTime;
    }

    public void setTheTime(String theTime) {
        this.theTime = theTime;
    }

    @Override
    public String toString() {
        return "TheAudio{" +
                "uuidAudioName='" + uuidAudioName + '\'' +
                ", theTime='" + theTime + '\'' +
                '}';
    }
}
