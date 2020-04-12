package com.boot.pojo;

import java.util.Date;

public class Dynamic {
    //主键
    private int id;
    //标题
    private String theTitle;
    //点赞人,字符串拼接
    private String praisePeople;
    //发布时间
    private Date theDate;

    public Dynamic(){}

    public Dynamic(int id, String theTitle, String praisePeople, Date theDate) {
        this.id = id;
        this.theTitle = theTitle;
        this.praisePeople = praisePeople;
        this.theDate = theDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTheTitle() {
        return theTitle;
    }

    public void setTheTitle(String theTitle) {
        this.theTitle = theTitle;
    }

    public String getPraisePeople() {
        return praisePeople;
    }

    public void setPraisePeople(String praisePeople) {
        this.praisePeople = praisePeople;
    }

    public Date getTheDate() {
        return theDate;
    }

    public void setTheDate(Date theDate) {
        this.theDate = theDate;
    }

    @Override
    public String toString() {
        return "Dynamic{" +
                "id=" + id +
                ", theTitle='" + theTitle + '\'' +
                ", praisePeople='" + praisePeople + '\'' +
                ", theDate=" + theDate +
                '}';
    }
}
