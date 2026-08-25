package triplog.backend.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Firebase Admin SDK와 FCM 발송 객체를 구성합니다.
 */
@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
public class FirebaseConfig {

    /**
     * 환경변수로 전달된 서비스 계정 파일을 사용하여 Firebase Messaging을 초기화합니다.
     *
     * @return 초기화된 Firebase Messaging 객체
     * @throws IOException 서비스 계정 파일을 읽을 수 없는 경우
     */
    @Bean
    public FirebaseMessaging firebaseMessaging(
            org.springframework.core.env.Environment environment
    ) throws IOException {
        String credentialsPath = environment.getRequiredProperty("firebase.credentials-path");
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new FileInputStream(credentialsPath)
        );
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp firebaseApp = FirebaseApp.getApps().isEmpty()
                ? FirebaseApp.initializeApp(options)
                : FirebaseApp.getInstance();
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
