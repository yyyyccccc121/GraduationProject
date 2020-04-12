package com.ittttt.provider.controller;

import com.ittttt.provider.pojo.Dynamic;
import com.ittttt.provider.pojo.DynamicComment;
import com.ittttt.provider.pojo.DynamicImage;
import com.ittttt.provider.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "space")
public class SpaceController {

    @Autowired
    private SpaceService spaceService;

    @RequestMapping(value = "/insertToDynamic")
    public void insertToDynamic(int id,String theTitle,Date theDate){
        spaceService.insertToDynamic(id,theTitle,theDate);
    }

    @RequestMapping(value = "/insertToDynamicAccountNumber")
    public void insertToDynamicAccountNumber(int id,String accountNumber){
        spaceService.insertToDynamicAccountNumber(id,accountNumber);
    }

    @RequestMapping(value = "/insertToDynamicImage")
    public void insertToDynamicImage(int id,String route){
        spaceService.insertToDynamicImage(id,route);
    }

    @RequestMapping(value = "/addAgree")
    public void addAgree(String theId,String names){
        spaceService.addAgree(theId,names);
    }

    @RequestMapping(value = "/addComment")
    public void addComment(String str,String nickname,String theId){
        spaceService.addComment(theId,nickname,str);
    }

    @RequestMapping(value = "/findAllDynamic")
    public List<Dynamic> findAllDynamic(){
        return spaceService.findAllDynamic();
    }

    @RequestMapping(value = "/findNumberById")
    public String findNumberById(int id){
        return spaceService.findNumberById(id);
    }

    @RequestMapping(value = "/findAllDynamicComment")
    public List<DynamicComment> findAllDynamicComment(int id){
        return spaceService.findAllDynamicComment(id);
    }

    @RequestMapping(value = "/findImageById")
    public DynamicImage findImageById(int id){
        return spaceService.findImageById(id);
    }
}
