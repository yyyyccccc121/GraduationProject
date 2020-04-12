package com.boot.controller;

import com.boot.pojo.SysUser;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

@Controller
public class FileController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("manage/excelImport.do")
    @ResponseBody
    public Map<String,String> excelImport(@RequestParam("file") MultipartFile excelFile, HttpServletRequest request) throws IOException {
        String fromNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        //真实文件名
        String realFileName=null;
        //生成一个唯一文件名
        String uuidFileName = UUID.randomUUID().toString();
        if (excelFile != null){
            String filename=excelFile.getOriginalFilename();
            //IE浏览器获取的文件名会自带盘符,这里需要截取一下
            int unixSep = filename.lastIndexOf('/');
            int winSep = filename.lastIndexOf('\\');
            int pos = (winSep > unixSep ? winSep : unixSep);
            if (pos != -1)  {
                filename = filename.substring(pos + 1);
            }
            realFileName = filename;
            //唯一文件名加后缀
            String str1=realFileName.substring(0, realFileName.indexOf("."));
            String str2=realFileName.substring(str1.length()+1);
            uuidFileName = uuidFileName+"."+str2;
            //模拟服务器存储文件路径,到时候改改
            String a="F:/XXXXX";
            //保存到服务器的路径
            SaveFileFromInputStream(excelFile.getInputStream(),a,uuidFileName);
        }
        //把唯一文件名和真实文件名存入数据库
        String s =  restTemplate.getForObject("http://PROVIDER-TICKET//addToTheFile?uuidFileName="+uuidFileName+"&realFileName="+realFileName, String.class);
        //获取自己的头像
        SysUser sysUser =  restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+fromNumber, SysUser.class);
        Map<String,String> map = new HashedMap();
        map.put("uuidFileName",uuidFileName);
        map.put("realFileName",realFileName);
        map.put("headPortrait",sysUser.getHeadPortrait());
        return map;
    }

    /**
     * 将MultipartFile转化为file并保存到服务器上的某地
     */
    public static void SaveFileFromInputStream(InputStream stream, String path, String savefile) throws IOException
    {
        File file =new File(path);
        //如果文件夹不存在则创建
        if  (!file .exists()  && !file .isDirectory())
        {
            //System.out.println("//不存在");
            file .mkdir();
        } else
        {
            //System.out.println("//目录存在");
        }
        FileOutputStream fs=new FileOutputStream( path + "/"+ savefile);
        //System.out.println("------------"+path + "/"+ savefile);
        byte[] buffer =new byte[1024*1024];
        int bytesum = 0;
        int byteread = 0;
        while ((byteread=stream.read(buffer))!=-1)
        {
            bytesum+=byteread;
            fs.write(buffer,0,byteread);
            fs.flush();
        }
        fs.close();
        stream.close();
    }

    @RequestMapping(value = "util/downfile")
    public void downFile(HttpServletRequest request, HttpServletResponse response, String uuidFileName) throws IOException {

        // 根据文件名称|文件路径获取 上下文的路径地址
        String realPath = "F:/XXXXX/"+uuidFileName;
        //没有就创建
        File theFile=new File("F:/XXXXX");
        if (!theFile.exists()) {
            theFile.mkdirs();
        }
        // 获取文件的长度
        File file = new File(realPath);
        long fileLength = file.length();
        // 获取文件名称
        String name = file.getName();
        //System.out.println("name="+name);
        // 设置响应类型
        response.setHeader("Content-Type", "application/octet-stream");
        // 设置下载的类型的长度
        response.setHeader("Content-Length", String.valueOf(fileLength));
        // 设置以下载方式使用
        response.setHeader("Content-Disposition","attachment;filename*=utf-8'zh_cn'" + URLEncoder.encode(name, "UTF-8"));
        // 获取下载流对象
        ServletOutputStream os = response.getOutputStream();
        // 缓冲输出流
        BufferedOutputStream bos = new BufferedOutputStream(os);
        // 下载文件的缓冲输入流
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        // 定义的缓冲区
        byte buffer[] =new byte[1024];
        // 定义读取的长度
        int len=0;
        // 循环读取
        while((len=bis.read(buffer))!=-1) {
            // 写入到响应的流中
            bos.write(buffer, 0, len);
        }
        bis.close();
        bos.close();
        os.close();
    }

    @RequestMapping(value = "/findRealFileName")
    @ResponseBody
    public Map<String,String> findRealFileName(String uuidFileName){
        //根据唯一文件名查真实文件名
        String realFileName = restTemplate.getForObject("http://PROVIDER-TICKET//findRealFileName?uuidFileName="+uuidFileName,String.class);
        Map<String,String> map = new HashedMap();
        map.put("realFileName",realFileName);
        return map;
    }

    @RequestMapping(value = "message/audio")
    @ResponseBody
    public Map<String,String> saveAudio(String theBase,String theTime){

        String fromNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        //生成一个唯一音频文件名
        String uuidAudioName = UUID.randomUUID().toString();

        //根据音频文件名把theBase存入文件
        String directory="F:/XXXXX";
        String filename=uuidAudioName+".txt";
        File file=new File(directory);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2=new File(directory,filename);
        if (!file2.exists()) {
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(directory+"/"+filename);
            //使用缓冲区比不使用缓冲区效果更好，因为每趟磁盘操作都比内存操作要花费更多时间。
            //通过BufferedWriter和FileWriter的连接，BufferedWriter可以暂存一堆数据，然后到满时再实际写入磁盘
            //这样就可以减少对磁盘操作的次数。如果想要强制把缓冲区立即写入,只要调用writer.flush();这个方法就可以要求缓冲区马上把内容写下去
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
            bufferedWriter.write(theBase);
            bufferedWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //把音频文件名和时间存进数据库
        String str = restTemplate.getForObject("http://PROVIDER-TICKET//saveAudio?uuidAudioName="+uuidAudioName+"&theTime="+theTime,String.class);
        //获取自己的头像
        SysUser sysUser =  restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+fromNumber, SysUser.class);
        Map<String,String> map = new HashedMap();
        map.put("uuidAudioName",uuidAudioName);
        map.put("headPortrait",sysUser.getHeadPortrait());
        return map;
    }

    @RequestMapping(value = "/findTheBaseById")
    @ResponseBody
    public Map<String,String> findTheBaseById(String id) throws IOException {
        String path = "F:/XXXXX"+"/"+id+".txt";
        InputStream is = new FileInputStream(path);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null){
            buffer.append(line);
        }
        String theBase = buffer.toString();
        Map<String,String> map = new HashedMap();
        map.put("theBase64",theBase);
        return map;
    }

    @RequestMapping(value = "/findTheTimeById")
    @ResponseBody
    public Map<String,String> findTheTimeById(String uuidAudioName){
        String theTime = restTemplate.getForObject("http://PROVIDER-TICKET//findTheTimeById?uuidAudioName="+uuidAudioName,String.class);
        Map<String,String> map = new HashedMap();
        map.put("theTime",theTime);
        return map;
    }

    @RequestMapping(value = "file/sendImageOrVideo")
    @ResponseBody
    public Map<String,String> sendImageOrVideo(@RequestParam("image") MultipartFile excelFile) throws IOException {
        String fromNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String filename=excelFile.getOriginalFilename();
        //IE浏览器获取的文件名会自带盘符,这里需要截取一下
        int unixSep = filename.lastIndexOf('/');
        int winSep = filename.lastIndexOf('\\');
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1)  {
            filename = filename.substring(pos + 1);
        }
        String str1=filename.substring(0, filename.indexOf("."));
        //文件后缀
        String houZui=filename.substring(str1.length()+1);
        //生成一个唯一文件名
        String uuidFileName = UUID.randomUUID().toString();
        uuidFileName+="."+houZui;
        String a="F:/XXXXX";
        //保存到服务器的路径
        SaveFileFromInputStream(excelFile.getInputStream(),a,uuidFileName);
        //获取自己的头像
        SysUser sysUser =  restTemplate.getForObject("http://PROVIDER-TICKET//findAllByName?accountNumber="+fromNumber, SysUser.class);
        Map<String,String> map = new HashedMap();
        map.put("headPortrait",sysUser.getHeadPortrait());
        map.put("uuidFileName",uuidFileName);
        //是视频
        if (houZui.equals("mp4")||houZui.equals("webm")||houZui.equals("ogg")){
            map.put("type","1");
        }else {
            map.put("type","0");
        }
        return map;
    }
}
