package com.ittttt.provider.service.Impl;

import com.ittttt.provider.mapper.SpaceMapper;
import com.ittttt.provider.pojo.Dynamic;
import com.ittttt.provider.pojo.DynamicComment;
import com.ittttt.provider.pojo.DynamicImage;
import com.ittttt.provider.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SpaceServiceImpl implements SpaceService {

    @Autowired
    private SpaceMapper spaceMapper;

    @Override
    public void insertToDynamic(int id, String theTitle, Date theDate) {
        spaceMapper.insertToDynamic(id,theTitle,theDate);
    }

    @Override
    public void insertToDynamicAccountNumber(int id, String accountNumber) {
        spaceMapper.insertToDynamicAccountNumber(id,accountNumber);
    }

    @Override
    public void insertToDynamicImage(int id, String route) {
        spaceMapper.insertToDynamicImage(id,route);
    }

    @Override
    public void addAgree(String theId, String names) {
        spaceMapper.addAgree(theId,names);
    }

    @Override
    public void addComment(String theId, String nickname, String str) {
        spaceMapper.addComment(theId,nickname,str);
    }

    @Override
    public List<Dynamic> findAllDynamic() {
        return spaceMapper.findAllDynamic();
    }

    @Override
    public String findNumberById(int id) {
        return spaceMapper.findNumberById(id);
    }

    @Override
    public List<DynamicComment> findAllDynamicComment(int id) {
        return spaceMapper.findAllDynamicComment(id);
    }

    @Override
    public DynamicImage findImageById(int id) {
        return spaceMapper.findImageById(id);
    }

}
