package com.sidd.twitter.service;

import com.sidd.twitter.entities.Tweet;
import com.sidd.twitter.entities.User;

import java.util.List;

/**
 * Created by Siddharth on 2/26/18.
 */
public interface TwitterService {

    /**
     * Registers a user
     * @param u
     */
    public void registerUser(User u);


    /**
     *
     * @param email
     * @return
     */
    public User getUser(String email);

    /**
     *
     * @param userId
     * @return
     */
    public User getUserById(String userId);
    /**
     * Login a user
     * @param email
     * @param password
     * @return authToken on a successful login
     */
    public String login(String email, String password);

    /**
     *
     * @param t
     */
    public void tweet(Tweet t);

    /**
     * returns the list of tweets posted by the user
     * @param email
     * @return
     */
    public List<Tweet> getTweets(String email);

    /**
     * returns the list of tweets posted by the user and her followers
     * @param userId
     * @return
     */
    public List<Tweet> getTimeline(String userId);

    /**
     * returns the followers for the user
     * @param userId
     * @return
     */
    public List<User> getFollowers(String userId);

    /**
     * returns the list of users following this user
     * @param userId
     * @return
     */
    public List<User> getFollowings(String userId);

    /**
     * Follow a user
     * @param userId
     * @param userIdToFollow
     */
    public void follow(String userId, String userIdToFollow);

    /**
     * UnFollow a user
     * @param userId
     * @param userToUnFollow
     */
    public void unFollow(String userId, String userToUnFollow);


}
