package flo.no.kanji.ai.gemini.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleAuthenticator {

    private final GoogleCredentials credentials;

    @Autowired
    public GoogleAuthenticator(Environment env) {
        this.credentials = UserCredentials.newBuilder()
                .setClientId(env.getProperty("gcloud.auth.clientId", ""))
                .setClientSecret(env.getProperty("gcloud.auth.clientSecret", ""))
                .setRefreshToken(env.getProperty("gcloud.auth.refreshToken", ""))
                .setQuotaProjectId(env.getProperty("gcloud.auth.quotaProjectId", ""))
                .setUniverseDomain("googleapis.com")
                .build();
    }

    public String getAccessToken() {
        try {
            credentials.refreshIfExpired();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return credentials.getAccessToken() != null ? credentials.getAccessToken().getTokenValue() : null;
    }
}
