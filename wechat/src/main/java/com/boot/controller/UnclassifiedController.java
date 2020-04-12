package com.boot.controller;

import com.alibaba.fastjson.JSON;
import com.boot.pojo.GroupChat;
import com.boot.pojo.NotifyMessage;
import com.boot.pojo.SysUser;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping(value = "unclassified")
public class UnclassifiedController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/newGroupChat")
    public Map<String,String> newGroupChat(String groupName){
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        //群人数
        int groupCount = 1;
        Random random = new Random();
        //生成群号
        String groupNumber="";
        for(int i=0;i<8;i++){
            //首字母不能为0
            groupNumber += (random.nextInt(9)+1);
        }
        //添加群
        String str = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/newGroupChat?groupName="+groupName+"&groupNumber="+groupNumber+"&groupCount="+groupCount,String.class);
        //群成员类添加数据
        String str1 = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/addGroupMember?groupNumber="+groupNumber+"&accountNumber="+accountNumber,String.class);
        //查我的名字
        SysUser sysUser = restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+accountNumber, SysUser.class);
        Map<String, String> map = new HashMap<String, String>();
        map.put("groupNumber",groupNumber);
        map.put("accountNumber",accountNumber);
        map.put("myName",sysUser.getNickName());
        return map;
    }

    @RequestMapping(value = "/addGroupMember")
    public JSONObject addGroupMember(String groupNumber,String accountNumber){
        //群成员类添加数据
        String str = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/addGroupMember?groupNumber="+groupNumber+"&accountNumber="+accountNumber,String.class);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("message","ok");
        return jsonObject;
    }

    @RequestMapping(value = "/updateGroupCount")
    public JSONObject updateGroupCount(String groupNumber){
        String str = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/updateGroupCount?groupNumber="+groupNumber,String.class);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("message","ok");
        return jsonObject;
    }

    @RequestMapping(value = "/findGroupChatByNumber")
    public Map<String,Integer> findGroupChatByNumber(String groupNumber){
        GroupChat groupChat = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/findGroupChatByNumber?groupNumber="+groupNumber,GroupChat.class);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("count",groupChat.getGroupCount());
        return map;
    }

    @RequestMapping(value = "/findUnreadInvitation")
    public Map<String,Object> findUnreadInvitation(){
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String json = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/findUnreadInvitation?toNumber="+accountNumber,String.class);
        //设置为已读
        restTemplate.getForObject("http://PROVIDER-TICKET//changeNotifyMessageStatus?toNumber="+accountNumber,String.class);
        List<NotifyMessage> NotifyMessageList = JSON.parseArray(json, NotifyMessage.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("NotifyMessageList",NotifyMessageList);
        return map;
    }

    @RequestMapping(value = "/findMyGroup")
    public Map<String,Object> findMyGroup(){
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String json = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/findMyGroup?accountNumber="+accountNumber,String.class);
        List<GroupChat> GroupList = JSON.parseArray(json, GroupChat.class);
        //查询每个群的最后一次消息
        List<String> lastMessageList =  new ArrayList<String>();
        for (GroupChat groupChat:GroupList){
            String groupNumber = groupChat.getGroupNumber();
            String lastMessage = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/findLastMessage?groupNumber="+groupNumber,String.class);
            lastMessageList.add(lastMessage);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("GroupList",GroupList);
        map.put("lastMessageList",lastMessageList);
        return map;
    }

    @RequestMapping(value = "/findUsersByGroupNumber")
    public Map<String,Object> findUsersByGroupNumber(String groupNumber) {
        //查这个群里有哪些人
        List<String> UserList = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/findUsersByGroupNumber?groupNumber=" + groupNumber, List.class);
        List<SysUser> sysUserList = new ArrayList<SysUser>();
        for (String number:UserList){
            SysUser sysUser = restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+number, SysUser.class);
            sysUserList.add(sysUser);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sysUserList",sysUserList);
        return map;
    }

    @RequestMapping(value = "/findGroup")
    public Map<String,Object> findGroup(String groupNumber){
        GroupChat groupChat = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/findGroupChatByNumber?groupNumber="+groupNumber,GroupChat.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("groupChat",groupChat);
        return map;
    }

    @RequestMapping(value = "/findAllGame")
    public Map<String,String> findAllGame(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("gg","其实什么也没查");
        return map;
    }

    @RequestMapping(value = "/judgeMe")
    public Map<String,String> judgeMe(String groupNumber){
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        //查询这个群的所有人
        List<String> UserList = restTemplate.getForObject("http://PROVIDER-TICKET//unclassified/findUsersByGroupNumber?groupNumber="+groupNumber,List.class);
        boolean ff = UserList.contains(accountNumber);
        Map<String, String> map = new HashMap<String, String>();
        if (ff){
            //我在群里面
            map.put("cc","1");
        }else {
            map.put("cc","2");
        }

        return map;
    }
}
