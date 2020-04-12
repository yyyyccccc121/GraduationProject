package com.ittttt.provider.service;

import com.ittttt.provider.pojo.SysUser;
import com.ittttt.provider.pojo.recommendHead;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    boolean findByaccountnumber(String accountnumber);
    void adduser(String accountnumber,String password,String nickName,String headPortrait);
    String findUrlByname(String accountnumber);
    SysUser findAllByName(String accountnumber);
    void updateUser(String nickName,String sex,String region,String signature,String accountnumber);
    List<recommendHead> findAllHead();
    void updateHead(String finalUrl,String accountNumber);
    List<SysUser> findAllUser();
}
