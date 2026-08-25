package triplog.backend.fcmtoken.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import triplog.backend.fcmtoken.entity.FcmToken;
import triplog.backend.fcmtoken.exception.FcmTokenException;
import triplog.backend.fcmtoken.repository.FcmTokenRepository;
import triplog.backend.users.entity.Users;
import triplog.backend.users.service.UsersService;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static triplog.backend.fcmtoken.exception.FcmTokenErrorCode.FCM_TOKEN_ALREADY_REGISTERED;
import static triplog.backend.fcmtoken.exception.FcmTokenErrorCode.FCM_TOKEN_NOT_FOUND;
import triplog.backend.users.exception.UsersException;
import static triplog.backend.users.exception.UsersErrorCode.USER_NOT_FOUND;
import static triplog.backend.users.entity.LoginType.GOOGLE;

@ExtendWith(MockitoExtension.class)
class FcmTokenServiceImplTest {
    private static final String USERS_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String TOKEN = "fcm_device_token_string";
    @Mock private FcmTokenRepository fcmTokenRepository;
    @Mock private UsersService usersService;
    @Mock private ObjectProvider<FirebaseMessaging> firebaseMessagingProvider;
    private FcmTokenServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new FcmTokenServiceImpl(
                fcmTokenRepository,
                usersService,
                firebaseMessagingProvider
        );
    }

    @Test void registersFcmToken() {
        Users users = user();
        given(usersService.findById(USERS_ID)).willReturn(users);
        var response = service.register(USERS_ID, TOKEN, "ANDROID", "Galaxy Z Flip 5");
        assertThat(response.getIsRegistered()).isTrue();
        ArgumentCaptor<FcmToken> captor = ArgumentCaptor.forClass(FcmToken.class);
        verify(fcmTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getUsers()).isSameAs(users);
        assertThat(captor.getValue().getToken()).isEqualTo(TOKEN);
        assertThat(captor.getValue().getDeviceType()).isEqualTo("ANDROID");
        assertThat(captor.getValue().getDeviceName()).isEqualTo("Galaxy Z Flip 5");
    }

    @Test void rejectsUnknownUser() {
        given(usersService.findById(USERS_ID)).willThrow(new UsersException(USER_NOT_FOUND));
        assertThatThrownBy(() -> service.register(USERS_ID, TOKEN, "ANDROID", "Galaxy"))
                .isInstanceOf(UsersException.class).extracting("errorCode").isEqualTo(USER_NOT_FOUND);
        verify(fcmTokenRepository, never()).save(any());
    }

    @Test void rejectsDuplicateToken() {
        given(usersService.findById(USERS_ID)).willReturn(user());
        given(fcmTokenRepository.existsByToken(TOKEN)).willReturn(true);
        assertThatThrownBy(() -> service.register(USERS_ID, TOKEN, "ANDROID", "Galaxy"))
                .isInstanceOf(FcmTokenException.class).extracting("errorCode").isEqualTo(FCM_TOKEN_ALREADY_REGISTERED);
        verify(fcmTokenRepository, never()).save(any());
    }

    @Test void deletesFcmToken() {
        FcmToken fcmToken = new FcmToken(user(), TOKEN, "ANDROID", "Galaxy");
        given(fcmTokenRepository.findByUsersUsersIdAndToken(USERS_ID, TOKEN))
                .willReturn(Optional.of(fcmToken));

        var response = service.delete(USERS_ID, TOKEN);

        assertThat(response.getIsRegistered()).isFalse();
        verify(fcmTokenRepository).delete(fcmToken);
    }

    @Test void rejectsMissingFcmTokenOnDelete() {
        given(fcmTokenRepository.findByUsersUsersIdAndToken(USERS_ID, TOKEN))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(USERS_ID, TOKEN))
                .isInstanceOf(FcmTokenException.class)
                .extracting("errorCode")
                .isEqualTo(FCM_TOKEN_NOT_FOUND);
        verify(fcmTokenRepository, never()).delete(any());
    }

    private Users user() { return new Users(GOOGLE, "여행자", "profile.png", "user@test.com", null); }
}
