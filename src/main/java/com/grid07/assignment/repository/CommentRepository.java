package com.grid07.assignment.repository;

import com.grid07.assignment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Comment entity
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
