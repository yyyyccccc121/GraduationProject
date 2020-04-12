package com.ittttt.provider.controller;

import com.ittttt.provider.service.FileService;
import com.ittttt.provider.utils.JedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
public class FileController {

//    private static Jedis jedis;
//    static{
//        jedis = JedisUtils.getJedis();
//    }

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/addToTheFile")
    public String addToTheFile(String uuidFileName,String realFileName){
        fileService.addToTheFile(uuidFileName,realFileName);
        return "s";
    }

    @RequestMapping(value = "/findRealFileName")
    public String findRealFileName(String uuidFileName){
        Jedis jedis = JedisUtils.getJedis();
        String realFileName;
        //以唯一文件名做为键,真实文件名做为值
        if(!jedis.exists(uuidFileName)) {
            realFileName = fileService.findRealFileName(uuidFileName);
            jedis.set(uuidFileName,realFileName);
            JedisUtils.close(jedis);
            return realFileName;
        }else {
            realFileName = jedis.get(uuidFileName);
            JedisUtils.close(jedis);
            return realFileName;
        }
    }

    @RequestMapping(value = "/saveAudio")
    public String saveAudio(String uuidAudioName,String theTime){
        fileService.saveAudio(uuidAudioName,theTime);
        return "success";
    }

    @RequestMapping(value = "/findTheTimeById")
    public String findTheTimeById(String uuidAudioName){
        return fileService.findTheTimeById(uuidAudioName);
    }
}
