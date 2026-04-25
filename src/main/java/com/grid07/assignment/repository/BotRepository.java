
package com.grid07.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grid07.assignment.entity.Bot;
// Repository for Bot Entity
public interface BotRepository extends JpaRepository<Bot, Long> {}
