package com.ittttt.provider.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface FileMapper {
    void addToTheFile(String uuidFileName,String realFileName);
    String findRealFileName(String uuidFileName);
    void saveAudio(String uuidAudioName, String theTime);
    String findTheTimeById(String uuidAudioName);
}
