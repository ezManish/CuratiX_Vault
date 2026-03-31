package com.curatix.vault.dto;

import com.curatix.vault.entity.BoardMemberEntity;
import com.curatix.vault.entity.MemberProfileEntity;
import com.curatix.vault.entity.UserEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardMemberResponse {
    private Long id;
    private UserEntity user;
    private BoardMemberEntity.Role role;
    private MemberProfileEntity profile;
}
