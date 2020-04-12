package com.ittttt.provider.service.Impl;

import com.ittttt.provider.mapper.MessageMapper;
import com.ittttt.provider.pojo.GroupMessage;
import com.ittttt.provider.pojo.Message;
import com.ittttt.provider.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void addMessageList(String userAccountNumber, String newsAccountNumber) {
        messageMapper.addMessageList(userAccountNumber,newsAccountNumber);
    }

    @Override
    public List<String> showMessageList(String userAccountNumber) {
        return messageMapper.showMessageList(userAccountNumber);
    }

    @Override
    public void addMessage(String fromNumber, String toNumber, String content, Date theDate,String state) {
        messageMapper.addMessage(fromNumber,toNumber,content,theDate,state);
    }

    @Override
    public List<Message> queryHistoryMessage(String fromNumber, String toNumber) {
        return messageMapper.queryHistoryMessage(fromNumber,toNumber);
    }

    @Override
    public void removeMessageList(String userAccountNumber, String newsAccountNumber) {
        messageMapper.removeMessageList(userAccountNumber,newsAccountNumber);
    }

    @Override
    public void changeMessageStatus(String toNumber) {
        messageMapper.changeMessageStatus(toNumber,"0","1");
        messageMapper.changeMessageStatus(toNumber,"2","3");
        messageMapper.changeMessageStatus(toNumber,"4","5");
        messageMapper.changeMessageStatus(toNumber,"6","7");
    }

    @Override
    public List<Message> findUnreadMessage(String toNumber) {
        return messageMapper.findUnreadMessage(toNumber);
    }

    @Override
    public void storeToNotify(String myName, String toNumber, String groupName, String groupNumber, String state) {
        messageMapper.storeToNotify(myName,toNumber,groupName,groupNumber,state);
    }

    @Override
    public void changeNotifyMessageStatus(String toNumber) {
        messageMapper.changeNotifyMessageStatus(toNumber);
    }

    @Override
    public void addToGroupMessage(String fromNumber, String groupNumber, String content, Date theDate,String state) {
        messageMapper.addToGroupMessage(fromNumber,groupNumber,content,theDate,state);
    }

    @Override
    public List<GroupMessage> queryGroupMessage(String groupNumber) {
        return messageMapper.queryGroupMessage(groupNumber);
    }

}
