package com.ittttt.provider.mapper;

import com.ittttt.provider.pojo.Dynamic;
import com.ittttt.provider.pojo.DynamicComment;
import com.ittttt.provider.pojo.DynamicImage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper
public interface SpaceMapper {
    void insertToDynamic(int id, String theTitle, Date theDate);
    void insertToDynamicAccountNumber(int id, String accountNumber);
    void insertToDynamicImage(int id, String route);
    void addAgree(String theId, String names);
    void addComment(String theId, String nickname, String str);
    List<Dynamic> findAllDynamic();
    String findNumberById(int id);
    List<DynamicComment> findAllDynamicComment(int id);
    DynamicImage findImageById(int id);
}
