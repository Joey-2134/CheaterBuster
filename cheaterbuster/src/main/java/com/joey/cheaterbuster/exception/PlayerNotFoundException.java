package com.joey.cheaterbuster.exception;

import lombok.Getter;

@Getter
public class PlayerNotFoundException extends RuntimeException {

    private final String steamId;

    public PlayerNotFoundException(String steamId, Throwable cause) {
        super("Player not found for Steam ID: " + steamId, cause);
        this.steamId = steamId;
    }

}