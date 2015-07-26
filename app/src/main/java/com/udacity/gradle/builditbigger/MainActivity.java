package com.udacity.gradle.builditbigger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arbuleac.joketeller.api.myApi.MyApi;
import com.udacity.gradle.builditbigger.service.JokeService;
import com.udacity.gradle.builditbigger.utils.Navigation;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

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
            public void call(final Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onStart();
                }
                JokeService.get().loadJoke(new JokeService.JokeCallback() {
                    @Override
                    public void onNext(String joke) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(joke);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(e);
                        }
                    }
                });
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
                        loadingPb.setVisibility(View.GONE);
                        view.setEnabled(true);
                    }

                    @Override
                    public void onNext(String s) {
                        Navigation.get().joke(MainActivity.this, s);
                    }
                });
    }
}