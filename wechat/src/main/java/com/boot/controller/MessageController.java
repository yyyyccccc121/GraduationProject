package com.boot.controller;

import com.alibaba.fastjson.JSON;
import com.boot.pojo.GroupChat;
import com.boot.pojo.GroupMessage;
import com.boot.pojo.Message;
import com.boot.pojo.SysUser;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.security.Principal;
import java.util.*;

import static org.thymeleaf.util.StringUtils.substring;

@Controller
public class MessageController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private SimpMessagingTemplate template;

    @RequestMapping(value = "message/addMessageList",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject addMessageList(String accountNumber){
        String userAccountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String Message = restTemplate.getForObject("http://PROVIDER-TICKET//addMessageList?userAccountNumber="+userAccountNumber+"&newsAccountNumber="+accountNumber, String.class);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("message",Message);
        return jsonObject;
    }

    @RequestMapping(value = "message/showMessageList",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> showMessageList(){
        String userAccountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        //查询我的打开的消息列表
        String json1 =  restTemplate.getForObject("http://PROVIDER-TICKET//showMessageList?userAccountNumber="+userAccountNumber,String.class);
        List<SysUser> MessageList = JSON.parseArray(json1, SysUser.class);
        //查询和每个人的最后一次消息
        List<String> lastMessageList =  new ArrayList<String>();
        for(SysUser sysUser:MessageList){
            String a = sysUser.getAccountNumber();
            String json = restTemplate.getForObject("http://PROVIDER-TICKET//queryHistoryMessage?toNumber="+userAccountNumber+"&fromNumber="+a,String.class);
            List<Message> list = JSON.parseArray(json, Message.class);
            if(null == list || list.size() ==0){
                lastMessageList.add(" ");
            }
            else {
                //普通消息
                if(list.get(list.size()-1).getState().equals("0")||list.get(list.size()-1).getState().equals("1")){
                    lastMessageList.add(list.get(list.size()-1).getContent());
                //文件消息
                }else if(list.get(list.size()-1).getState().equals("2")||list.get(list.size()-1).getState().equals("3")){
                    String realFileName = restTemplate.getForObject("http://PROVIDER-TICKET//findRealFileName?uuidFileName="+list.get(list.size()-1).getContent(),String.class);
                    lastMessageList.add(realFileName);
                //图片视频
                }else if(list.get(list.size()-1).getState().equals("6")||list.get(list.size()-1).getState().equals("7")){
                    lastMessageList.add("[图片]");
                //语音消息
                }else{
                    lastMessageList.add("[语音]");
                }

            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("MessageList", MessageList);
        map.put("lastMessageList",lastMessageList);
        return map;
    }

    //单聊中转
    @MessageMapping(value = "/userChat")
    public void sendMessage(JSONObject jsonObject,Principal principal) {
        String accountNumber = principal.getName();
        String content = jsonObject.getString("content");
        String toNumber = jsonObject.getString("toNumber");
        //找到需要发送的地址
        String dest = "/userChat/chat";
        Message message = null;
        Map<String,Object> map = new HashedMap();

        //判断是否是文件
        if(content.startsWith("Y")&&content.endsWith("C")){
            String uuidFileName = substring(content,1,content.length()-1);
            //消息存入数据库，状态设置为未读文件
            storeToDatabase(accountNumber,toNumber,uuidFileName,"3");
            message = new Message(accountNumber,uuidFileName,toNumber);
            map.put("type","3");
            //根据唯一文件名查真实文件名
            String realFileName = restTemplate.getForObject("http://PROVIDER-TICKET//findRealFileName?uuidFileName="+uuidFileName,String.class);
            map.put("uuidFileName",uuidFileName);
            map.put("realFileName",realFileName);
        //判断是否是语音
        }else if (content.startsWith("Y")&&content.endsWith("F")){
            String uuidAudioName = substring(content,1,content.length()-1);
            //消息存入数据库，状态设置为未读语音
            storeToDatabase(accountNumber,toNumber,uuidAudioName,"5");
            message = new Message(accountNumber,uuidAudioName,toNumber);
            map.put("type","5");
            map.put("uuidAudioName",uuidAudioName);
            //根据id查语音时间
            String theTime = restTemplate.getForObject("http://PROVIDER-TICKET//findTheTimeById?uuidAudioName="+uuidAudioName,String.class);
            map.put("theTime",theTime);
        //视频或图片
        }else if(content.startsWith("Y")&&content.endsWith("Z")){
            String uuidFileName = substring(content,1,content.length()-1);
            //消息存入数据库，状态设置为未读
            storeToDatabase(accountNumber,toNumber,uuidFileName,"7");
            message = new Message(accountNumber,uuidFileName,toNumber);
            map.put("type","7");
            map.put("uuidFileName",uuidFileName);
        }else {
            //消息存入数据库，状态设置为未读
            storeToDatabase(accountNumber,toNumber,content,"1");
            message = new Message(accountNumber,content,toNumber);
            map.put("type","1");
        }

        map.put("message",message);
        map.put("headPortrait",jsonObject.getString("headPortrait"));
        //单聊用的发送方法,convertAndSendToUser底层还是用的convertAndSend
        template.convertAndSendToUser(message.getToNumber(),dest,map);
    }

    //通知中转
    @MessageMapping(value = "/invitation")
    public void sendInvitationMessage(JSONObject jsonObject,Principal principal) {
        String accountNumber = principal.getName();
        String toNumber = jsonObject.getString("toNumber");
        String groupNumber = jsonObject.getString("groupNumber");
        //查询当前这个群是否存在,存在表示是邀请好友
        GroupChat groupChat = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/findGroupChatByNumber?groupNumber="+groupNumber,GroupChat.class);
        if (groupChat!=null){
            //查询这个群的所有人
            List<String> UserList = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/findUsersByGroupNumber?groupNumber="+groupNumber,List.class);
            boolean ff = UserList.contains(toNumber);
            if (ff == true) {

            } else {
                SysUser sysUser = restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber=" + accountNumber, SysUser.class);
                String groupName = jsonObject.getString("groupName");

                //邀请消息存入数据库，状态设置为未读
                String str = restTemplate.getForObject("http://PROVIDER-TICKET//storeToNotify?myName=" + sysUser.getNickName() + "&toNumber=" + toNumber + "&groupName=" + groupName + "&groupNumber=" + groupNumber, String.class);

                //发送地址
                String dest = "/invitation/chat";
                Map<String, Object> map = new HashedMap();
                map.put("myName", sysUser.getNickName());
                map.put("groupName", groupName);
                map.put("groupNumber", groupNumber);
                map.put("toNumber", toNumber);
                template.convertAndSendToUser(toNumber, dest, map);
            }
        }else {

            //不能自己邀请自己
            if (toNumber == accountNumber || toNumber.equals(accountNumber)) {

            } else {
                SysUser sysUser = restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber=" + accountNumber, SysUser.class);
                String groupName = jsonObject.getString("groupName");

                //邀请消息存入数据库，状态设置为未读
                String str = restTemplate.getForObject("http://PROVIDER-TICKET//storeToNotify?myName=" + sysUser.getNickName() + "&toNumber=" + toNumber + "&groupName=" + groupName + "&groupNumber=" + groupNumber, String.class);

                //发送地址
                String dest = "/invitation/chat";
                Map<String, Object> map = new HashedMap();
                map.put("myName", sysUser.getNickName());
                map.put("groupName", groupName);
                map.put("groupNumber", groupNumber);
                map.put("toNumber", toNumber);
                template.convertAndSendToUser(toNumber, dest, map);
            }
        }

    }

    //群聊中转
    @MessageMapping(value = "/all")
    public void sendGroupMessage(JSONObject jsonObject,Principal principal){
        String accountNumber = principal.getName();
        String content = jsonObject.getString("content");
        String toNumber = jsonObject.getString("toNumber");
        String dest = "/all/chat";
        //查这个群里有哪些人
        List<String> UserList = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/findUsersByGroupNumber?groupNumber="+toNumber,List.class);
        //获取头像
        SysUser sysUser = restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+accountNumber,SysUser.class);
        String headPortrait = sysUser.getHeadPortrait();
        Map<String,Object> map = new HashedMap();
        map.put("headPortrait",headPortrait);
        map.put("content",content);
        map.put("toNumber",toNumber);
        String state = null;
        //判断是否是文件
        if(content.startsWith("Y")&&content.endsWith("C")) {
            String uuidFileName = substring(content,1,content.length()-1);
            //根据唯一文件名查真实文件名
            String realFileName = restTemplate.getForObject("http://PROVIDER-TICKET//findRealFileName?uuidFileName="+uuidFileName,String.class);
            //把消息存进数据库
            state = "1";
            String str = restTemplate.getForObject("http://PROVIDER-TICKET//addToGroupMessage?fromNumber="+accountNumber+"&groupNumber="+toNumber+"&content="+uuidFileName+"&state="+state,String.class);
            map.put("uuidFileName",uuidFileName);
            map.put("realFileName",realFileName);
            map.put("type","1");
        //判断是否是语音
        }else if (content.startsWith("Y")&&content.endsWith("F")){
            String uuidAudioName = substring(content,1,content.length()-1);
            //把消息存进数据库
            state = "2";
            String str = restTemplate.getForObject("http://PROVIDER-TICKET//addToGroupMessage?fromNumber="+accountNumber+"&groupNumber="+toNumber+"&content="+uuidAudioName+"&state="+state,String.class);
            map.put("uuidAudioName",uuidAudioName);
            //根据id查语音时间
            String theTime = restTemplate.getForObject("http://PROVIDER-TICKET//findTheTimeById?uuidAudioName="+uuidAudioName,String.class);
            map.put("theTime",theTime);
            map.put("type","2");
        //视频或图片
        }else if(content.startsWith("Y")&&content.endsWith("Z")){
            String uuidFileName = substring(content,1,content.length()-1);
            //消息存入数据库
            state = "3";
            String str = restTemplate.getForObject("http://PROVIDER-TICKET//addToGroupMessage?fromNumber="+accountNumber+"&groupNumber="+toNumber+"&content="+uuidFileName+"&state="+state,String.class);
            map.put("type","3");
            map.put("uuidFileName",uuidFileName);
        }else {
            map.put("type","0");
            state = "0";
            //把消息存进数据库
            String str = restTemplate.getForObject("http://PROVIDER-TICKET//addToGroupMessage?fromNumber="+accountNumber+"&groupNumber="+toNumber+"&content="+content+"&state="+state,String.class);
        }
        //把消息发给所有人(除了自己),不管在没在线，反正数据库里可以查
        for (String n:UserList){
            if(!n.equals(accountNumber)) {
                template.convertAndSendToUser(n, dest, map);
            }
        }
    }

    //游戏中转
    @MessageMapping(value = "/game")
    public void sendGameMessage(JSONObject jsonObject,Principal principal) {

        String accountNumber = principal.getName();
        SysUser sysUser =  restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+accountNumber, SysUser.class);
        String myName = sysUser.getNickName();
        String toNumber = jsonObject.getString("toNumber");
        String type = jsonObject.getString("type");
        Map<String, Object> map = new HashedMap();
        //发送地址
        String dest = "/game/chat";
        if (accountNumber!=toNumber) {
            if (type.equals("GoBangInvite")) {
                map.put("theType", "GoBangInvite");
            } else if (type.equals("GoBangTrue")) {
                map.put("theType", "GoBangTrue");
            } else if (type.equals("GoBangFalse")) {
                map.put("theType", "GoBangFalse");
            } else if (type.equals("GoBangPK")){
                int fx = jsonObject.getInt("fx");
                int fy = jsonObject.getInt("fy");
                int theValue = jsonObject.getInt("theValue");
                map.put("fx", fx);
                map.put("fy", fy);
                map.put("theValue", theValue);
                map.put("theType", "GoBangPK");
            }
            map.put("myName", myName);
            map.put("accountNumber", accountNumber);
            template.convertAndSendToUser(toNumber, dest, map);
        }
    }

    @RequestMapping(value = "message/storeToDatabase",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject storeToDatabase(String fromNumber,String toNumber,String content,String state){
        String str = restTemplate.getForObject("http://PROVIDER-TICKET//storeToDatabase?fromNumber="+fromNumber+"&toNumber="+toNumber+"&content="+content+"&state="+state,String.class);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("message",str);
        return jsonObject;
    }

    @RequestMapping(value = "message/queryHistoryMessage",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> queryHistoryMessage(String toAccountNumber){
        String fromNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String json = restTemplate.getForObject("http://PROVIDER-TICKET//queryHistoryMessage?toNumber="+toAccountNumber+"&fromNumber="+fromNumber,String.class);
        List<Message> list = JSON.parseArray(json, Message.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        return map;
    }

    @RequestMapping(value = "message/removeMessageList",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject removeMessageList(String accountNumber){
        String userAccountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        restTemplate.getForObject("http://PROVIDER-TICKET//removeMessageList?userAccountNumber="+userAccountNumber+"&newsAccountNumber="+accountNumber,String.class);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("message","ok");
        return jsonObject;
    }

    @RequestMapping(value = "message/changeMessageStatus",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject changeMessageStatus(String toNumber){
        restTemplate.getForObject("http://PROVIDER-TICKET//changeMessageStatus?toNumber="+toNumber,String.class);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("message","ok");
        return jsonObject;
    }

    @RequestMapping(value = "message/findUnreadMessage",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> findUnreadMessage(){
        String toNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String json = restTemplate.getForObject("http://PROVIDER-TICKET//findUnreadMessage?toNumber="+toNumber,String.class);
        List<Message> list = JSON.parseArray(json, Message.class);
        for (Message message:list){
            //如果是未读文件
            if(message.getState().equals("3")||message.getState().equals('3')){
                String realFileName = restTemplate.getForObject("http://PROVIDER-TICKET//findRealFileName?uuidFileName="+message.getContent(),String.class);
                message.setContent(realFileName);
            }
            //如果是未读语音
            if(message.getState().equals("5")||message.getState().equals('5')){
                message.setContent("[语音]");
            }
            //如果是未读图片视频
            if(message.getState().equals("7")||message.getState().equals('7')){
                message.setContent("[图片]");
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("unreadMessageList",list);
        return map;
    }

    @RequestMapping(value = "message/changeNotifyMessageStatus",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject changeNotifyMessageStatus(String toNumber){
        restTemplate.getForObject("http://PROVIDER-TICKET//changeNotifyMessageStatus?toNumber="+toNumber,String.class);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("message","ok");
        return jsonObject;
    }

    @RequestMapping(value = "/queryGroupMessage")
    @ResponseBody
    public Map<String,Object> queryGroupMessage(String groupNumber){
        String myNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String json = restTemplate.getForObject("http://PROVIDER-TICKET//queryGroupMessage?groupNumber="+groupNumber,String.class);
        List<GroupMessage> GroupMessageList = JSON.parseArray(json, GroupMessage.class);
        Map<String, Object> map = new HashMap<String, Object>();
        for (GroupMessage groupMessage:GroupMessageList){
            String fromNumber = groupMessage.getFromNumber();
            SysUser sysUser =  restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+fromNumber, SysUser.class);
            map.put(fromNumber,sysUser.getHeadPortrait());
        }
        map.put("GroupMessageList",GroupMessageList);
        map.put("myNumber",myNumber);
        return map;
    }
}
