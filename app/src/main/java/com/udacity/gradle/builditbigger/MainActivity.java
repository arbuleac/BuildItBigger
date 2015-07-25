package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arbuleac.joketeller.JokeActivity;
import com.arbuleac.joketeller.api.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private MyApi myApiService = null;
    private Observable<String> observable;
    private Subscription subscription;
    private ProgressBar loadingPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingPb = (ProgressBar) findViewById(R.id.loading_pb);
        observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onStart();
                }
                if (myApiService == null) {  // Only do this once
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
                try {
                    String string = myApiService.tellJoke().execute().getData();
                    Thread.sleep(5000);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(string);
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingPb.setVisibility(View.GONE);
        if (subscription == null || subscription.isUnsubscribed()) {
            return;
        }
        subscription.unsubscribe();
    }

    public void tellJoke(final View view) {
        subscription = observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onStart() {
                        loadingPb.setVisibility(View.VISIBLE);
                        view.setEnabled(false);
                        setProgressBarIndeterminateVisibility(true);
                    }

                    @Override
                    public void onCompleted() {
                        loadingPb.setVisibility(View.GONE);
                        view.setEnabled(true);
                        setProgressBarIndeterminateVisibility(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, getString(R.string.error_get_joke), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String s) {
                        Intent jokeIntent = new Intent(MainActivity.this, JokeActivity.class);
                        jokeIntent.putExtra(JokeActivity.EXTRA_JOKE, s);
                        startActivity(jokeIntent);
                    }
                });
    }
}