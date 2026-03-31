package com.curatix.vault.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FirebasePrincipal {
    private final String uid;
    private final String email;
    private final String displayName;
    private final String photoUrl;
}
