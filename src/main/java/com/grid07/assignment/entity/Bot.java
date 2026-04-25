package com.grid07.assignment.entity;

import jakarta.persistence.*;
import lombok.Data;

// Using Lombok's @Data for getters/setters
@Entity
@Table(name = "bots")
@Data
public class Bot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String personaDescription;
}