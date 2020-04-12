package com.ittttt.provider.pojo;

import java.util.Date;

//群消息
public class GroupMessage {
    //群消息发送者账号
    private String fromNumber;
    //发送的内容
    private String content;
    //群号
    private String groupNumber;
    //群消息日期
    private Date theDate;
    //状态  0表示文本，1表示文件，2表示语音
    private String state;

    public GroupMessage(){}

    public GroupMessage(String fromNumber, String content, String groupNumber, Date theDate,String state) {
        this.fromNumber = fromNumber;
        this.content = content;
        this.groupNumber = groupNumber;
        this.theDate = theDate;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public Date getTheDate() {
        return theDate;
    }

    public void setTheDate(Date theDate) {
        this.theDate = theDate;
    }

    @Override
    public String toString() {
        return "GroupMessage{" +
                "fromNumber='" + fromNumber + '\'' +
                ", content='" + content + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", theDate=" + theDate +
                ", state='" + state + '\'' +
                '}';
    }
}
