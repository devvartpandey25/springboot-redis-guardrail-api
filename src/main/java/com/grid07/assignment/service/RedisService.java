package com.grid07.assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Increments virality score for a post based on the interaction type
    public void incrementViralityScore(Long postId, String interactionType) {
        String scoreKey = "post:" + postId + ":virality_score";
        int increment;
        // Might add more types later
        switch (interactionType.toLowerCase()) {
            case "bot_reply":
                increment = 1;
                break;
            case "human_like":
                increment = 20;
                break;
            case "human_comment":
                increment = 50;
                break;
            default:
                // Should probably log this somewhere
                throw new IllegalArgumentException("Unknown interaction type: " + interactionType);
        }
        redisTemplate.opsForValue().increment(scoreKey, increment);
    }

    // Gets the virality score for a post (defaults to 0 if not found)
    public int getViralityScore(Long postId) {
        String scoreKey = "post:" + postId + ":virality_score";
        String val = redisTemplate.opsForValue().get(scoreKey);
        // Not handling errors if value can't be parsed, but should be fine
        return val == null ? 0 : Integer.parseInt(val);
    }

    // Prevents too many bot replies at once (limit: 5)
    public boolean tryBotReply(Long postId) {
        String botKey = "post:" + postId + ":botReplies";
        Long now = redisTemplate.opsForValue().increment(botKey, 1);
        if (now != null && now > 5) {
            redisTemplate.opsForValue().decrement(botKey, 1);
            return false;
        }
        return true;
    }

    // Decrements the in-progress bot reply counter (should call after done)
    public void finishBotReply(Long postId) {
        String botKey = "post:" + postId + ":botReplies";
        redisTemplate.opsForValue().decrement(botKey, 1);
    }

    // Save a notification for a post with a TTL (in seconds)
    public void saveNotification(Long postId, String message, long ttlSeconds) {
        String notifKey = "notification:post:" + postId;
        redisTemplate.opsForValue().set(notifKey, message, ttlSeconds, TimeUnit.SECONDS);
        // Could set a list of notifications instead of just one, but keeping it simple
    }

    // Retrieve notification for a post
    public String getNotification(Long postId) {
        String notifKey = "notification:post:" + postId;
        return redisTemplate.opsForValue().get(notifKey);
    }

    // Utility to get all keys matching a pattern (careful: can be slow in production)
    public Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    // Delete a key from Redis (used for cleanup jobs)
    public void resetKey(String key) {
        redisTemplate.delete(key);
    }
}