package com.curatix.vault.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The competition result status of a project board")
public enum Result {
    WINNER, 
    RUNNER_UP, 
    SPECIAL_MENTION, 
    PARTICIPATED
}
