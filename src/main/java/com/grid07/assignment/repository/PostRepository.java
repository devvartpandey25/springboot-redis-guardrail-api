package com.grid07.assignment.repository;

import com.grid07.assignment.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Post entity
public interface PostRepository extends JpaRepository<Post, Long> {

}
