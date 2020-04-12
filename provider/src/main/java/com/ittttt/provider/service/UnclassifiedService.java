package com.ittttt.provider.service;

import com.ittttt.provider.pojo.GroupChat;
import com.ittttt.provider.pojo.NotifyMessage;

import java.util.List;

public interface UnclassifiedService {
    void newGroupChat(String groupName,String groupNumber,int groupCount);
    void addGroupMember(String groupNumber,String accountNumber);
    void updateGroupCount(String groupNumber);
    GroupChat findGroupChatByNumber(String groupNumber);
    List<NotifyMessage> findUnreadInvitation(String toNumber);
    List<String> findMyGroup(String accountNumber);
    List<String> findUsersByGroupNumber(String groupNumber);
    String findLastMessage(String groupNumber);
}
