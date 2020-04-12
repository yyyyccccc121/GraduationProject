package com.ittttt.provider.pojo;

public class DynamicIdAccountNumber {
    //动态主键
    private int id;
    //用户
    private String accountNumber;

    public DynamicIdAccountNumber(){}

    public DynamicIdAccountNumber(int id, String accountNumber) {
        this.id = id;
        this.accountNumber = accountNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "DynamicIdAccountNumber{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
