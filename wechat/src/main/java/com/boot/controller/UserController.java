package com.boot.controller;

import com.alibaba.fastjson.JSON;
import com.boot.pojo.Message;
import com.boot.pojo.SysUser;
import com.boot.pojo.recommendHead;
import com.boot.utils.Pinyin;
import com.boot.utils.uploadfile;
import com.sun.org.apache.xpath.internal.objects.XObject;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping(value = "touser")
public class UserController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject register(String accountNumber, String password){
        JSONObject jsonObject = new JSONObject();
        if(accountNumber.length()<8) {
            String Message = restTemplate.getForObject("http://PROVIDER-TICKET//register?accountNumber=" + accountNumber + "&password=" + password, String.class);
            jsonObject.put("message", Message);
        }else{
            jsonObject.put("message", "长度不能大于7位");
        }
        return jsonObject;
    }


    //编辑用户基本信息
    @RequestMapping(value = "/editUser",method = RequestMethod.POST)
    public String editUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取用户名
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        String nickName = request.getParameter("nickName");
        String sex = request.getParameter("sex");
        String region = request.getParameter("region");
        String signature = request.getParameter("signature");

        //持久化
        MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<String, String>();
        postParameters.add("nickName", nickName);
        postParameters.add("sex", sex);
        postParameters.add("region", region);
        postParameters.add("signature", signature);
        postParameters.add("accountNumber",accountNumber);
        restTemplate.postForObject("http://PROVIDER-TICKET//editUser",postParameters,String.class);
        return "redirect:/index";
    }


    //上传用户保存的图片
    @RequestMapping(value = "/editHead",method = RequestMethod.POST)
    public String editHead(HttpServletRequest request) throws IOException{

        //转型为MultipartHttpRequest(重点的所在)
        MultipartHttpServletRequest multipartRequest  =  (MultipartHttpServletRequest) request;
        //获得第1张图片（根据前台的name名称得到上传的文件）
        MultipartFile file  =  multipartRequest.getFile("file");
        //获取用户名
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        //用户名加文件名作为新的文件名
        String fileName = accountNumber+file.getOriginalFilename();
        //调用写的工具类里面的方法将用户上传图片保存到本地F盘
        String url = uploadfile.SaveFileFromInputStream(file.getInputStream(),"F:/",fileName);
        //调用写的工具类里面的方法将本地图片上传到七牛云
        uploadfile.uploadToQiNiu(url,fileName);
        //调用写的工具类里面的方法获取图片访问链接
        String finalUrl = uploadfile.downFile(fileName);

        //持久化
        MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<String, String>();
        postParameters.add("finalUrl", "http://"+finalUrl);
        postParameters.add("accountNumber",accountNumber);
        restTemplate.postForObject("http://PROVIDER-TICKET//editHead",postParameters,String.class);
        return "redirect:/index";
    }

    //上传用户使用的推荐头像
    @RequestMapping(value = "/editHead1",method = RequestMethod.GET)
    public String editHead1(String id) throws IOException{
        //System.out.println("id==========="+id);
        String accountNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        restTemplate.getForObject("http://PROVIDER-TICKET//editHead1?id="+id+"&accountNumber="+accountNumber,String.class);
        return "redirect:/index";
    }

    //查询所有推荐头像
    @RequestMapping(value = "/editRecommendHead",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> editRecommendHead(){
        List<recommendHead> listHead = restTemplate.getForObject("http://PROVIDER-TICKET//editRecommendHead",List.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("listHead", listHead);
        return map;
    }

    //查询所有人
    @RequestMapping(value = "/findAllUser",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List> findAllUser(){
        String json = restTemplate.getForObject("http://PROVIDER-TICKET//findAllUser",String.class);
        List<SysUser> listUser = JSON.parseArray(json, SysUser.class);
        Map<String, List> map = new HashMap<String, List>();
        List<Character> listChar = new ArrayList<Character>();
        for (SysUser sysUser:listUser){
            char firstChar = Pinyin.getPingYin(sysUser.getNickName()).charAt(0);
            if(map.get(firstChar+"")==null){
                listChar.add(firstChar);
                List<SysUser> list = new ArrayList<SysUser>();
                list.add(sysUser);
                map.put(firstChar+"",list);
            }else{
                map.get(firstChar+"").add(sysUser);
            }
        }
        Collections.sort(listChar);
        map.put("listChar",listChar);
        return map;
    }

    @RequestMapping(value = "/findUserByName" ,method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> findUserByName(String accountNumber){
        SysUser sysUser =  restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+accountNumber, SysUser.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sysUser", sysUser);
        return map;
    }

    @RequestMapping(value = "/findUserByWord",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> findUserByWord(String word){
        List<SysUser> SearchListUser = restTemplate.getForObject("http://PROVIDER-TICKET//findUserByWord?word="+word,List.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("SearchListUser", SearchListUser);
        return map;
    }

}
