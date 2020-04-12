package com.ittttt.provider.mapper;

import com.ittttt.provider.pojo.GroupMessage;
import com.ittttt.provider.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper
public interface MessageMapper {
    void addMessageList(String userAccountNumber,String newsAccountNumber);
    List<String> showMessageList(String userAccountNumber);
    void addMessage(String fromNumber, String toNumber, String content, Date theDate,String state);
    List<Message> queryHistoryMessage(String fromNumber, String toNumber);
    void removeMessageList(String userAccountNumber,String newsAccountNumber);
    void changeMessageStatus(String toNumber,String state1,String state2);
    List<Message> findUnreadMessage(String toNumber);
    void storeToNotify(String myName, String toNumber, String groupName, String groupNumber, String state);
    void changeNotifyMessageStatus(String toNumber);
    void addToGroupMessage(String fromNumber, String groupNumber, String content, Date theDate,String state);
    List<GroupMessage> queryGroupMessage(String groupNumber);
}
