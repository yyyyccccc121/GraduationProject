package com.boot.controller;

import com.alibaba.fastjson.JSON;
import com.boot.pojo.Dynamic;
import com.boot.pojo.DynamicComment;
import com.boot.pojo.DynamicImage;
import com.boot.pojo.SysUser;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.boot.controller.FileController.SaveFileFromInputStream;
import static okhttp3.internal.http.HttpDate.parse;

@RestController
@RequestMapping(value = "space")
public class SpaceController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/findMe")
    public Map<String,Object> findMe(){
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser sysUser =  restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+accountNumber, SysUser.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sysUser", sysUser);
        return map;
    }

    @RequestMapping(value = "/findSpace")
    public Map<String,Object> findSpace() throws ParseException {
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser Me =  restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+accountNumber, SysUser.class);
        Map<String,Object> map = new HashedMap();
        //查所有Dynamic
        String json = restTemplate.getForObject("http://PROVIDER-TICKET//space/findAllDynamic",String.class);
        List<Dynamic> dynamicList = JSON.parseArray(json, Dynamic.class);
        List<String> dateList = new ArrayList<String>();
        Date date = new Date();
        for (Dynamic dynamic:dynamicList){

            long diff = date.getTime() - dynamic.getTheDate().getTime();//这样得到的差值是毫秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            String ddd = "";
            if (days!=0){
                ddd += days+"天前";
            }
            else if (hours!=0){
                ddd += hours+"小时前";
            }else if (minutes!=0){
                ddd += minutes+"分钟前";
            }else{
                ddd += "不到一分钟";
            }
            dateList.add(ddd);

            int id = dynamic.getId();
            //根据id查是谁发的
            String number = restTemplate.getForObject("http://PROVIDER-TICKET//space/findNumberById?id="+id,String.class);
            SysUser sysUser =  restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+number, SysUser.class);
            map.put("SysUser"+id,sysUser);
            //根据id查所有评论,到时候根据Comment+id来查对应评论
            String json1 = restTemplate.getForObject("http://PROVIDER-TICKET//space/findAllDynamicComment?id="+id,String.class);
            List<DynamicComment> dynamicCommentList = JSON.parseArray(json1, DynamicComment.class);
            map.put("Comment"+id,dynamicCommentList);
            //根据id查路径,Image+id来查对应路径
            DynamicImage dynamicImage = restTemplate.getForObject("http://PROVIDER-TICKET//space/findImageById?id="+id,DynamicImage.class);;
            File file = new File(dynamicImage.getRoute());		//获取其file对象
            File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
            List<String> fileList = new ArrayList<String>();
            List<String> fileTypeList = new ArrayList<String>();

            for(File f:fs){					//遍历File[]数组
                if(!f.isDirectory()) {    //若非目录(即文件)，则打印
                    String filename = f.getName();
                    //IE浏览器获取的文件名会自带盘符,这里需要截取一下
                    int unixSep = filename.lastIndexOf('/');
                    int winSep = filename.lastIndexOf('\\');
                    int pos = (winSep > unixSep ? winSep : unixSep);
                    if (pos != -1)  {
                        filename = filename.substring(pos + 1);
                    }
                    fileList.add(filename);
                    String Suffix = filename.substring(filename.lastIndexOf(".") + 1);
                    if (Suffix.equals("mp4")||Suffix=="mp4"){
                        //1表示视频
                        fileTypeList.add("1");
                    }else {
                        fileTypeList.add("0");
                    }
                }
            }
            map.put("Image"+id,fileList);
            map.put("ImageType"+id,fileTypeList);
        }

        map.put("dateList",dateList);
        map.put("dynamicList",dynamicList);
        map.put("myName",Me.getNickName());

        return map;
    }

    @RequestMapping(value = "/publish")
    public Map<String,Object> publish(@RequestParam("file") MultipartFile[] files,String thePublishMessage) throws IOException {
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        //生成id
        Random random = new Random();
        String dynamicNumber="";
        for(int i=0;i<9;i++){
            //首字母不能为0
            dynamicNumber += (random.nextInt(9)+1);
        }

        int id = Integer.parseInt(dynamicNumber);

        //上传文件存到服务器
        //保存到服务器的路径
        String a = "F:/XXXXX/" + dynamicNumber;
        for (MultipartFile file:files) {
            if(StringUtils.isNotBlank(file.getOriginalFilename())) {
                //目标文件的对象
                String fileName = file.getOriginalFilename();
                //IE浏览器获取的文件名会自带盘符,这里需要截取一下
                int unixSep = fileName.lastIndexOf('/');
                int winSep = fileName.lastIndexOf('\\');
                int pos = (winSep > unixSep ? winSep : unixSep);
                if (pos != -1)  {
                    fileName = fileName.substring(pos + 1);
                }
                SaveFileFromInputStream(file.getInputStream(), a, fileName);
            }
        }

        Date theDate = new Date();

        //存入数据库
        restTemplate.getForObject("http://PROVIDER-TICKET//space/insertToDynamic?id="+id+"&theTitle="+thePublishMessage+"&theDate="+theDate, String.class);
        restTemplate.getForObject("http://PROVIDER-TICKET//space/insertToDynamicAccountNumber?id="+id+"&accountNumber="+accountNumber, String.class);
        restTemplate.getForObject("http://PROVIDER-TICKET//space/insertToDynamicImage?id="+id+"&route="+a, String.class);
        //自己显示出来
        List<String> fileList = new ArrayList<String>();
        List<String> fileTypeList = new ArrayList<String>();
        Map<String, Object> map = new HashMap<String, Object>();
        File file = new File(a);		//获取其file对象
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        for(File f:fs){					//遍历File[]数组
            if(!f.isDirectory()) {    //若非目录(即文件)，则打印
                String filename = f.getName();
                //IE浏览器获取的文件名会自带盘符,这里需要截取一下
                int unixSep = filename.lastIndexOf('/');
                int winSep = filename.lastIndexOf('\\');
                int pos = (winSep > unixSep ? winSep : unixSep);
                if (pos != -1)  {
                    filename = filename.substring(pos + 1);
                }
                fileList.add(filename);
                String Suffix = filename.substring(filename.lastIndexOf(".") + 1);
                if (Suffix.equals("mp4")||Suffix=="mp4"){
                    //1表示视频
                    fileTypeList.add("1");
                }else {
                    fileTypeList.add("0");
                }
            }
        }
        map.put("fileList",fileList);
        map.put("fileTypeList",fileTypeList);
        SysUser sysUser =  restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+accountNumber, SysUser.class);
        map.put("thePublishMessage",thePublishMessage);
        map.put("theDate","小于1分钟");
        map.put("headPortrait",sysUser.getHeadPortrait());
        map.put("nickname",sysUser.getNickName());
        map.put("id",id);
        return map;
    }

    @RequestMapping(value = "/addAgree")
    public Map<String,String> addAgree(String theId,String names){
        String  s =  restTemplate.getForObject("http://PROVIDER-TICKET//space/addAgree?theId="+theId+"&names="+names, String.class);
        Map<String,String> map = new HashedMap();
        return map;
    }

    @RequestMapping(value = "/addComment")
    public Map<String,String> addComment(String str,String nickname,String theId){
        String  s =  restTemplate.getForObject("http://PROVIDER-TICKET//space/addComment?theId="+theId+"&nickname="+nickname+"&str="+str, String.class);
        Map<String,String> map = new HashedMap();
        return map;
    }
}
