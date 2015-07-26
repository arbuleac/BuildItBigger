package com.udacity.gradle.builditbigger.service;

import com.arbuleac.joketeller.api.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * @since 7/26/15.
 */
public class JokeService {
    private static JokeService sInstance;
    private final MyApi myApiService;

    public static JokeService get() {
        if (sInstance == null) {
            synchronized (JokeService.class) {
                if (sInstance == null) {
                    sInstance = new JokeService();
                }
            }
        }
        return sInstance;
    }

    public JokeService() {
        MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                //TODO replace this!!! I use Genymotion
                .setRootUrl("http://10.0.3.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        // end options for devappserver

        myApiService = builder.build();
    }

    public void loadJoke(JokeCallback callback) {
        try {
            String string = myApiService.tellJoke().execute().getData();
            //TODO This is for testing progress bar!
            Thread.sleep(1000);
            callback.onNext(string);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    public interface JokeCallback {
        void onNext(String joke);

        void onError(Throwable e);
    }
}
