package com.grid07.assignment.controller;

import com.grid07.assignment.entity.Bot;
import com.grid07.assignment.entity.Comment;
import com.grid07.assignment.entity.Post;
import com.grid07.assignment.entity.AuditLog;
import com.grid07.assignment.repository.BotRepository;
import com.grid07.assignment.repository.CommentRepository;
import com.grid07.assignment.repository.PostRepository;
import com.grid07.assignment.repository.AuditLogRepository;
import com.grid07.assignment.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private BotRepository botRepo;

    @Autowired
    private AuditLogRepository auditRepo;

    @Autowired
    private RedisService redis;

    // CREATE Post -- adds timestamp, then saves, then initializes score
    @PostMapping
    public ResponseEntity<Post> makePost(@RequestBody Post p) {
        p.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepo.save(p);
        redis.incrementViralityScore(savedPost.getId(), "human_like"); // just initialize
        return ResponseEntity.ok(savedPost);
    }

    // Get all posts (maybe should paginate eventually)
    @GetMapping
    public List<Post> allPosts() {
        return postRepo.findAll();
    }

    // Get post by id (no error message, just 404)
    @GetMapping("/{id}")
    public ResponseEntity<Post> fetchPost(@PathVariable("id") Long pid) {
        Optional<Post> found = postRepo.findById(pid);
        if (found.isPresent()) {
            return ResponseEntity.ok(found.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update a post
    @PutMapping("/{id}")
    public ResponseEntity<Post> changePost(@PathVariable("id") Long id,
                                           @RequestBody Post newPost) {
        Optional<Post> maybe = postRepo.findById(id);
        if (maybe.isPresent()) {
            Post post = maybe.get();
            post.setContent(newPost.getContent());
            post.setAuthorId(newPost.getAuthorId());
            Post after = postRepo.save(post);
            return ResponseEntity.ok(after);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a post by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> nukePost(@PathVariable("id") Long id) {
        // Could check if post exists, just try to delete for now
        Optional<Post> maybe = postRepo.findById(id);
        if (maybe.isPresent()) {
            postRepo.delete(maybe.get());
            return ResponseEntity.noContent().<Void>build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ADD Comment to Post
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> dropComment(@PathVariable("postId") Long postId,
                                               @RequestBody Comment comment) {
        comment.setPostId(postId);
        comment.setCreatedAt(LocalDateTime.now());
        Comment c = commentRepo.save(comment);

        redis.incrementViralityScore(postId, "human_comment");

        // log comment action
        AuditLog log = new AuditLog();
        log.setPostId(postId);
        log.setActionType("COMMENT");
        log.setTimestamp(LocalDateTime.now());
        log.setActorId(comment.getAuthorId());
        auditRepo.save(log);

        int score = redis.getViralityScore(postId);
        if (score > 200) {
            redis.saveNotification(postId, "Post " + postId + " has crossed the virality threshold!", 3600);
        }

        return ResponseEntity.ok(c);
    }

    // LIKE Post
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> thumbsUp(@PathVariable("postId") Long pid) {
        redis.incrementViralityScore(pid, "human_like");
        int currScore = redis.getViralityScore(pid);

        AuditLog log = new AuditLog();
        log.setPostId(pid);
        log.setActionType("LIKE");
        log.setTimestamp(LocalDateTime.now());
        log.setActorId(1L); // TODO: Replace with user session id
        auditRepo.save(log);

        if (currScore > 200) {
            redis.saveNotification(pid, "Post " + pid + " has crossed the virality threshold!", 3600);
        }

        String respMsg = "Post " + pid + " liked. Current score: " + currScore;
        return ResponseEntity.ok(respMsg);
    }

    // BOT Reply -- restricts excessive bot replies
    @PostMapping("/{postId}/botReply")
    public ResponseEntity<String> botTalk(@PathVariable("postId") Long postId,
                                          @RequestBody Bot bot) {
        // Don't let bots reply too often
        if (!redis.tryBotReply(postId)) {
            return ResponseEntity.status(429).body("Too many bot replies in progress for post " + postId);
        }

        redis.incrementViralityScore(postId, "bot_reply");
        redis.finishBotReply(postId);

        AuditLog log = new AuditLog();
        log.setPostId(postId);
        log.setActionType("BOT_REPLY");
        log.setTimestamp(LocalDateTime.now());
        log.setActorId(bot.getId());
        auditRepo.save(log);

        int score = redis.getViralityScore(postId);
        if (score > 200) {
            redis.saveNotification(postId, "Post " + postId + " has crossed the virality threshold!", 3600);
        }

        return ResponseEntity.ok("Bot reply added. Current score: " + score);
    }

    // TRENDING Posts
    @GetMapping("/trending")
    public ResponseEntity<List<Post>> trendingNow() {
        List<Post> all = postRepo.findAll();
        List<Post> trending = new ArrayList<>();
        for (Post p : all) {
            int score = redis.getViralityScore(p.getId());
            if (score > 100) {
                trending.add(p);
            }
        }
        return ResponseEntity.ok(trending);
    }

    // ANALYTICS
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> showAnalytics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPosts", postRepo.count());
        stats.put("totalComments", commentRepo.count());
        stats.put("totalBots", botRepo.count());
        return ResponseEntity.ok(stats);
    }

    // Top Viral Posts
    @GetMapping("/topviral")
    public ResponseEntity<List<Map<String, Number>>> viralLeaders() {
        List<Post> posts = postRepo.findAll();
        List<Map<String, Number>> viralList = new ArrayList<>();
        for (Post p : posts) {
            int score = redis.getViralityScore(p.getId());
            Map<String, Number> map = new HashMap<>();
            map.put("postId", p.getId());
            map.put("score", score);
            viralList.add(map);
        }
        viralList.sort((a, b) -> ((Integer)b.get("score")).compareTo((Integer)a.get("score")));
        // Only keep top 5
        List<Map<String, Number>> top = new ArrayList<>();
        int cap = Math.min(5, viralList.size());
        for (int i = 0; i < cap; i++) {
            top.add(viralList.get(i));
        }
        return ResponseEntity.ok(top);
    }

    @GetMapping("/dailySummary")
    public ResponseEntity<Map<String, Object>> todaySummary() {
        LocalDate today = LocalDate.now();
        List<AuditLog> logs = auditRepo.findAll();
        long likes = 0, comments = 0, bots = 0;
        for (AuditLog l : logs) {
            if (l.getTimestamp() == null) continue;
            if (l.getTimestamp().toLocalDate().equals(today)) {
                if ("LIKE".equals(l.getActionType())) likes++;
                else if ("COMMENT".equals(l.getActionType())) comments++;
                else if ("BOT_REPLY".equals(l.getActionType())) bots++;
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("date", today.toString());
        summary.put("totalLikes", likes);
        summary.put("totalComments", comments);
        summary.put("totalBotReplies", bots);

        return ResponseEntity.ok(summary);
    }
}

