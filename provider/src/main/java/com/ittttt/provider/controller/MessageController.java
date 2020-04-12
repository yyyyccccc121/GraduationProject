package com.ittttt.provider.controller;

import com.ittttt.provider.pojo.GroupMessage;
import com.ittttt.provider.pojo.Message;
import com.ittttt.provider.pojo.SysUser;
import com.ittttt.provider.service.MessageService;
import com.ittttt.provider.service.UserService;
import com.ittttt.provider.utils.JedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import java.util.*;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

//    private static Jedis jedis;
//    static{
//        jedis = JedisUtils.getJedis();
//    }

    @RequestMapping(value ="/addMessageList",method = RequestMethod.GET)
    public String addMessageList(String userAccountNumber,String newsAccountNumber){
        Jedis jedis = JedisUtils.getJedis();
        //存在就不添加
        List<String> list = jedis.lrange("messageList"+userAccountNumber,0,-1);
        if(list.contains(newsAccountNumber)){
            return "alreadyExist";
        }
        //不存在
        messageService.addMessageList(userAccountNumber,newsAccountNumber);
        jedis.lpush("messageList"+userAccountNumber,newsAccountNumber);
        JedisUtils.close(jedis);
        return "success";
    }

    @RequestMapping(value = "/showMessageList",method = RequestMethod.GET)
    public List<SysUser> showMessageList(String userAccountNumber){
        Jedis jedis = JedisUtils.getJedis();
        List<String> list = new ArrayList<>();
        //以messageList+账号作为键,其他人的账号集合作为值
        if(!jedis.exists("messageList"+userAccountNumber)) {
            list = messageService.showMessageList(userAccountNumber);
            for (String k : list) {
                jedis.lpush("messageList"+userAccountNumber, k);
            }
        }
        list = jedis.lrange("messageList"+userAccountNumber,0,-1);
        JedisUtils.close(jedis);
        List<SysUser> userList = new ArrayList<>();
        for(String n:list){
            userList.add(userService.findAllByName(n));
        }
        return userList;
    }

    @RequestMapping(value = "/removeMessageList",method = RequestMethod.GET)
    public void removeMessageList(String userAccountNumber,String newsAccountNumber){
        Jedis jedis = JedisUtils.getJedis();
        messageService.removeMessageList(userAccountNumber,newsAccountNumber);
        jedis.lrem("messageList"+userAccountNumber,0,newsAccountNumber);
        JedisUtils.close(jedis);
    }

    @RequestMapping(value = "/storeToDatabase",method = RequestMethod.GET)
    public void storeToDatabase(String fromNumber, String toNumber, String content,String state){
        Date theDate = new Date();
        messageService.addMessage(fromNumber,toNumber,content,theDate,state);
    }

    @RequestMapping(value = "/queryHistoryMessage",method = RequestMethod.GET)
    public List<Message> queryHistoryMessage(String toNumber,String fromNumber){
        List<Message> list = new ArrayList<>();
        //查询别人发给我的消息
        List<Message> list1 = messageService.queryHistoryMessage(fromNumber,toNumber);
        //查询我发给别人的消息
        List<Message> list2 = messageService.queryHistoryMessage(toNumber,fromNumber);
        list.addAll(list1);
        list.addAll(list2);
        //按日期排序
        Collections.sort(list,new Comparator<Message>(){
            public int compare(Message p1, Message p2) {
                long diff = p1.getTheDate().getTime() - p2.getTheDate().getTime();
                if (diff > 0) {
                    return 1;
                }else if (diff < 0) {
                    return -1;
                }
                return 0;
            }
        });
        return list;
    }

    @RequestMapping(value = "/changeMessageStatus",method = RequestMethod.GET)
    public void changeMessageStatus(String toNumber){
        messageService.changeMessageStatus(toNumber);
    }

    @RequestMapping(value = "/findUnreadMessage",method = RequestMethod.GET)
    public List<Message> findUnreadMessage(String toNumber){
        List<Message> list = messageService.findUnreadMessage(toNumber);
        messageService.changeMessageStatus(toNumber);
        return list;
    }

    @RequestMapping(value = "/storeToNotify")
    public void storeToNotify(String myName,String toNumber,String groupName,String groupNumber){
        messageService.storeToNotify(myName,toNumber,groupName,groupNumber,"1");
    }

    @RequestMapping(value = "/changeNotifyMessageStatus",method = RequestMethod.GET)
    public void changeNotifyMessageStatus(String toNumber){
        messageService.changeNotifyMessageStatus(toNumber);
    }

    @RequestMapping(value = "/addToGroupMessage")
    public void addToGroupMessage(String fromNumber,String groupNumber,String content,String state){
        Date theDate = new Date();
        messageService.addToGroupMessage(fromNumber,groupNumber,content,theDate,state);
    }

    @RequestMapping(value = "/queryGroupMessage")
    public List<GroupMessage> queryGroupMessage(String groupNumber){
        List<GroupMessage> list = messageService.queryGroupMessage(groupNumber);
        return list;
    }
}
