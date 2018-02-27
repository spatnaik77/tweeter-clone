package com.sidd.twitter.service;

import com.sidd.twitter.entities.Tweet;
import com.sidd.twitter.entities.User;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Siddharth on 2/27/18.
 */
public class TwitterServiceImpl implements TwitterService{

    Jedis jedis;

    //Counter for user id
    String KEY_USER_ID_COUNTER  = "User_Id_Counter";

    //Map containing userId as key and User object as value
    String NS_USERID_USER_MAP  = "Users";

    //simple key values. Email as key and UserId as value
    String NS_USEREMAIL_USERID = "UserEmails";

    //simple key values. Authtoken as key and UserId as value
    String NS_AUTHTOKENS_USERID = "AuthTokens";

    //sorted set for followers. key as id of the user. value is a set of followers
    String NS_FOLLOWERS = "Followers";

    //sorted set for followings. key as id of the user. value is a set of followings
    String NS_FOLLOWINGS = "Followings";

    //Counter for Tweet id
    String KEY_TWEET_ID_COUNTER  = "Tweet_Id_Counter";

    //Map containing tweetId as key and Tweet object as value
    String NS_TWEETID_TWEET_MAP  = "Tweets";

    //Sorted set containing user tweets
    String NS_USER_TWEETS  = "UserTweets";

    //Sorted set containing user timeline
    String NS_USER_TIMELINE  = "UserTimeline";


    public TwitterServiceImpl()
    {
        jedis = new Jedis();
    }
    public TwitterServiceImpl(boolean cleanAll)
    {
        if(cleanAll) {
            jedis = new Jedis();
            jedis.flushAll();
        }
        else
        {
            jedis = new Jedis();
        }
    }

    public void registerUser(User u)
    {
        //Get the user id from User_Counter
        long userId = jedis.incr(KEY_USER_ID_COUNTER);

        HashMap<String, String> userObj = new HashMap<String, String>();
        userObj.put("email", u.getEmail());
        userObj.put("password", u.getPassword());
        //userObj.put("authToken", System.currentTimeMillis()+"");//TODO change it to a proper one
        jedis.hmset(NS_USERID_USER_MAP + ":" + userId, userObj);

        jedis.set(NS_USEREMAIL_USERID + ":" + u.getEmail(), "" + userId);
    }

    public User getUser(String email)
    {
        User u = null;

        String userId = jedis.get(NS_USEREMAIL_USERID + ":" + email);
        if(userId != null)
        {
            String authToken = jedis.hget(NS_USERID_USER_MAP + ":" + userId, "authToken");
            u = new User();
            u.setAuthToken(authToken);
            u.setId(Long.parseLong(userId));
            u.setEmail(email);
        }
        return u;
    }

    public User getUserById(String userId)
    {
        User u = null;

        String email = jedis.hget(NS_USERID_USER_MAP + ":" + userId, "email");
        if(email != null) {
            String authToken = jedis.hget(NS_USERID_USER_MAP + ":" + userId, "authToken");
            u = new User();
            u.setAuthToken(authToken);
            u.setId(Long.parseLong(userId));
            u.setEmail(email);
        }
        return u;
    }

    public String login(String email, String password)
    {
        //get the userId for this email
        String userId = jedis.get(NS_USEREMAIL_USERID + ":" + email);
        if(userId != null)
        {
            //get the password
            String pwd = jedis.hget(NS_USERID_USER_MAP + ":" + userId, "password");
            if(password.equals(pwd))
            {
                //login successful
                //generate authToken
                String authToken = "" + System.currentTimeMillis();
                jedis.hset(NS_USERID_USER_MAP + ":" + userId, "authToken", authToken);

                jedis.set(NS_AUTHTOKENS_USERID + ":" + authToken, userId);
                return authToken;
            }
            else
            {
                return null;
            }

        }
        else
        {
            return null;
        }
    }

