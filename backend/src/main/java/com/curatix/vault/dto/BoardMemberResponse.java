package com.curatix.vault.dto;

import com.curatix.vault.entity.BoardMemberEntity;
import com.curatix.vault.entity.MemberProfileEntity;
import com.curatix.vault.entity.UserEntity;

public class BoardMemberResponse {
    private Long id;
    private UserEntity user;
    private BoardMemberEntity.Role role;
    private MemberProfileEntity profile;

    public BoardMemberResponse() {}

    public static BoardMemberResponseBuilder builder() {
        return new BoardMemberResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public BoardMemberEntity.Role getRole() { return role; }
    public void setRole(BoardMemberEntity.Role role) { this.role = role; }
    public MemberProfileEntity getProfile() { return profile; }
    public void setProfile(MemberProfileEntity profile) { this.profile = profile; }

    public static class BoardMemberResponseBuilder {
        private BoardMemberResponse instance = new BoardMemberResponse();
        public BoardMemberResponseBuilder id(Long id) { instance.setId(id); return this; }
        public BoardMemberResponseBuilder user(UserEntity user) { instance.setUser(user); return this; }
        public BoardMemberResponseBuilder role(BoardMemberEntity.Role role) { instance.setRole(role); return this; }
        public BoardMemberResponseBuilder profile(MemberProfileEntity profile) { instance.setProfile(profile); return this; }
        public BoardMemberResponse build() { return instance; }
    }
}
