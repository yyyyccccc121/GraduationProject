package com.boot.pojo;

public class DynamicImage {
    //动态主键
    private int id;
    //图片文件夹路径
    private String route;

    public DynamicImage(){}

    public DynamicImage(int id, String route) {
        this.id = id;
        this.route = route;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "DynamicImage{" +
                "id=" + id +
                ", route='" + route + '\'' +
                '}';
    }
}
