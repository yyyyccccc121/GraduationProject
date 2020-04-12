package com.ittttt.provider.mapper;

import com.ittttt.provider.pojo.SysUser;
import com.ittttt.provider.pojo.recommendHead;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SysUserMapper {
     SysUser findByaccountnumber(String accountnumber);
     void adduser(String accountnumber, String password, String nickName,String headPortrait);
     String findUrlByname(String accountnumber);
     void updateUser(String nickName, String sex, String region, String signature, String accountNumber);
     void updateHead(String finalUrl,String accountNumber);
     List<recommendHead> findAllHead();
     List<SysUser> findAllUser();
}

