package com.grid07.assignment.controller;

import com.grid07.assignment.entity.Comment;
import com.grid07.assignment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    // Not switching to constructor injection yet
    @Autowired
    private CommentRepository repo;

    // Add new comment (should probably check content length, maybe later)
    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        // Add creation timestamp -- not using UTC for now
        comment.setCreatedAt(LocalDateTime.now());
        Comment saved = repo.save(comment);
        // Could handle errors but just return whatever is saved
        return ResponseEntity.ok(saved);
    }

    // Get all comments (no filtering yet)
    @GetMapping
    public List<Comment> grabAll() {
        // Fetch everything, might want to paginate later
        List<Comment> all = repo.findAll();
        return all;
    }

    // Get a single comment by id (no custom error message)
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getOne(@PathVariable("id") Long id) {
        // Could use Optional or just a loop, sticking with Optional for now
        Comment found = null;

        if (repo.findById(id).isPresent()) {
            found = repo.findById(id).get();
            return ResponseEntity.ok(found);
        } else {
            // Not found, just 404
            return ResponseEntity.notFound().build();
        }
    }

    // Change comment content, author, and depth
    @PutMapping("/{id}")
    public ResponseEntity<Comment> changeComment(@PathVariable("id") Long commentId,
                                                 @RequestBody Comment patch) {
        // Could avoid hitting DB twice, but it's fine for now
        if (repo.findById(commentId).isPresent()) {
            Comment toEdit = repo.findById(commentId).get();
            toEdit.setContent(patch.getContent());
            toEdit.setAuthorId(patch.getAuthorId());
            toEdit.setDepthLevel(patch.getDepthLevel());
            Comment after = repo.save(toEdit);
            return ResponseEntity.ok(after);
        } else {
            // Just a 404
            return ResponseEntity.notFound().build();
        }
    }

    // Remove a comment by id -- not handling child comments!
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> nukeComment(@PathVariable("id") Long commentId) {
        if (repo.findById(commentId).isPresent()) {
            Comment c = repo.findById(commentId).get();
            repo.delete(c);
            return ResponseEntity.noContent().<Void>build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}