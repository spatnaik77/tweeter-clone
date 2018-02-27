# tweeter-clone
Designing A Simple Twitter Clone Using Redis As The Underlying Datastore

This article describes the design and implementation of a very simple twitter clone application using 
Redis as the data store. We will focus on the design of the various data structures of Redis to store the application data.

It exposes following functionalities:
The application will expose the following functionalities:

User registration, 
Login, 
Post a tweet
Get tweets by user
Get Timeline for user (shows the user's tweets and also the tweets by her followers sorted by time)
follow a user, 
unfollow a user
get user's followings
get user's followers

# Data Structures
1.Name: User_Id_Counter
Type: Global Counter
Description: Used for UserId

2.Name: Users
Type: Hash
Description: Hashmap for storing UserId as key and User object as value
Example: 
users:1 - email siddharth@gmail.com 
          password mypassword
          authToken fea5e81ac8ca77622bed1c2132a021f9
 
users:2 - email siddharth@yahoo.com 
          password mypassword
          authToken gea5e81ac8ca77622bed1c2132a021f8

3.Name: UserEmails
Type: key-value pair
Description: Stores email as key and UserId as value
Example:
siddharth@gmail.com 1
siddharth@yahoo.com 2

4.Name: authTokens
Type: key-value pair
Description: Stores authToken as key as corresponding userId as value
Example:
gea5e81ac8ca77622bed1c2132a021f8 1
fea5e81ac8ca77622bed1c2132a021f9 2

5.Name: followers
Type: Sortedset
Description: Stored userId as key and all the follower Ids as value
Example:
followers:1 - 2 3 4 5
followers:2 - 3 4

6.Name: followings
Type: Sortedset
Description: Stored userId as key and all the followings Ids as value
Example:
followings:1 - 2 3 4 5
followings:2 - 3 4

7.Name: Tweet_Id_Counter
Type: Global Counter
Description: Used for Tweet Ids

8.Name: Tweets
Type: Hash
Description: map storing TweetId as key and Tweet object as value
Example: 
tweets:1 - content Going to attend GBS today 
          tweetedOn 374837387
tweets:2 - content I love redis 
          tweetedOn 374837390

9.Name: UserTweets
Type: SortedSet
Description: UserId as key and list of tweetIds as value
Example:
userTweets:1 - 1 2 3 4 5
userTweets:2 - 6 7 8 9 10

10.Name: UserTimeline
Type: SortedSet
Description: UserId as key and list of tweetIds as value
Example:
UserTimeline:1 - 1 2 3 4 5 6 7 8 9
UserTimeline:2 - 6 7 8 9 10 11 12

