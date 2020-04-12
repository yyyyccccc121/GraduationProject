package com.boot.pojo;

//群成员
public class GroupMember {
    //群号
    private String groupNumber;
    //用户账号
    private String accountNumber;

    public GroupMember(String groupNumber, String accountNumber) {
        this.groupNumber = groupNumber;
        this.accountNumber = accountNumber;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "groupNumber='" + groupNumber + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
