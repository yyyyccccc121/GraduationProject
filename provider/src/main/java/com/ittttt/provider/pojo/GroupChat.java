package com.ittttt.provider.pojo;

//群聊
public class GroupChat {
    //群号
    private String groupNumber;
    //群名称
    private String groupName;
    //群人数
    private int groupCount;

    public GroupChat(){}

    public GroupChat(String groupNumber, String groupName, int groupCount) {
        this.groupNumber = groupNumber;
        this.groupName = groupName;
        this.groupCount = groupCount;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }

    @Override
    public String toString() {
        return "GroupChat{" +
                "groupNumber='" + groupNumber + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupCount=" + groupCount +
                '}';
    }
}
