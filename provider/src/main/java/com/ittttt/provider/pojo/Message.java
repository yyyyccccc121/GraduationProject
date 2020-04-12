package com.ittttt.provider.pojo;

import java.util.Date;

public class Message {
    //消息发送者
    private String fromNumber;
    //消息内容
    private String content;
    //消息接收者
    private String toNumber;
    //消息状态 0表示已读，1表示未读,2表示是已读文件,3表示未读文件,4表示已读语音,5表示未读语音,6表示已读视频或图片,7表示未读
    private String state;
    //消息日期
    private Date theDate;

    public Message(){}

    public Message(String fromNumber, String content, String toNumber, String state, Date theDate) {
        this.fromNumber = fromNumber;
        this.content = content;
        this.toNumber = toNumber;
        this.state = state;
        this.theDate = theDate;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTheDate(Date theDate) {
        this.theDate = theDate;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public String getContent() {
        return content;
    }

    public String getToNumber() {
        return toNumber;
    }

    public String getState() {
        return state;
    }

    public Date getTheDate() {
        return theDate;
    }

    @Override
    public String toString() {
        return "Message{" +
                "fromNumber='" + fromNumber + '\'' +
                ", content='" + content + '\'' +
                ", toNumber='" + toNumber + '\'' +
                ", state='" + state + '\'' +
                ", theDate=" + theDate +
                '}';
    }
}
