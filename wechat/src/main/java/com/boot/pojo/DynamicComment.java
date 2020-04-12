package com.boot.pojo;

public class DynamicComment {
    //动态主键
    private int id;
    //评论人
    private String commentPeople;
    //评论内容
    private String commentContent;
    //评论类型,0是文本，1是图片
    private String theType;

    public DynamicComment(){}

    public DynamicComment(int id, String commentPeople, String commentContent, String theType) {
        this.id = id;
        this.commentPeople = commentPeople;
        this.commentContent = commentContent;
        this.theType = theType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommentPeople() {
        return commentPeople;
    }

    public void setCommentPeople(String commentPeople) {
        this.commentPeople = commentPeople;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getTheType() {
        return theType;
    }

    public void setTheType(String theType) {
        this.theType = theType;
    }

    @Override
    public String toString() {
        return "DynamicComment{" +
                "id=" + id +
                ", commentPeople='" + commentPeople + '\'' +
                ", commentContent='" + commentContent + '\'' +
                ", theType='" + theType + '\'' +
                '}';
    }
}