    public void tweet(Tweet t)
    {
        //Create tweet
        long tweetId = jedis.incr(KEY_TWEET_ID_COUNTER);
        HashMap<String, String> tweetObj = new HashMap<String, String>();
        tweetObj.put("id", "" + tweetId);
        tweetObj.put("time", "" + System.currentTimeMillis());
        tweetObj.put("owner", "" + t.getOwnerId());
        tweetObj.put("content", "" + t.getContent());
        jedis.hmset(NS_TWEETID_TWEET_MAP + ":" + tweetId, tweetObj);

        //Fan out:
            // 1. Add to user's tweets
            jedis.zadd(NS_USER_TWEETS + ":" + t.getOwnerId(), (double)t.getTime(), ""+tweetId);

            // 2. Add to User's timeline
            jedis.zadd(NS_USER_TIMELINE + ":" + t.getOwnerId(), (double)t.getTime(), ""+tweetId);

            // 3. Get all the followers
            Set<String> followers = jedis.zrange(NS_FOLLOWERS + ":" + t.getOwnerId(), 0, -1);

            // 4. Add to all the followers's timeline
            for(String id : followers)
            {
                jedis.zadd(NS_USER_TIMELINE + ":" + id, (double)t.getTime(), ""+tweetId);
            }
    }
    public Tweet getTweetById(String tweetId)
    {
        Tweet t = null;

        String owner = jedis.hget(NS_TWEETID_TWEET_MAP + ":" + tweetId, "owner");
        if(owner != null)
        {
            String time    = jedis.hget(NS_TWEETID_TWEET_MAP + ":" + tweetId, "time");
            String content = jedis.hget(NS_TWEETID_TWEET_MAP + ":" + tweetId, "content");

            t = new Tweet();
            t.setId(tweetId);
            t.setContent(content);
            t.setOwnerId(owner);
            t.setTime(Long.parseLong(time));
        }
        return t;
    }

    public List<Tweet> getTweets(String userId)
    {
        List<Tweet> tweets = new ArrayList<Tweet>();

        Set<String> ids = jedis.zrange(NS_USER_TWEETS + ":" + userId , 0, -1);
        for(String tweetId : ids)
        {
            Tweet t = this.getTweetById(tweetId);
            tweets.add(t);
        }
        return tweets;
    }

    public List<Tweet> getTimeline(String userId)
    {

        List<Tweet> tweets = new ArrayList<Tweet>();

        Set<String> ids = jedis.zrange(NS_USER_TIMELINE + ":" + userId , 0, -1);
        for(String tweetId : ids)
        {
            Tweet t = this.getTweetById(tweetId);
            tweets.add(t);
        }
        return tweets;
    }

    public List<User> getFollowers(String userId)
    {
        List<User> users = new ArrayList<User>();
        Set<String> ids = jedis.zrange(NS_FOLLOWERS + ":" + userId, 0, -1);
        for(String id : ids)
        {
            users.add(this.getUserById(id));
        }
        return users;
    }

    public List<User> getFollowings(String userId) {

        List<User> users = new ArrayList<User>();
        Set<String> ids = jedis.zrange(NS_FOLLOWINGS + ":" + userId, 0, -1);
        for(String id : ids)
        {
            users.add(this.getUserById(id));
        }
        return users;
    }

    public void follow(String userId, String userIdToFollow)
    {
        //add to followings
        jedis.zadd(NS_FOLLOWINGS + ":" + userId, System.currentTimeMillis(), userIdToFollow);

        //add to followers
        jedis.zadd(NS_FOLLOWERS + ":" + userIdToFollow, System.currentTimeMillis(), userId);
    }

    public void unFollow(String userId, String userIdToUnFollow)
    {
        //remove from followings
        jedis.zrem(NS_FOLLOWINGS + ":" + userId, userIdToUnFollow);

        //remove from followers
        jedis.zrem(NS_FOLLOWERS + ":" + userIdToUnFollow, userId);

    }
}
