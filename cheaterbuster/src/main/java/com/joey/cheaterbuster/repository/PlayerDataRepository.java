package com.joey.cheaterbuster.repository;

import com.joey.cheaterbuster.entity.PlayerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerDataRepository extends JpaRepository<PlayerData, String> {
    Optional<PlayerData> findBySteamId(String steamId);
    boolean existsBySteamId(String steamId);
}