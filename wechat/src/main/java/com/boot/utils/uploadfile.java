package com.boot.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class uploadfile {

    public static final String ACCESS_KEY = "Y7QIvvEcbDS5lFFGFmkXHqU4r-hZ3NX5u2C7r-SJ"; // 你的access_key
    public static final String SECRET_KEY = "qq8uW8hunwJp1B3NAZz2qMR1vw63-_VfL0oezeo4"; // 你的secret_key
    public static final String BUCKET_NAME = "cdn.dxkbz.top"; // 域名-

    public static String SaveFileFromInputStream(InputStream stream, String path, String filename)throws IOException
    {
        FileOutputStream fs = new FileOutputStream( path +"/"+ filename);
        byte[] buffer=new byte[1024*1024];
        int bytesum=0;
        int byteread=0;
        while((byteread=stream.read(buffer))!=-1){
            bytesum+=byteread;
            fs.write(buffer,0,byteread);
            fs.flush();
        }
        fs.close();
        stream.close();
        return path+"/"+filename;
    }
    public static void uploadToQiNiu(String url,String filename){

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = ACCESS_KEY;
        String secretKey = SECRET_KEY;
        String bucket = "mystoragespacename";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = url;
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = filename;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket,key);
        //System.out.println("uptoken is:    "+upToken);
        //System.out.println("localFilePath="+localFilePath);
        //System.out.println("key="+key);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //System.out.println("????????????????");
            }
        }
    }

    public static String downFile(String fileName){
        String domainOfBucket = BUCKET_NAME;
        String finalUrl = String.format("%s/%s", domainOfBucket, fileName);
        return finalUrl;
    }
}
