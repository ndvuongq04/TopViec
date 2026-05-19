package com.topviec.topviec_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "rank_order", nullable = false)
    private Integer rank;
}