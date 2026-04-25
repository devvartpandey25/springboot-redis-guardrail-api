package com.grid07.assignment.jobs;

import com.grid07.assignment.repository.PostRepository;
import com.grid07.assignment.entity.Post;
import com.grid07.assignment.service.RedisService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViralityJob implements Job {
    @Autowired
    private RedisService redisService;

    @Autowired
    private PostRepository postRepo;

    @Override
    public void execute(JobExecutionContext context) {
        // Check every post for virality
        List<Post> allPosts = postRepo.findAll();
        for (Post p : allPosts) {
            int vScore = redisService.getViralityScore(p.getId());
            if (vScore > 100) {
                // Might want to do something more useful than printing
                System.out.println("Post " + p.getId() + " is trending!");
            }
        }
    }
}