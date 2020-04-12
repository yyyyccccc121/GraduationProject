package com.ittttt.provider.pojo;

public class NotifyMessage {
    //发送邀请消息的人
    private String myName;
    //消息接受者
    private String toNumber;
    //群名
    private String groupName;
    //群号
    private String groupNumber;
    //消息状态,0表示已读，1表示未读
    private String state;

    public NotifyMessage(){}

    public NotifyMessage(String myName, String toNumber, String groupName, String groupNumber, String state) {
        this.myName = myName;
        this.toNumber = toNumber;
        this.groupName = groupName;
        this.groupNumber = groupNumber;
        this.state = state;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "NotifyMessage{" +
                "myName='" + myName + '\'' +
                ", toNumber='" + toNumber + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
