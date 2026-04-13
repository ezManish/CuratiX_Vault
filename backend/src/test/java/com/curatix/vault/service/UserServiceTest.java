package com.curatix.vault.service;

import com.curatix.vault.dto.UserUpdateRequest;
import com.curatix.vault.entity.UserEntity;
import com.curatix.vault.exception.ResourceNotFoundException;
import com.curatix.vault.repository.UserRepository;
import com.curatix.vault.security.FirebasePrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock ApplicationContext context;
    @Mock MemberProfileService memberProfileService;

    @InjectMocks
    UserService userService;

    // ---------------------------------------------------------- syncUser
    @Nested
    @DisplayName("syncUser()")
    class SyncUser {

        @Test
        @DisplayName("creates a new user when Firebase uid is not in DB")
        void createsNewUser() {
            FirebasePrincipal principal = new FirebasePrincipal(
                    "uid-new", "new@example.com", "New User", "https://photo.url");

            when(userRepository.findByFirebaseUid("uid-new")).thenReturn(Optional.empty());
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UserEntity result = userService.syncUser(principal);

            assertThat(result.getFirebaseUid()).isEqualTo("uid-new");
            assertThat(result.getEmail()).isEqualTo("new@example.com");
            assertThat(result.getDisplayName()).isEqualTo("New User");
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("updates display name and photo when user already exists")
        void updatesExistingUser() {
            UserEntity existing = UserEntity.builder()
                    .id(1L)
                    .firebaseUid("uid-existing")
                    .email("existing@example.com")
                    .displayName("Old Name")
                    .photoUrl("https://old-photo.url")
                    .build();

            FirebasePrincipal principal = new FirebasePrincipal(
                    "uid-existing", "existing@example.com", "New Name", "https://new-photo.url");

            when(userRepository.findByFirebaseUid("uid-existing")).thenReturn(Optional.of(existing));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UserEntity result = userService.syncUser(principal);

            assertThat(result.getDisplayName()).isEqualTo("New Name");
            assertThat(result.getPhotoUrl()).isEqualTo("https://new-photo.url");
        }
    }

    // ---------------------------------------------------------- getByFirebaseUid
    @Nested
    @DisplayName("getByFirebaseUid()")
    class GetByFirebaseUid {

        @Test
        @DisplayName("returns user when found")
        void returnsUser() {
            UserEntity user = UserEntity.builder().firebaseUid("uid-1").build();
            when(userRepository.findByFirebaseUid("uid-1")).thenReturn(Optional.of(user));

            assertThat(userService.getByFirebaseUid("uid-1")).isSameAs(user);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void throwsWhenNotFound() {
            when(userRepository.findByFirebaseUid("uid-missing")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getByFirebaseUid("uid-missing"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found");
        }
    }

    // ---------------------------------------------------------- updateProfile
    @Nested
    @DisplayName("updateProfile()")
    class UpdateProfile {

        @Test
        @DisplayName("applies non-null fields and skips null fields")
        void appliesNonNullFields() {
            UserEntity user = UserEntity.builder()
                    .id(5L)
                    .firebaseUid("uid-5")
                    .email("user@test.com")
                    .displayName("Original")
                    .admissionNo(null)
                    .phone("9999999999")
                    .build();

            when(userRepository.findByFirebaseUid("uid-5")).thenReturn(Optional.of(user));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(context.getBean(MemberProfileService.class)).thenReturn(memberProfileService);

            UserUpdateRequest req = new UserUpdateRequest();
            req.setDisplayName("Updated Name");
            req.setAdmissionNo("ADM-001");
            // phone is null in request → should NOT override existing value

            UserEntity result = userService.updateProfile("uid-5", req);

            assertThat(result.getDisplayName()).isEqualTo("Updated Name");
            assertThat(result.getAdmissionNo()).isEqualTo("ADM-001");
            assertThat(result.getPhone()).isEqualTo("9999999999"); // untouched
        }

        @Test
        @DisplayName("syncs all member profiles after update")
        void triggersProfileSync() {
            UserEntity user = UserEntity.builder()
                    .id(7L).firebaseUid("uid-7").email("a@a.com").build();

            when(userRepository.findByFirebaseUid("uid-7")).thenReturn(Optional.of(user));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(context.getBean(MemberProfileService.class)).thenReturn(memberProfileService);

            userService.updateProfile("uid-7", new UserUpdateRequest());

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(memberProfileService).updateAllProfilesForUser(captor.capture());
            assertThat(captor.getValue().getId()).isEqualTo(7L);
        }
    }
}
