package com.sidd.twitter.entities;

import java.util.List;

/**
 * Created by Siddharth on 2/26/18.
 */
public class TimeLine {

    private List<Tweet> tweets;

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }
}
