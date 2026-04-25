package com.grid07.assignment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor // Needed for JPA
@AllArgsConstructor // Not always using, but handy for tests
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Probably redundant, but makes things explicit
    private Long id;

    // Username should really be unique
    private String username;
    private boolean isPremium;
}
