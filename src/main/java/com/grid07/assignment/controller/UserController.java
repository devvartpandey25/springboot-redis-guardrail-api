package com.grid07.assignment.controller;

import com.grid07.assignment.entity.User;
import com.grid07.assignment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    // Not switching to constructor injection yet
    @Autowired
    private UserRepository repo;

    // Add a new user
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        // Might want to check for duplicates here
        User saved = repo.save(user);
        return ResponseEntity.ok(saved);
    }

    // Get all users
    @GetMapping
    public List<User> getEveryone() {
        List<User> all = repo.findAll();
        return all;
    }

    // Get a user by id
    @GetMapping("/{id}")
    public ResponseEntity<User> fetchUser(@PathVariable("id") Long id) {
        User found = null;
        if (repo.findById(id).isPresent()) {
            found = repo.findById(id).get();
            return ResponseEntity.ok(found);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update username and premium status (not handling email or password)
    @PutMapping("/{id}")
    public ResponseEntity<User> editUser(@PathVariable("id") Long id, @RequestBody User patch) {
        if (repo.findById(id).isPresent()) {
            User toUpdate = repo.findById(id).get();
            toUpdate.setUsername(patch.getUsername());
            toUpdate.setPremium(patch.isPremium());
            User result = repo.save(toUpdate);
            return ResponseEntity.ok(result);
        } else {
            // If not found, just 404
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUser(@PathVariable("id") Long id) {
        if (repo.findById(id).isPresent()) {
            User u = repo.findById(id).get();
            repo.delete(u);
            return ResponseEntity.noContent().<Void>build();
        } else {
            // Already gone?
            return ResponseEntity.notFound().build();
        }
    }
}