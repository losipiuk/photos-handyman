package net.losipiuk.photoshandyman;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class PhotosLibraryClientFactory
{
    private static final String CLIENT_ID_RESOURCE_JSON = "/client_id.json";
    private static final String CREDENTIALS_DATA_STORE_PATH = System.getProperty("user.home") + "/.photoshandyman/credentials";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final int LOCAL_RECEIVER_PORT = 61984;

    private PhotosLibraryClientFactory() {}

    /** Creates a new {@link PhotosLibraryClient} instance with credentials and scopes. */
    public static PhotosLibraryClient createClient(
            List<String> selectedScopes)
    {
        PhotosLibrarySettings settings =
                null;
        try {
            settings = PhotosLibrarySettings.newBuilder()
                    .setCredentialsProvider(
                            FixedCredentialsProvider.create(
                                    getUserCredentials(selectedScopes)))
                    .build();
            return PhotosLibraryClient.initialize(settings);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Credentials getUserCredentials(List<String> selectedScopes)
            throws IOException, GeneralSecurityException {
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JSON_FACTORY, new InputStreamReader(PhotosLibraryClientFactory.class.getResourceAsStream(CLIENT_ID_RESOURCE_JSON), UTF_8));
        String clientId = clientSecrets.getDetails().getClientId();
        String clientSecret = clientSecrets.getDetails().getClientSecret();

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JSON_FACTORY,
                        clientSecrets,
                        selectedScopes)
                        .setDataStoreFactory(new FileDataStoreFactory(new File(PhotosLibraryClientFactory.CREDENTIALS_DATA_STORE_PATH)))
                        .setAccessType("offline")
                        .build();

        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(LOCAL_RECEIVER_PORT).build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(credential.getRefreshToken())
                .build();
    }
}
