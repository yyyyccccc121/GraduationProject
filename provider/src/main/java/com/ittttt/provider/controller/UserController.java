package com.ittttt.provider.controller;

import com.alibaba.fastjson.JSON;
import com.ittttt.provider.pojo.SysUser;
import com.ittttt.provider.pojo.recommendHead;
import com.ittttt.provider.service.UserService;
import com.ittttt.provider.utils.JedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

//    private static Jedis jedis;
//    static{
//        jedis = JedisUtils.getJedis();
//    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(String accountNumber,String password){
        boolean b = userService.findByaccountnumber(accountNumber);
        if(b){
            if (isInteger(accountNumber)){
                if(accountNumber.length()<=7) {
                    //添加默认图片路径
                    String headPortrait = "https://i.postimg.cc/L8scF29D/u-3307019904-1245652632-fm-26-gp-0.jpg";
                    String nickName = "假装这是昵称";
                    userService.adduser(accountNumber, password, nickName, headPortrait);
                    Jedis jedis = JedisUtils.getJedis();
                    jedis.del("listUser");
                    jedis.del("accountList");
                    return "注册成功";
                }else {
                    return "账号需小于8位数";
                }
            }else {
                return "账号需纯数字";
            }
        }
        return "用户已存在";
    }

    @RequestMapping(value = "/findAllByName",method = RequestMethod.GET)
    public SysUser findAllByName(String accountNumber){
        Jedis jedis = JedisUtils.getJedis();
        if(jedis.get(accountNumber)==null){
            SysUser sysUser = userService.findAllByName(accountNumber);
            jedis.set(accountNumber, JSON.toJSONString(sysUser));
            JedisUtils.close(jedis);
            return sysUser;
        }
        SysUser sysUser = JSON.parseObject(jedis.get(accountNumber),SysUser.class);
        JedisUtils.close(jedis);
        return sysUser;
    }

    @RequestMapping(value = "/editUser",method = RequestMethod.POST)
    public void editUser(@RequestParam String nickName, @RequestParam String sex,@RequestParam String region,@RequestParam String signature,@RequestParam String accountNumber){
        Jedis jedis = JedisUtils.getJedis();
        SysUser sysUser = JSON.parseObject(jedis.get(accountNumber),SysUser.class);
        sysUser.setNickName(nickName);
        sysUser.setSex(sex);
        sysUser.setRegion(region);
        sysUser.setSignature(signature);
        jedis.set(accountNumber,JSON.toJSONString(sysUser));
        JedisUtils.close(jedis);
        userService.updateUser(nickName,sex,region,signature,accountNumber);
    }

    @RequestMapping(value = "/editHead",method = RequestMethod.POST)
    public void editHead(@RequestParam String finalUrl, @RequestParam String accountNumber){
        Jedis jedis = JedisUtils.getJedis();
        userService.updateHead(finalUrl,accountNumber);
        SysUser sysUser = JSON.parseObject(jedis.get(accountNumber),SysUser.class);
        sysUser.setHeadPortrait(finalUrl);
        jedis.set(accountNumber,JSON.toJSONString(sysUser));
        JedisUtils.close(jedis);
    }

    @RequestMapping(value = "/editHead1",method = RequestMethod.GET)
    public void editHead1(String id, String accountNumber){
        Jedis jedis = JedisUtils.getJedis();
        String finalUrl = jedis.hmget("headMap",id).get(0);
        userService.updateHead(finalUrl,accountNumber);
        SysUser sysUser = JSON.parseObject(jedis.get(accountNumber),SysUser.class);
        sysUser.setHeadPortrait(finalUrl);
        jedis.set(accountNumber,JSON.toJSONString(sysUser));
        JedisUtils.close(jedis);
    }

    @RequestMapping(value = "/editRecommendHead",method = RequestMethod.GET)
    public List<recommendHead> editRecommendHead(){
        Jedis jedis = JedisUtils.getJedis();
        List<recommendHead> headList=null;
//        if(jedis.hmget("headMap")==null){
            headList = userService.findAllHead();
            Map<String,String> map = new HashMap<>();
            for(recommendHead r : headList){
                map.put(Integer.toString(r.getId()),r.getRecommendHeadUrl());
            }
            jedis.hmset("headMap",map);
//        }else {
//            //推荐头像id是从1-57,keys不能从Jedis中查出来,只能写死了
//            for(int i=1;i<58;i++){
//                String v = jedis.hmget("headMap",Integer.toString(i)).get(0);
//                headList.add(new recommendHead(i,v));
//                System.out.println("头像从reids中查询了-----------");
//            }
//        }
        JedisUtils.close(jedis);
        return headList;
    }

    @RequestMapping(value = "/findAllUser",method = RequestMethod.GET)
    public List<SysUser> findAllUser(){
        Jedis jedis = JedisUtils.getJedis();
        if(jedis.get("listUser")==null) {
            List<SysUser> listUser = userService.findAllUser();
            //做个标记，表明已经存入redis
            jedis.set("listUser", "1");
            for (SysUser sysUser : listUser) {
                jedis.set(sysUser.getAccountNumber(), JSON.toJSONString(sysUser));
                jedis.lpush("accountList", sysUser.getAccountNumber());
            }
            JedisUtils.close(jedis);
            return listUser;
        }

        List<SysUser> listUser =  new ArrayList<>();
        List<String> accountList = jedis.lrange("accountList",0,-1);
        for(String AccountNumber:accountList){
            listUser.add(JSON.parseObject(jedis.get(AccountNumber),SysUser.class));
        }
        JedisUtils.close(jedis);
        return listUser;
    }

    @RequestMapping(value = "/findUserByWord",method = RequestMethod.GET)
    public List<SysUser> findUserByWord(String word){
        Jedis jedis = JedisUtils.getJedis();
        //先把所有用户查出来
        List<SysUser> listUser =  new ArrayList<>();
        if(jedis.get("listUser")==null) {
            listUser = userService.findAllUser();
            //做个标记，表明已经存入redis
            jedis.set("listUser", "1");
            for (SysUser sysUser : listUser) {
                jedis.set(sysUser.getAccountNumber(), JSON.toJSONString(sysUser));
                jedis.lpush("accountList", sysUser.getAccountNumber());
            }
        }
        else {
            List<String> accountList = jedis.lrange("accountList",0,-1);
            for(String AccountNumber:accountList){
                listUser.add(JSON.parseObject(jedis.get(AccountNumber),SysUser.class));
            }
        }
        //从所有用户里面查询满足条件的
        List<SysUser> SearchListUser =  new ArrayList<>();
        for(SysUser sysUser:listUser){
            if (sysUser.getNickName().contains(word)||sysUser.getAccountNumber().contains(word)){
                SearchListUser.add(sysUser);
            }
        }
        JedisUtils.close(jedis);
        return SearchListUser;
    }

}
