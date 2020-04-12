package com.ittttt.provider.pojo;

public class recommendHead {

    private int id;
    private String recommendHeadUrl;

    public recommendHead(int id, String recommendHeadUrl) {
        this.id = id;
        this.recommendHeadUrl = recommendHeadUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecommendHeadUrl() {
        return recommendHeadUrl;
    }

    public void setRecommendHeadUrl(String recommendHeadUrl) {
        this.recommendHeadUrl = recommendHeadUrl;
    }
}
