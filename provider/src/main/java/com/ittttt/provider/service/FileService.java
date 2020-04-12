package com.ittttt.provider.service;

public interface FileService {
    void addToTheFile(String uuidFileName,String realFileName);
    String findRealFileName(String uuidFileName);
    void saveAudio(String uuidAudioName,String theTime);
    String findTheTimeById(String uuidAudioName);
}
