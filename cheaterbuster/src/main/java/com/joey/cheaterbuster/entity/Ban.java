package com.joey.cheaterbuster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform", length = 50)
    private String platform;

    @Column(name = "banned_date", length = 50)
    private String bannedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "steam_id", nullable = false)
    private PlayerData player;
}