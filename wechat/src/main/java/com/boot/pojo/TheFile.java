package com.boot.pojo;

public class TheFile {
    //文件存储名
    private String uuidFileName;
    //文件真实名
    private String realFileName;

    public String getUuidFileName() {
        return uuidFileName;
    }

    public void setUuidFileName(String uuidFileName) {
        this.uuidFileName = uuidFileName;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    public TheFile(String uuidFileName, String realFileName) {
        this.uuidFileName = uuidFileName;
        this.realFileName = realFileName;
    }

    @Override
    public String toString() {
        return "TheFile{" +
                "uuidFileName='" + uuidFileName + '\'' +
                ", realFileName='" + realFileName + '\'' +
                '}';
    }
}
