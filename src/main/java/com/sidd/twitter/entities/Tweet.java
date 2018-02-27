package com.sidd.twitter.entities;

/**
 * Created by Siddharth on 2/26/18.
 */
public class Tweet {

    private String id;
    private String content;
    private long time;
    private String ownerId;

    public Tweet()
    {

    }
    public Tweet(String content, String ownerId) {
        this.content = content;
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
