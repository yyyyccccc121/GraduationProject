package com.ittttt.provider.controller;

import com.ittttt.provider.pojo.GroupChat;
import com.ittttt.provider.pojo.NotifyMessage;
import com.ittttt.provider.service.UnclassifiedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "unclassified")
public class UnclassifiedController {

    @Autowired
    private UnclassifiedService unclassifiedService;

    @RequestMapping(value = "/newGroupChat")
    public void newGroupChat(String groupName,String groupNumber,int groupCount){
        unclassifiedService.newGroupChat(groupName,groupNumber,groupCount);
    }

    @RequestMapping(value = "/addGroupMember")
    public void addGroupMember(String groupNumber,String accountNumber){
        unclassifiedService.addGroupMember(groupNumber,accountNumber);
    }

    @RequestMapping(value = "/updateGroupCount")
    public void updateGroupCount(String groupNumber){
        unclassifiedService.updateGroupCount(groupNumber);
    }

    @RequestMapping(value = "/findGroupChatByNumber")
    public GroupChat findGroupChatByNumber(String groupNumber){
        GroupChat groupChat = unclassifiedService.findGroupChatByNumber(groupNumber);
        return groupChat;
    }

    @RequestMapping(value = "/findUnreadInvitation")
    public List<NotifyMessage> findUnreadInvitation(String toNumber){
        return unclassifiedService.findUnreadInvitation(toNumber);
    }

    @RequestMapping(value = "/findMyGroup")
    public List<GroupChat> findMyGroup(String accountNumber){
        List<String> myGroupNumberList = unclassifiedService.findMyGroup(accountNumber);
        List<GroupChat> GroupList = new ArrayList<>();
        for (String groupNumber:myGroupNumberList){
            GroupList.add(unclassifiedService.findGroupChatByNumber(groupNumber));
        }
        return GroupList;
    }

    @RequestMapping(value = "/findUsersByGroupNumber")
    public List<String> findUsersByGroupNumber(String groupNumber){
        List<String> UserList = unclassifiedService.findUsersByGroupNumber(groupNumber);
        return UserList;
    }

    @RequestMapping(value = "/findLastMessage")
    public String findLastMessage(String groupNumber){
        String lastMessage = unclassifiedService.findLastMessage(groupNumber);
        return lastMessage;
    }

}
