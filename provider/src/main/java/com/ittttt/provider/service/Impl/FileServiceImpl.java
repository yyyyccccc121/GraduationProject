package com.ittttt.provider.service.Impl;

import com.ittttt.provider.mapper.FileMapper;
import com.ittttt.provider.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Override
    public void addToTheFile(String uuidFileName, String realFileName) {
        fileMapper.addToTheFile(uuidFileName,realFileName);
    }

    @Override
    public String findRealFileName(String uuidFileName) {
        return fileMapper.findRealFileName(uuidFileName);
    }

    @Override
    public void saveAudio(String uuidAudioName, String theTime) {
        fileMapper.saveAudio(uuidAudioName,theTime);
    }

    @Override
    public String findTheTimeById(String uuidAudioName) {
        return fileMapper.findTheTimeById(uuidAudioName);
    }
}
