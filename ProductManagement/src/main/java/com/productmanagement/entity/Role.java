package com.productmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleType name;

    @ManyToMany(mappedBy = "roles")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> users;

    public enum RoleType {
        USER, ADMIN, SUPER_ADMIN
    }
}
