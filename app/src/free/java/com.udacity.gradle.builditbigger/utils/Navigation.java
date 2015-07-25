package com.udacity.gradle.builditbigger.utils;

import android.app.Activity;
import android.content.Intent;

import com.arbuleac.joketeller.JokeActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * @since 7/25/15.
 */
public class Navigation {

    private static Navigation sInstance;

    public static Navigation get() {
        if (sInstance == null) {
            synchronized (Navigation.class) {
                if (sInstance == null) {
                    sInstance = new Navigation();
                }
            }
        }
        return sInstance;
    }

    public void joke(final Activity activity, final String joke) {
        final InterstitialAd interstitialAd = new InterstitialAd(activity);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                goToJoke(activity, joke);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                goToJoke(activity, joke);
            }
        });
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        interstitialAd.loadAd(adRequest);
    }

    private void goToJoke(Activity activity, String joke) {
        Intent jokeIntent = new Intent(activity, JokeActivity.class);
        jokeIntent.putExtra(JokeActivity.EXTRA_JOKE, joke);
        activity.startActivity(jokeIntent);
    }
}
