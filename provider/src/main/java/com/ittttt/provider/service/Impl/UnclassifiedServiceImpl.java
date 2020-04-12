package com.ittttt.provider.service.Impl;

import com.ittttt.provider.mapper.UnclassifiedMapper;
import com.ittttt.provider.pojo.GroupChat;
import com.ittttt.provider.pojo.NotifyMessage;
import com.ittttt.provider.service.UnclassifiedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnclassifiedServiceImpl implements UnclassifiedService {

    @Autowired
    private UnclassifiedMapper unclassifiedMapper;

    @Override
    public void newGroupChat(String groupName, String groupNumber, int groupCount) {
        unclassifiedMapper.newGroupChat(groupName,groupNumber,groupCount);
    }

    @Override
    public void addGroupMember(String groupNumber, String accountNumber) {
        unclassifiedMapper.addGroupMember(groupNumber,accountNumber);
    }

    @Override
    public void updateGroupCount(String groupNumber) {
        unclassifiedMapper.updateGroupCount(groupNumber);
    }

    @Override
    public GroupChat findGroupChatByNumber(String groupNumber) {
        return unclassifiedMapper.findGroupChatByNumber(groupNumber);
    }

    @Override
    public List<NotifyMessage> findUnreadInvitation(String toNumber) {
        return unclassifiedMapper.findUnreadInvitation(toNumber);
    }

    @Override
    public List<String> findMyGroup(String accountNumber) {
        return unclassifiedMapper.findMyGroup(accountNumber);
    }

    @Override
    public List<String> findUsersByGroupNumber(String groupNumber) {
        return unclassifiedMapper.findUsersByGroupNumber(groupNumber);
    }

    @Override
    public String findLastMessage(String groupNumber) {
        return unclassifiedMapper.findLastMessage(groupNumber);
    }
}
