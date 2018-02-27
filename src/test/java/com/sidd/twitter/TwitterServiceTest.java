package com.sidd.twitter;

import com.sidd.twitter.entities.Tweet;
import com.sidd.twitter.entities.User;
import com.sidd.twitter.service.TwitterService;
import com.sidd.twitter.service.TwitterServiceImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Created by sr250345 on 2/27/18.
 */
public class TwitterServiceTest {

    static TwitterService twitterService;


    String user1_email = "user1@gmail.com";
    String user2_email = "user2@gmail.com";
    String user3_email = "user3@gmail.com";
    String user4_email = "user4@gmail.com";

    @BeforeClass
    public static void setup() {
        twitterService = new TwitterServiceImpl(true);
    }

    @Test
    public void testUsers() {
        User u1 = new User();
        u1.setEmail(user1_email);
        u1.setPassword("user1_password");

        User u2 = new User();
        u2.setEmail(user2_email);
        u2.setPassword("user2_password");

        User u3 = new User();
        u3.setEmail(user3_email);
        u3.setPassword("user3_password");

        User u4 = new User();
        u4.setEmail(user4_email);
        u4.setPassword("user4_password");


        twitterService.registerUser(u1);
        twitterService.registerUser(u2);
        twitterService.registerUser(u3);
        twitterService.registerUser(u4);

        Assert.assertNotNull(user1_email);
        Assert.assertNotNull(user2_email);
        Assert.assertNotNull(user3_email);
        Assert.assertNotNull(user4_email);


        //Login
        String authToken = twitterService.login(user1_email, "user1_password");
        Assert.assertNotNull(authToken);

        //Login with wrong credentials
        authToken = twitterService.login(user1_email, "user1_passwordHHHHHHHHHHHH");
        Assert.assertNull(authToken);
    }

    @Test
    public void testFollow()
    {
        String user1_id = "" + twitterService.getUser(user1_email).getId();
        String user2_id = "" + twitterService.getUser(user2_email).getId();
        String user3_id = "" + twitterService.getUser(user3_email).getId();
        String user4_id = "" + twitterService.getUser(user4_email).getId();


        //User1 follows user2 and user3
        twitterService.follow(user1_id, user2_id);
        twitterService.follow(user1_id, user3_id);
        twitterService.follow(user1_id, user4_id);

        twitterService.unFollow(user1_id, user4_id);

        //get followers for user2
        List<User> user2Followers = twitterService.getFollowers(user2_id);
        Assert.assertEquals(1, user2Followers.size());

        //get followings of user1
        List<User> user1Followings = twitterService.getFollowings(user1_id);
        Assert.assertEquals(2, user1Followings.size());
    }


    @Test
    public void testTweets() {

        String user1_id = "" + twitterService.getUser(user1_email).getId();
        String user2_id = "" + twitterService.getUser(user2_email).getId();
        String user3_id = "" + twitterService.getUser(user3_email).getId();

        //Tweet by user1
        twitterService.tweet(new Tweet("user1Tweet", user1_id));
        //Tweet by user2
        twitterService.tweet(new Tweet("user2Tweet", user2_id));
        //Tweet by user3
        twitterService.tweet(new Tweet("user3Tweet", user3_id));

        List<Tweet> user1Tweets = twitterService.getTweets(user1_id);
        Assert.assertEquals(1, user1Tweets.size());

        List<Tweet> user1TimelineTweets = twitterService.getTimeline(user1_id);
        Assert.assertEquals(3, user1TimelineTweets.size());

    }
}