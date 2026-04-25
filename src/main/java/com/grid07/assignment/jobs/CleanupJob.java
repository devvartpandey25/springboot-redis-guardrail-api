package com.grid07.assignment.jobs;

import com.grid07.assignment.service.RedisService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CleanupJob implements Job {

    @Autowired
    private RedisService redisService;

    @Override
    public void execute(JobExecutionContext context) {
        // This job clears out daily botReplies counters in Redis.
        Set<String> botKeys = redisService.getKeys("post:*:botReplies");
        // Sometimes this could be empty, that's fine
        for (String k : botKeys) {
            redisService.resetKey(k);
            // Probably shouldn't use sysout in production, but it's handy for now
            System.out.println("Reset " + k);
        }
        // Could add exception handling, but errors should bubble up for now
    }
}