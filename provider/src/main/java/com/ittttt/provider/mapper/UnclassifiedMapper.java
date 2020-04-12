package com.ittttt.provider.mapper;

import com.ittttt.provider.pojo.GroupChat;
import com.ittttt.provider.pojo.NotifyMessage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UnclassifiedMapper {
    void newGroupChat(String groupName, String groupNumber, int groupCount);
    void addGroupMember(String groupNumber, String accountNumber);
    void updateGroupCount(String groupNumber);
    GroupChat findGroupChatByNumber(String groupNumber);
    List<NotifyMessage> findUnreadInvitation(String toNumber);
    List<String> findMyGroup(String accountNumber);
    List<String> findUsersByGroupNumber(String groupNumber);
    String findLastMessage(String groupNumber);
}
