package com.boot.pojo;

public class SysUser {
    /** 主键 */
    private Long id;

    /** 昵称. */
    private String nickName;

    /** 账号. */
    private String accountNumber;

    /** 密码. */
    private String password;

    private String headPortrait;

    private String sex;

    private String region;

    private String signature;

    public SysUser() {}

    public SysUser(String nickName, String headPortrait, String sex, String region, String signature) {
        this.nickName = nickName;
        this.headPortrait = headPortrait;
        this.sex = sex;
        this.region = region;
        this.signature = signature;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", password='" + password + '\'' +
                ", headPortrait='" + headPortrait + '\'' +
                ", sex='" + sex + '\'' +
                ", region='" + region + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
