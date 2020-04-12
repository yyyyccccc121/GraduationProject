package com.ittttt.provider.service;

import com.ittttt.provider.pojo.GroupMessage;
import com.ittttt.provider.pojo.Message;

import java.util.Date;
import java.util.List;

public interface MessageService {
    void addMessageList(String userAccountNumber,String newsAccountNumber);
    List<String> showMessageList(String userAccountNumber);
    void addMessage(String fromNumber, String toNumber, String content, Date theDate,String state);
    List<Message> queryHistoryMessage(String fromNumber, String toNumber);
    void removeMessageList(String userAccountNumber,String newsAccountNumber);
    void changeMessageStatus(String toNumber);
    List<Message> findUnreadMessage(String toNumber);
    void storeToNotify(String myName,String toNumber,String groupName,String groupNumber,String state);
    void changeNotifyMessageStatus(String toNumber);
    void addToGroupMessage(String fromNumber,String groupNumber,String content,Date theDate,String state);
    List<GroupMessage> queryGroupMessage(String groupNumber);
}
