package com.ittttt.provider.service;

import com.ittttt.provider.pojo.Dynamic;
import com.ittttt.provider.pojo.DynamicComment;
import com.ittttt.provider.pojo.DynamicImage;

import java.util.Date;
import java.util.List;

public interface SpaceService {
    void insertToDynamic(int id, String theTitle, Date theDate);
    void insertToDynamicAccountNumber(int id,String accountNumber);
    void insertToDynamicImage(int id,String route);
    void addAgree(String theId,String names);
    void addComment(String theId,String nickname,String str);
    List<Dynamic> findAllDynamic();
    String findNumberById(int id);
    List<DynamicComment> findAllDynamicComment(int id);
    DynamicImage findImageById(int id);
}
